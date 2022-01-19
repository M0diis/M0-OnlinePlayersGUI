package me.m0dii.onlineplayersgui.inventoryholder;

import me.m0dii.onlineplayersgui.CustomItem;
import me.m0dii.onlineplayersgui.OnlineGUI;
import me.m0dii.onlineplayersgui.utils.Config;
import me.m0dii.onlineplayersgui.utils.Messenger;
import me.m0dii.onlineplayersgui.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
import java.util.Map;
import java.util.stream.Collectors;

public class OnlineGUIInventory implements InventoryHolder, CustomGUI
{
    private Inventory inv;
    private final String name;
    private final int size, page;
    private final OnlineGUI plugin;

    public OnlineGUIInventory(OnlineGUI plugin, String name, int page, Player p)
    {
        this.size = this.adjustSize(plugin.getCfg());
        
        this.name = name;
        this.page = page;
        
        this.plugin = plugin;
        
        this.inv = Bukkit.createInventory(this, this.size, name);
    
        setCustomItems(p);
        
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
                    cmds = this.plugin.getCfg().LEFT_CLICK_CMDS();
    
                if(clickType.equals(ClickType.MIDDLE))
                    cmds = this.plugin.getCfg().MIDDLE_CLICK_CMDS();
    
                if(clickType.equals(ClickType.RIGHT))
                    cmds = this.plugin.getCfg().RIGHT_CLICK_CMDS();
                
                for(String cmd : cmds)
                    Utils.sendCommand(clickee, skullOwner, cmd);
            }
    
            if(clickType.equals(ClickType.LEFT) && this.plugin.getCfg().LEFT_CLICK_CMDS().contains("[CLOSE]"))
                clickee.closeInventory();
    
            if(clickType.equals(ClickType.MIDDLE) && this.plugin.getCfg().MIDDLE_CLICK_CMDS().contains("[CLOSE]"))
                clickee.closeInventory();
            
            if(clickType.equals(ClickType.RIGHT) && this.plugin.getCfg().RIGHT_CLICK_CMDS().contains("[CLOSE]"))
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
                    OnlineGUIInventory newinv = new OnlineGUIInventory(this.plugin, this.name, nextPage, clickee);
                    
                    if(newinv.hasPlayers())
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
                
                    CustomItem c = getCustomItemBySlot(slot);
                
                    if(c == null)
                    {
                        return;
                    }
                    
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
    
    private CustomItem getCustomItemBySlot(int slot)
    {
        return plugin.getCfg().getCustomItems().getOrDefault(slot, null);
    }
    
    public void setCustomItems(Player p)
    {
        plugin.getGuiUtils().setCustomItems(inv, p, plugin.getCfg().getCustomItems());
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
        this.inv = Bukkit.createInventory(this, this.size, this.name);
        
        initByPage(this.page);
        setCustomItems(p);
        
        p.openInventory(this.inv);
    }
    
    public boolean hasPlayers()
    {
        return getByPage(page).size() != 0;
    }
    
    public boolean hasPlayers(int offset)
    {
        return getByPage(page + offset).size() != 0;
    }
    
    private void initByPage(int page)
    {
        setButtons();
        
        List<Player> byPage = getByPage(page);
        
        for(Player player : byPage)
        {
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
    
            ItemMeta meta = head.getItemMeta();
        
            List<String> lore = plugin.getCfg().HEAD_LORE()
                    .stream()
                    .map(str -> Utils.setPlaceholders(str, player))
                    .collect(Collectors.toList());
        
            meta.setDisplayName(Utils.setPlaceholders(plugin.getCfg().HEAD_DISPLAY_NAME(), player));
        
            meta.setLore(lore);
        
            SkullMeta sm = (SkullMeta)meta;
        
            sm.setOwningPlayer(player);
            head.setItemMeta(sm);
            
            for(int i = 0; i < inv.getSize(); i++)
            {
                if(inv.getItem(i) == null)
                {
                    if(plugin.getCfg().NEXT_PAGE_SLOT() != i &&
                       plugin.getCfg().PREV_PAGE_SLOT() != i)
                    {
                        inv.setItem(i, head);
                    }
                }
            }
    
        }
    }
    
    @NotNull
    private List<Player> getByPage(int page)
    {
        String permission = plugin.getCfg().isPERMISSION_REQUIRED() ? plugin.getCfg().getREQUIRED_PERMISSION()
                : null;
    
        String condition = plugin.getCfg().isCONDITION_REQUIRED() ? plugin.getCfg().getCONDITION()
                : null;
        
        List<Player> online = plugin.getGuiUtils().getOnline(permission, condition);
        
        List<Player> byPage = new ArrayList<>();
        
        int availableSlots = this.size - 9;
    
        for(Map.Entry<Integer, CustomItem> entry : plugin.getCfg()
                .getCustomItems()
                .entrySet())
        {
            if(entry.getKey() >= this.size - 9)
            {
                continue;
            }
            
            availableSlots--;
        }
        
        if(plugin.getCfg().NEXT_PAGE_SLOT() < this.size - 9)
            availableSlots--;
        
        if(plugin.getCfg().PREV_PAGE_SLOT() < this.size - 9)
            availableSlots--;
    
        int lowBound = availableSlots * page;
        int highBound = availableSlots * (page == 0 ? 1 : page + 1);
        
        for(int i = lowBound; i < highBound; i++)
            if(lowBound < online.size() && i < online.size())
                byPage.add(online.get(i));
 
        return byPage;
    }
    
    private void setButtons()
    {
        boolean show = plugin.getCfg().ALWAYS_SHOW_BUTTONS();
        
        if(show)
            setNextButton();
        else if(hasPlayers(1))
            setNextButton();
    
        if(show)
            setPreviousButton();
        else if(page != 0)
            setPreviousButton();
    }
    
    private void setNextButton()
    {
        ItemStack nextButton = new ItemStack(plugin.getCfg().NEXT_PAGE_MATERIAL());
        ItemMeta nextButtonMeta = nextButton.getItemMeta();
    
        List<String> nextLore = plugin.getCfg().NEXT_PAGE_LORE().stream()
                .map(str -> Utils.setPlaceholders(str, null))
                .collect(Collectors.toList());
    
        nextButtonMeta.setLore(nextLore);
    
        nextButtonMeta.getPersistentDataContainer().set(
                new NamespacedKey(plugin, "Button"),
                PersistentDataType.STRING, "Next");
        
        nextButtonMeta.setDisplayName(plugin.getCfg().NEXT_PAGE_BUTTON_NAME());
    
        nextButton.setItemMeta(nextButtonMeta);
    
        inv.setItem(plugin.getCfg().NEXT_PAGE_SLOT(), nextButton);
    }
    
    private void setPreviousButton()
    {
        ItemStack prevButton = new ItemStack(plugin.getCfg().PREV_PAGE_MATERIAL());
        ItemMeta prevButtonMeta = prevButton.getItemMeta();
        
        List<String> prevLore = plugin.getCfg().PREV_PAGE_LORE().stream()
                .map(str -> Utils.setPlaceholders(str, null))
                .collect(Collectors.toList());
    
        prevButtonMeta.setLore(prevLore);
    
        prevButtonMeta.getPersistentDataContainer().set(
                new NamespacedKey(plugin, "Button"),
                PersistentDataType.STRING, "Previous");
    
        prevButtonMeta.setDisplayName(Utils.format(plugin.getCfg().PREV_PAGE_BUTTON_NAME()));
        prevButton.setItemMeta(prevButtonMeta);
    
        inv.setItem(plugin.getCfg().PREV_PAGE_SLOT(), prevButton);
    }
}
