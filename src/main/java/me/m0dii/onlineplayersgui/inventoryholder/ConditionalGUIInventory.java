package me.m0dii.onlineplayersgui.inventoryholder;

import me.m0dii.onlineplayersgui.CustomItem;
import me.m0dii.onlineplayersgui.OnlineGUI;
import me.m0dii.onlineplayersgui.utils.ConditionalConfig;
import me.m0dii.onlineplayersgui.utils.Messenger;
import me.m0dii.onlineplayersgui.utils.Utils;
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
import java.util.Map;
import java.util.stream.Collectors;

public class ConditionalGUIInventory implements InventoryHolder, CustomGUI
{
    private Inventory inv;
    private final String name;
    private final int size, page;
    private final OnlineGUI plugin;
    
    private final List<Integer> customItemSlots;
    
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
                Utils.format(this.cfg.getGuiTitle()));
        
        this.customItemSlots = plugin.getCfg().getCustomItemSlots();
        
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
                    cmds = this.cfg.getLeftClickCmds();
    
                if(clickType.equals(ClickType.MIDDLE))
                    cmds = this.cfg.getMiddleClickCmds();
        
                if(clickType.equals(ClickType.RIGHT))
                    cmds = this.cfg.getRightClickCmds();
        
                for(String cmd : cmds)
                    Utils.sendCommand(clickee, skullOwner, cmd);
            }
        }
    
        if((clickedItem != null) &&
            (clickedItem.getType().equals(this.cfg.getPrevPageMat())
            || clickedItem.getType().equals(this.cfg.getNextPageMat())))
        {
            NamespacedKey key = new NamespacedKey(this.plugin, "Button");
            PersistentDataContainer cont = clickedItem.getItemMeta().getPersistentDataContainer();
    
            if(cont.has(key, PersistentDataType.STRING))
            {
                String buttonType = cont.get(key, PersistentDataType.STRING);
    
                if(buttonType == null) return;
    
                int nextPage = page;
                
                if(buttonType.equalsIgnoreCase("Next")) nextPage = page + 1;
                else if(buttonType.equalsIgnoreCase("Previous")) nextPage = page - 1;
    
                try
                {
                    ConditionalGUIInventory newinv = new ConditionalGUIInventory(this.plugin, this.name, nextPage, fileCfg);
                    
                    if(newinv.hasPlayers())
                        clickee.openInventory(newinv.getInventory());
                }
                catch(IndexOutOfBoundsException ex)
                {
                    Messenger.debug("IndexOutOfBoundsException: " + ex.getMessage());
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
    
    public void refresh(Player p)
    {
        this.inv = Bukkit.createInventory(this, this.size, this.name);
    
        initByPage(this.page);
        setCustomItems(p);
    
        p.openInventory(this.inv);
    }
    
    private CustomItem getCustomItemBySlot(int slot)
    {
        return cfg.getCustomItems().getOrDefault(slot, null);
    }
    
    public void setCustomItems(Player p)
    {
        plugin.getGuiUtils().setCustomItems(inv, p, cfg.getCustomItems());
    }
    
    private int adjustSize()
    {
        int size = cfg.getGuiSize();
    
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
        String permission = cfg.isPermissionRequired() ? cfg.getREQUIRED_PERMISSION() : null;
        
        List<Player> online = plugin.getGuiUtils().getOnline(permission, condition);
        
        List<Player> byPage = new ArrayList<>();
    
        int availableSlots = this.size - 9;
    
        for(Map.Entry<Integer, CustomItem> entry : cfg.getCustomItems()
                                                    .entrySet())
        {
            if(entry.getKey() >= this.size - 9)
            {
                continue;
            }
        
            availableSlots--;
        }
    
        if(cfg.getNextPageSlot() < this.size - 9)
            availableSlots--;
    
        if(cfg.getPrevPageSlot() < this.size - 9)
            availableSlots--;
    
        int lowBound = availableSlots * page;
        int highBound = availableSlots * (page == 0 ? 1 : page + 1);
        
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
        
            List<String> lore = cfg.getHeadLore().stream()
                    .map(str -> Utils.setPlaceholders(str, p))
                    .collect(Collectors.toList());
    
            meta.setDisplayName(Utils.setPlaceholders(this.cfg.getHeadDisplay(), p));
        
            meta.setLore(lore);
        
            SkullMeta sm = (SkullMeta)meta;
            
            sm.setOwningPlayer(p);
            head.setItemMeta(sm);
        
            inv.setItem(slot, head);
        
            curr++;
        }
        
        setButtons();
    }
    
    public boolean hasPlayers()
    {
        return getByPage(page).size() != 0;
    }
    
    public boolean hasPlayers(int offset)
    {
        return getByPage(page + offset).size() != 0;
    }
    
    private void setButtons()
    {
        boolean show = plugin.getCfg().areButtonsAlwaysOn();
        
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
        ItemStack nextButton = new ItemStack(cfg.getNextPageMat());
        ItemMeta nextButtonMeta = nextButton.getItemMeta();
        
        List<String> nextLore = cfg.getNextPageLore().stream()
                .map(str -> Utils.setPlaceholders(str, null))
                .collect(Collectors.toList());
        
        nextButtonMeta.setLore(nextLore);
        
        nextButtonMeta.getPersistentDataContainer().set(
                new NamespacedKey(plugin, "Button"),
                PersistentDataType.STRING, "Next");
        
        nextButtonMeta.setDisplayName(Utils.format(cfg.getNextPageName()));
        
        nextButton.setItemMeta(nextButtonMeta);
        
        inv.setItem(cfg.getNextPageSlot(), nextButton);
    }
    
    private void setPreviousButton()
    {
        ItemStack prevButton = new ItemStack(cfg.getPrevPageMat());
        ItemMeta prevButtonMeta = prevButton.getItemMeta();
        
        List<String> prevLore = cfg.getPrevPageLore().stream()
                .map(str -> Utils.setPlaceholders(str, null))
                .collect(Collectors.toList());
        
        prevButtonMeta.setLore(prevLore);
        
        prevButtonMeta.getPersistentDataContainer().set(
                new NamespacedKey(plugin, "Button"),
                PersistentDataType.STRING, "Previous");
        
        prevButtonMeta.setDisplayName(Utils.format(cfg.getPrevPageName()));
        prevButton.setItemMeta(prevButtonMeta);
        
        inv.setItem(cfg.getPrevPageSlot(), prevButton);
    }
}
