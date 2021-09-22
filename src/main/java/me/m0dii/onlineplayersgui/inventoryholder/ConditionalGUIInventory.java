package me.m0dii.onlineplayersgui.inventoryholder;

import me.m0dii.onlineplayersgui.utils.ConditionalConfig;
import me.m0dii.onlineplayersgui.CustomItem;
import me.m0dii.onlineplayersgui.OnlineGUI;
import me.m0dii.onlineplayersgui.utils.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConditionalGUIInventory implements InventoryHolder, CustomGUI
{
    private Inventory inv;
    private final String name;
    private final int size, page;
    private final OnlineGUI plugin;
    
    private final String condition;
    
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
        
        this.inv = Bukkit.createInventory(this, this.size,
                Utils.format(this.cfg.getGUI_TITLE()));
        
        initByPage(page);
    }
    
    public void execute(Player clickee, ItemStack clickedItem, ClickType clickType)
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
                List<String> cmds = new ArrayList<>();
        
                if(clickType.equals(ClickType.LEFT))
                    cmds = this.cfg.LEFT_CLICK_CMDS();
    
                if(clickType.equals(ClickType.MIDDLE))
                    cmds = this.cfg.MIDDLE_CLICK_CMDS();
        
                if(clickType.equals(ClickType.RIGHT))
                    cmds = this.cfg.RIGHT_CLICK_CMDS();
        
                for(String cmd : cmds)
                    Utils.sendCommand(clickee, skullOwner, cmd);
            }
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
                    
                    if(newinv.hasNextPage())
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
                        
                        if(clickType.equals(ClickType.LEFT))
                            cicmds = c.getLCC();
    
                        if(clickType.equals(ClickType.MIDDLE))
                            cicmds = c.getMCC();
                        
                        if(clickType.equals(ClickType.RIGHT))
                            cicmds = c.getRCC();
                    
                        cicmds.forEach(cmd -> Utils.sendCommand(clickee, clickee, cmd));
                        
                        if(cicmds.contains("[CLOSE]"))
                            clickee.closeInventory();
                    }
                }
            }
        }
    }
    
    public void refresh(Player p)
    {
        this.inv = Bukkit.createInventory(this, this.size, this.name);
    
        initByPage(this.page);
        setCustomItems(p);
    
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
        plugin.getGuiUtils().setCustomItems(inv, p, size, cfg.getCustomItems());
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
    
    @NotNull
    private List<Player> getByPage(int page)
    {
        String permission = cfg.isPERMISSION_REQUIRED() ? cfg.getREQUIRED_PERMISSION() : null;
        
        List<Player> online = plugin.getGuiUtils().getOnline(permission, this.condition);
        
        List<Player> byPage = new ArrayList<>();
        
        int lowBound = (this.size - 9) * page;
        int highBound = (this.size - 9) * (page == 0 ? 1 : page + 1);
        
        for(int i = lowBound; i < highBound; i++)
            if(lowBound < online.size() && i < online.size())
                byPage.add(online.get(i));
        
        return byPage;
    }
    
    private void initByPage(int page)
    {
        List<Player> byPage = getByPage(page);
        
        int curr = 0;
    
        for(int slot = 0; slot < byPage.size(); slot++)
        {
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        
            Player p = byPage.get(curr);
            
            ItemMeta meta = head.getItemMeta();
        
            List<String> lore = cfg.HEAD_LORE().stream()
                    .map(str -> Utils.format(PlaceholderAPI.setPlaceholders(p, str)))
                    .collect(Collectors.toList());
    
            meta.setDisplayName(Utils.format(PlaceholderAPI.setPlaceholders(p, this.cfg
                            .HEAD_DISPLAY_NAME())));
        
            meta.setLore(lore);
        
            SkullMeta sm = (SkullMeta)meta;
            
            sm.setOwningPlayer(p);
            head.setItemMeta(sm);
        
            inv.setItem(slot, head);
        
            curr++;
        }
        
        setButtons();
    }
    
    public boolean hasNextPage()
    {
        return getByPage(page + 1).size() != 0;
    }
    
    private void setButtons()
    {
        boolean show = plugin.getCfg().ALWAYS_SHOW_BUTTONS();
        
        if(show)
            setNextButton();
        else if(hasNextPage())
            setNextButton();
        
        if(show)
            setPreviousButton();
        else if(page != 0)
            setPreviousButton();
    }
    
    private void setNextButton()
    {
        ItemStack nextButton = new ItemStack(cfg.NEXT_PAGE_MATERIAL());
        ItemMeta nextButtonMeta = nextButton.getItemMeta();
        
        List<String> nextLore = cfg.NEXT_PAGE_LORE().stream()
                .map(Utils::format)
                .map(str -> PlaceholderAPI.setPlaceholders(null, str))
                .collect(Collectors.toList());
        
        nextButtonMeta.setLore(nextLore);
        
        nextButtonMeta.getPersistentDataContainer().set(
                new NamespacedKey(plugin, "Button"),
                PersistentDataType.STRING, "Next");
        
        nextButtonMeta.setDisplayName(Utils.format(cfg.NEXT_PAGE_BUTTON_NAME()));
        
        nextButton.setItemMeta(nextButtonMeta);
        
        inv.setItem(size - 4, nextButton);
    }
    
    private void setPreviousButton()
    {
        ItemStack prevButton = new ItemStack(cfg.PREV_PAGE_MATERIAL());
        ItemMeta prevButtonMeta = prevButton.getItemMeta();
        
        List<String> prevLore = cfg.PREV_PAGE_LORE().stream()
                .map(Utils::format)
                .map(str -> PlaceholderAPI.setPlaceholders(null, str))
                .collect(Collectors.toList());
        
        prevButtonMeta.setLore(prevLore);
        
        prevButtonMeta.getPersistentDataContainer().set(
                new NamespacedKey(plugin, "Button"),
                PersistentDataType.STRING, "Previous");
        
        prevButtonMeta.setDisplayName(Utils.format(cfg.PREV_PAGE_BUTTON_NAME()));
        prevButton.setItemMeta(prevButtonMeta);
        
        inv.setItem(size - 6, prevButton);
    }
}
