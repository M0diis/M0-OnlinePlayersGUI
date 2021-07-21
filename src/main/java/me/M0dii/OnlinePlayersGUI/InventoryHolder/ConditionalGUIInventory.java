package me.M0dii.OnlinePlayersGUI.InventoryHolder;

import me.M0dii.OnlinePlayersGUI.Utils.ConditionalConfig;
import me.M0dii.OnlinePlayersGUI.CustomItem;
import me.M0dii.OnlinePlayersGUI.OnlineGUI;
import me.M0dii.OnlinePlayersGUI.Utils.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConditionalGUIInventory implements InventoryHolder, CustomGUI
{
    private Inventory inv;
    private final String name;
    private final int size, page;
    private final OnlineGUI plugin;
    
    private String condition;
    
    private final ConditionalConfig cfg;
    private final FileConfiguration fileCfg;
    
    public ConditionalGUIInventory(OnlineGUI plugin, String name, int page, FileConfiguration cfg)
    {
        this.cfg = new ConditionalConfig(plugin, cfg);
        
        this.size = this.adjustSize();
        
        this.fileCfg = cfg;
        
        this.name = name;
        this.page = page;
        
        this.plugin = plugin;
        
        this.condition = this.cfg.getCondition();
        
        this.inv = Bukkit.createInventory(this, this.size, Component.text(name));
        
        initByPage(page);
    }
    
    private int displayedHeads = 0;
    
    public void execute(Player clickee, ItemStack clickedItem, boolean left)
    {
        if(clickedItem != null && clickedItem.getType().equals(Material.PLAYER_HEAD))
        {
            SkullMeta sm = (SkullMeta)clickedItem.getItemMeta();
    
            Player skullOwner = sm.getOwningPlayer() != null ? sm.getOwningPlayer().getPlayer() : null;
    
            if(skullOwner == null)
            {
                String owner = sm.getOwner();
                
                if(owner != null)
                    skullOwner = Bukkit.getPlayer(owner);
            }
    
            if(skullOwner != null)
            {
                for(String cmd : left ? this.cfg.LEFT_CLICK_CMDS() :
                        this.cfg.RIGHT_CLICK_CMDS())
                    Utils.sendCommand(clickee, skullOwner, cmd);
            }
            
            if(left && this.cfg.CLOSE_ON_LEFT_CLICK())
                clickee.closeInventory();

            if(!left && this.cfg.CLOSE_ON_RIGHT_CLICK())
                clickee.closeInventory();
        }
    
        if((clickedItem != null) &&
            (clickedItem.getType().equals(this.cfg.PREV_PAGE_MATERIAL())
            || clickedItem.getType().equals(this.cfg.NEXT_PAGE_MATERIAL())))
        {
            NamespacedKey key = new NamespacedKey(this.plugin, "Button");
            PersistentDataContainer cont = clickedItem.getItemMeta().getPersistentDataContainer();
    
            if(cont.has(key, PersistentDataType.STRING))
            {
                String buttonType = cont.get(key, PersistentDataType.STRING);
    
                int nextPage = page;
    
                if(buttonType == null) return;
    
                if(buttonType.equalsIgnoreCase("Next")) nextPage = page + 1;
                else if(buttonType.equalsIgnoreCase("Previous")) nextPage = page - 1;
    
                try
                {
                    ConditionalGUIInventory newinv = new ConditionalGUIInventory(this.plugin, this.name, nextPage, fileCfg);
                    
                    if(newinv.displayedHeads != 0)
                        clickee.openInventory(newinv.getInventory());
                }
                catch(IndexOutOfBoundsException ex)
                {
                    // TODO
                    // Logger?
                }
            }
        }
    
        if(clickedItem != null)
        {
            NamespacedKey key = new NamespacedKey(this.plugin, "IsCustom");
            PersistentDataContainer cont = clickedItem.getItemMeta()
                    .getPersistentDataContainer();
        
            if(cont.has(key, PersistentDataType.STRING))
            {
                key = new NamespacedKey(this.plugin, "Slot");
            
                if(cont.has(key, PersistentDataType.INTEGER))
                {
                    //noinspection ConstantConditions
                    int slot = cont.get(key, PersistentDataType.INTEGER);
                
                    CustomItem c = this.getCustomItemBySlot(slot);
                
                    if(c != null)
                    {
                        List<String> cicmds = new ArrayList<>();
                    
                        boolean close = false;
                    
                        if(left)
                        {
                            cicmds = c.getLCC();
                        
                            if(c.closeOnLeft())
                                close = true;
                        }
                    
                        if(!left)
                        {
                            cicmds = c.getRCC();
                        
                            if(c.closeOnRight())
                                close = true;
                        }
                    
                        cicmds.forEach(cmd -> Utils.sendCommand(clickee, clickee, cmd));
                    
                        if(close)
                            clickee.closeInventory();
                    }
                }
            }
        }
    }
    
    public void refresh(Player p)
    {
        this.inv = Bukkit.createInventory(this, this.size, Component.text(this.name));
    
        this.initByPage(this.page);
        this.setCustomItems(p);
    
        p.openInventory(this.inv);
    }
    
    private CustomItem getCustomItemBySlot(int slot)
    {
        List<CustomItem> customItems = this.cfg.getCustomItems();
        
        CustomItem custom = null;
        
        for(CustomItem c : customItems)
            if(c.getItemSlot() == slot)
                custom = c;
        
        return custom;
    }
    
    public void setCustomItems(Player p)
    {
        new GUIUtils().setCustomItems(inv, p, size, cfg.getCustomItems());
    }
    
    private int adjustSize()
    {
        int size = cfg.GUI_SIZE();
    
        if (size < 18)
            return 18;
        else if (size > 54)
            return 54;
        else if(size % 9 == 0)
            return size;
        
        return 54;
    }
    
    @Override
    public @NotNull Inventory getInventory()
    {
        return inv;
    }
    
    private void initByPage(int page)
    {
        List<Player> online = Bukkit.getOnlinePlayers().stream().filter(p ->
                !p.hasPermission("m0onlinegui.hidden"))
                .collect(Collectors.toList());
        
        online = new GUIUtils().filterByCondition(online, this.condition);
    
        List<Player> byPage = new ArrayList<>();
    
        int lowBound = (this.size - 9) * page;
        int highBound = (this.size - 9) * (page == 0 ? 1 : page + 1);
    
        for(int i = lowBound; i < highBound; i++)
        {
            if(lowBound < online.size() && i < online.size())
                byPage.add(online.get(i));
        }
    
        displayedHeads = byPage.size();
        
        int curr = 0;
    
        for(int slot = 0; slot < byPage.size(); slot++)
        {
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        
            Player p = byPage.get(curr);
            
            ItemMeta meta = head.getItemMeta();
        
            List<String> lore = this.cfg.HEAD_LORE().stream().map(s -> Utils.format(PlaceholderAPI.setPlaceholders(p, s)))
                    .collect(Collectors.toList());
    
            meta.displayName(
                    Component.text(Utils.format(PlaceholderAPI.setPlaceholders(p, this.cfg
                            .HEAD_DISPLAY_NAME()))));
        
            meta.setLore(lore);
        
            SkullMeta sm = (SkullMeta)meta;
            
            sm.setOwningPlayer(p);
            head.setItemMeta(sm);
        
            inv.setItem(slot, head);
        
            curr++;
        }
        
        setButtons();
    }
    
    private void setButtons()
    {
        if(!this.cfg.HIDE_BUTTONS_SINGLE_PAGE())
        {
            ItemStack nextButton = new ItemStack(this.cfg.NEXT_PAGE_MATERIAL());
            ItemMeta nextButtonMeta = nextButton.getItemMeta();
        
            List<String> nextLore = this.cfg.NEXT_PAGE_LORE().stream().map(Utils::format)
                    .collect(Collectors.toList());
        
            nextButtonMeta.setLore(nextLore);
        
            nextButtonMeta.getPersistentDataContainer().set(
                    new NamespacedKey(plugin, "Button"),
                    PersistentDataType.STRING, "Next");
        
            nextButtonMeta.displayName(Component.text(this.cfg.NEXT_PAGE_BUTTON_NAME()));
            nextButton.setItemMeta(nextButtonMeta);
        
            inv.setItem(size - 4, nextButton);
            
            ItemStack prevButton = new ItemStack(this.cfg.PREV_PAGE_MATERIAL());
            ItemMeta prevButtonMeta = prevButton.getItemMeta();
        
            List<String> prevLore = this.cfg.PREV_PAGE_LORE().stream().map(Utils::format)
                    .collect(Collectors.toList());
        
            prevButtonMeta.setLore(prevLore);
        
            prevButtonMeta.getPersistentDataContainer().set(
                    new NamespacedKey(plugin, "Button"),
                    PersistentDataType.STRING, "Previous");
        
            prevButtonMeta.displayName(Component.text(this.cfg.PREV_PAGE_BUTTON_NAME()));
            prevButton.setItemMeta(prevButtonMeta);
        
            inv.setItem(size - 6, prevButton);
        }
    }
}
