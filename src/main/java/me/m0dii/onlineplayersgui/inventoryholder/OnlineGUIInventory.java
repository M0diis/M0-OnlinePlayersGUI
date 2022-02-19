package me.m0dii.onlineplayersgui.inventoryholder;

import me.m0dii.onlineplayersgui.CustomItem;
import me.m0dii.onlineplayersgui.OnlineGUI;
import me.m0dii.onlineplayersgui.utils.Config;
import me.m0dii.onlineplayersgui.utils.Utils;
import me.m0dii.onlineplayersgui.utils.Version;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
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
    private final Config cfg;

    public OnlineGUIInventory(OnlineGUI plugin, String name, int page, Player p)
    {
        this.name = name;
        this.page = page;
        
        this.plugin = plugin;
        this.cfg = plugin.getCfg();
    
        this.size = this.adjustSize(cfg);
    
        this.inv = Bukkit.createInventory(this, this.size, name);
    
        setCustomItems(p);
        
        initByPage(page);
    }
    
    public void execute(Player clickee, ItemStack clicked, ClickType clickType, int slot)
    {
        if(clicked == null)
        {
            return;
        }
        
        if(clicked.getType().equals(cfg.getDisplay().getType()))
        {
            SkullMeta sm = (SkullMeta)clicked.getItemMeta();
    
            
            Player skullOwner = null;
            
            if(Version.serverIsNewerThan(Version.v1_12_R1))
            {
                skullOwner = sm.getOwningPlayer() != null ? sm.getOwningPlayer().getPlayer() : null;
            }
            else
            {
                String owner = sm.getOwner();
    
                if(owner != null)
                {
                    skullOwner = Bukkit.getPlayer(owner);
                }
            }
            
            if(skullOwner == null)
            {
                skullOwner = Bukkit.getPlayer(Utils.clearFormat(clicked.getItemMeta().getDisplayName()));
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
    
            if(clickType.equals(ClickType.LEFT) && this.cfg.getLeftClickCmds().contains("[CLOSE]"))
                clickee.closeInventory();
    
            if(clickType.equals(ClickType.MIDDLE) && this.cfg.getMiddleClickCmds().contains("[CLOSE]"))
                clickee.closeInventory();
            
            if(clickType.equals(ClickType.RIGHT) && this.cfg.getRightClickCmds().contains("[CLOSE]"))
                clickee.closeInventory();
        }
    
        if(clicked.getType().equals(this.cfg.getPrevPageMat()) ||
           clicked.getType().equals(this.cfg.getNextPageMat()))
        {
            int nextPage = page;
            
            if(cfg.getNextPageSlot() == slot) nextPage = page + 1;
            else if(cfg.getPrevPageSlot() == slot) nextPage = page - 1;

            try
            {
                OnlineGUIInventory newinv = new OnlineGUIInventory(this.plugin, this.name, nextPage, clickee);
                
                if(newinv.hasPlayers())
                    clickee.openInventory(newinv.getInventory());
            }
            catch(IndexOutOfBoundsException ex)
            {
                // TODO
            }
        }
    
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
    
    private CustomItem getCustomItemBySlot(int slot)
    {
        return cfg.getCustomItems().getOrDefault(slot, null);
    }
    
    public void setCustomItems(Player p)
    {
        plugin.getGuiUtils().setCustomItems(inv, p, cfg.getCustomItems());
    }
    
    private int adjustSize(Config cfg)
    {
        int size = cfg.getGuiSize();
        
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
            ItemStack head = new ItemStack(cfg.getDisplay());
    
            ItemMeta meta = head.getItemMeta();
        
            List<String> lore = cfg.getHeadLore()
                    .stream()
                    .map(str -> Utils.setPlaceholders(str, player))
                    .collect(Collectors.toList());
        
            meta.setDisplayName(Utils.setPlaceholders(cfg.getHeadDisplay(), player));
            meta.setLore(lore);
            
            if(meta instanceof SkullMeta)
            {
                SkullMeta sm = (SkullMeta)meta;
    
                if(Version.getServerVersion(Bukkit.getServer()).isNewerThan(Version.v1_12_R1))
                {
                    sm.setOwningPlayer(player);
                }
                else
                {
                    sm.setOwner(player.getName());
                }
                
                head.setItemMeta(sm);
            }
            
            for(int i = 0; i < inv.getSize(); i++)
            {
                if(inv.getItem(i) == null)
                {
                    if(cfg.getNextPageSlot() != i &&
                       cfg.getPrevPageSlot() != i)
                    {
                        inv.setItem(i, head);
                        break;
                    }
                }
            }
        }
    }
    
    @NotNull
    private List<Player> getByPage(int page)
    {
        String permission = cfg.isPermissionRequired() ? cfg.getRequiredPerm()
                : null;
    
        String condition = cfg.isConditionRequired() ? cfg.getCondition()
                : null;
        
        List<Player> online = plugin.getGuiUtils().getOnline(permission, condition);
        
        List<Player> byPage = new ArrayList<>();
        
        int availableSlots = this.size - 9;
    
        for(Map.Entry<Integer, CustomItem> entry : cfg
                .getCustomItems()
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
    
    private void setButtons()
    {
        boolean show = cfg.areButtonsAlwaysOn();
        
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
        nextButtonMeta.setDisplayName(cfg.getNextPageName());
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
        prevButtonMeta.setDisplayName(Utils.format(cfg.getPrevPageName()));
        prevButton.setItemMeta(prevButtonMeta);
    
        inv.setItem(cfg.getPrevPageSlot(), prevButton);
    }
}
