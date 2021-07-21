package me.M0dii.OnlinePlayersGUI.InventoryHolder;

import me.M0dii.OnlinePlayersGUI.CustomItem;
import me.M0dii.OnlinePlayersGUI.OnlineGUI;
import me.M0dii.OnlinePlayersGUI.Utils.Config;
import me.M0dii.OnlinePlayersGUI.Utils.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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

public class OnlineGUIInventory implements InventoryHolder, CustomGUI
{
    private Inventory inv;
    private final String name;
    private final int size, page;
    private final OnlineGUI plugin;
    private int displayedHeads = 0;
    
    public OnlineGUIInventory(OnlineGUI plugin, String name, int page)
    {
        this.size = this.adjustSize(plugin.getCfg());
        
        this.name = name;
        this.page = page;
        
        this.plugin = plugin;
    
        this.inv = Bukkit.createInventory(this, this.size, Component.text(name));
        
        initByPage(page);
    }
    
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
                for(String cmd : left ? this.plugin.getCfg().LEFT_CLICK_CMDS() :
                        this.plugin.getCfg().RIGHT_CLICK_CMDS())
                    Utils.sendCommand(clickee, skullOwner, cmd);
            }
            
            if(left && this.plugin.getCfg().CLOSE_ON_LEFT_CLICK())
                clickee.closeInventory();

            if(!left && this.plugin.getCfg().CLOSE_ON_RIGHT_CLICK())
                clickee.closeInventory();
        }
    
        if((clickedItem != null) &&
        (clickedItem.getType().equals(this.plugin.getCfg().PREV_PAGE_MATERIAL())
        || clickedItem.getType().equals(this.plugin.getCfg().NEXT_PAGE_MATERIAL())))
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
                    OnlineGUIInventory newinv = new OnlineGUIInventory(this.plugin, this.name, nextPage);
                    newinv.setCustomItems(clickee);

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
    
    private CustomItem getCustomItemBySlot(int slot)
    {
        List<CustomItem> customItems = this.plugin.getCfg().getCustomItems();
        
        CustomItem custom = null;
        
        for(CustomItem c : customItems)
            if(c.getItemSlot() == slot)
                custom = c;
        
        return custom;
    }
    
    public void setCustomItems(Player p)
    {
        List<CustomItem> customItems = this.plugin.getCfg().getCustomItems();
    
        for(CustomItem c : customItems)
        {
            ItemStack item = c.getItem();
            ItemMeta m = item.getItemMeta();
        
            NamespacedKey key = new NamespacedKey(this.plugin, "Slot");
            PersistentDataContainer cont = item.getItemMeta().getPersistentDataContainer();
        
            if(cont.has(key, PersistentDataType.INTEGER))
            {
                //noinspection ConstantConditions
                int slot = cont.get(key, PersistentDataType.INTEGER);
            
                List<String> lore = c.getLore();
            
                List<String> newLore = new ArrayList<>();
            
                for(String l : lore)
                    newLore.add(PlaceholderAPI.setPlaceholders(p, l));
            
                m.setLore(newLore);
            
                item.setItemMeta(m);
            
                inv.setItem(this.size - 10 + slot, item);
            }
        }
    }
    
    private int adjustSize(Config cfg)
    {
        int size = cfg.GUI_SIZE();
        
        if(size % 9 == 0)
            return size;
        else if (size < 18)
            return 18;
        else if (size > 54)
            return 54;
        
        return 54;
    }
    
    @Override
    public @NotNull Inventory getInventory()
    {
        return inv;
    }
    
    public void refresh(Player p)
    {
        this.inv = Bukkit.createInventory(this, this.size, Component.text(this.name));
        
        this.initByPage(this.page);
        this.setCustomItems(p);
        
        p.openInventory(this.inv);
    }
    
    private List<Player> getOnline(boolean hook)
    {
        List<Player> online;
        
        List<Player> toggled = plugin.getHiddenPlayersToggled();
        
        if(hook)
        {
            online = Bukkit.getOnlinePlayers().stream().filter(p ->
                    !p.hasPermission("m0onlinegui.hidden")
                            || !plugin.getEssentials().getUser(p).isVanished()
                            || !toggled.contains(p))
                    .collect(Collectors.toList());
        }
        else
        {
            online = Bukkit.getOnlinePlayers().stream().filter(p ->
                    !p.hasPermission("m0onlinegui.hidden")
                            || !toggled.contains(p))
                    .collect(Collectors.toList());
        }
        
        return plugin.getCfg().isConditionEnabled() ?
                Utils.filterByCondition(online, plugin.getCfg().getCondition()) : online;
    }
    
    private void initByPage(int page)
    {
        List<Player> online = getOnline(plugin.getCfg().ESSX_HOOK());
    
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
        
            List<String> lore = new ArrayList<>();
        
            for(String s : plugin.getCfg().HEAD_LORE())
                lore.add(Utils.format(PlaceholderAPI.setPlaceholders(p, s)));

            meta.displayName(Component.text(Utils.format(
                    PlaceholderAPI.setPlaceholders(p, plugin.getCfg()
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
        if(!plugin.getCfg().HIDE_BUTTONS_SINGLE_PAGE())
        {
            ItemStack nextButton = new ItemStack(plugin.getCfg().NEXT_PAGE_MATERIAL());
            ItemMeta nextButtonMeta = nextButton.getItemMeta();
        
            List<String> nextLore = plugin.getCfg().NEXT_PAGE_LORE().stream().map(Utils::format)
                    .collect(Collectors.toList());
        
            nextButtonMeta.setLore(nextLore);
        
            nextButtonMeta.getPersistentDataContainer().set(
                    new NamespacedKey(plugin, "Button"),
                    PersistentDataType.STRING, "Next");
            
            nextButtonMeta.displayName(Component.text(plugin.getCfg().NEXT_PAGE_BUTTON_NAME()));
            
            nextButton.setItemMeta(nextButtonMeta);
        
            inv.setItem(plugin.getCfg().GUI_SIZE() - 4, nextButton);
            
            ItemStack prevButton = new ItemStack(plugin.getCfg().PREV_PAGE_MATERIAL());
            ItemMeta prevButtonMeta = prevButton.getItemMeta();
        
            List<String> prevLore = plugin.getCfg().PREV_PAGE_LORE().stream().map(Utils::format)
                    .collect(Collectors.toList());
        
            prevButtonMeta.setLore(prevLore);
        
            prevButtonMeta.getPersistentDataContainer().set(
                    new NamespacedKey(plugin, "Button"),
                    PersistentDataType.STRING, "Previous");
        
            prevButtonMeta.displayName(Component.text(plugin.getCfg().PREV_PAGE_BUTTON_NAME()));
            prevButton.setItemMeta(prevButtonMeta);
        
            inv.setItem(plugin.getCfg().GUI_SIZE() - 6, prevButton);
        }
    }
}
