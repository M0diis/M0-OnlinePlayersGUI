package me.M0dii.OnlinePlayersGUI;

import me.M0dii.OnlinePlayersGUI.Utils.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
public class PlayerListGUI
{
    private List<PlayerListGUI> guiPages;
    
    public List<PlayerListGUI> getGuiPages()
    {
        if(guiPages == null)
            this.guiPages = new ArrayList<>();
    
        return this.guiPages;
    }
    
    private final Inventory inv;
    private final String name;
    private final int displayPage;
    private final OnlineGUI plugin;
    private final int size;
    private final Config cfg;
    
    public PlayerListGUI(OnlineGUI plugin, int page)
    {
        this.plugin = plugin;
        this.cfg = this.plugin.getCfg();
        
        this.displayPage = page + 1;
        
        this.name = this.cfg.GUI_TITLE();
        
        this.size = this.adjustSize();

        this.inv = Bukkit.createInventory(null, this.size, this.name);
        
        this.plugin.setGUI(this);
    }
    
    private int adjustSize()
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
    
    public void setItem(int slot, ItemStack item)
    {
        this.inv.setItem(slot, item);
    }
    
    public int getDisplayPage()
    {
        return this.displayPage;
    }
    
    public PlayerListGUI getGUI()
    {
        return this;
    }
    
    public ItemStack getItem(int slot)
    {
        return this.inv.getItem(slot);
    }
    
    public ItemStack getItem(int x, int y)
    {
        return getItem(x * 9 + y);
    }
    
    public Inventory getInventory()
    {
        return this.inv;
    }
    
    public void show(HumanEntity h, int onlineSize)
    {
        Inventory inventory = Bukkit.createInventory(h, this.getInventory().getSize(),
                name.replaceAll("%playercount%", String.valueOf(onlineSize)));
        
        inventory.setContents(this.getInventory().getContents());
        
        h.openInventory(inventory);
    }
    
    private List<Player> getOnline(boolean hook)
    {
        List<Player> online = new ArrayList<>();
        
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
        
        return cfg.isConditionEnabled() ? filterByCondition(online) : online;
    }
    
    private List<Player> filterByCondition(List<Player> players)
    {
        List<Player> filtered = new ArrayList<>();
        
        for(Player p : players)
        {
            String result = PlaceholderAPI.setPlaceholders(p, cfg.getCondition())
                    .toLowerCase();
        
            if(result.equals("yes") || result.equals("true"))
                filtered.add(p);
        }
        
        return filtered;
    }
    
    public void showPlayers(Player player)
    {
        List<Player> online = getOnline(this.cfg.ESSX_HOOK());
        
        this.guiPages = new ArrayList<>();
        
        int requiredPages = calculatePages(online);
        
        int curr = 0;
        
        for(int page = 0; page < requiredPages; page++)
        {
            PlayerListGUI gui = new PlayerListGUI(this.plugin, page);
            
            for(int slot = 0; slot < Math.min(this.size - 9, online.size()); slot++)
            {
                if(curr < online.size())
                {
                    ItemStack head = new ItemStack(Material.PLAYER_HEAD);
    
                    Player p = online.get(curr);
                    
                    ItemMeta meta = head.getItemMeta();
    
                    List<String> lore = new ArrayList<>();
    
                    for(String s : cfg.HEAD_LORE())
                        lore.add(Utils.format(PlaceholderAPI.setPlaceholders(p, s)));
    
                    meta.setDisplayName(
                            Utils.format(PlaceholderAPI.setPlaceholders(p, cfg.HEAD_DISPLAY_NAME())));
    
                    meta.setLore(lore);
    
                    SkullMeta sm = (SkullMeta)meta;
    
                    sm.setOwningPlayer(p);
    
                    head.setItemMeta(sm);
    
                    gui.setItem(slot, head);
    
                    curr++;
                }
            }
            
            if(!cfg.HIDE_BUTTONS_SINGLE_PAGE())
            {
                ItemStack nextButton = new ItemStack(cfg.NEXT_PAGE_MATERIAL());
                ItemMeta nextButtonMeta = nextButton.getItemMeta();
    
                List<String> nextLore = cfg.NEXT_PAGE_LORE().stream().map(Utils::format)
                        .collect(Collectors.toList());
    
                nextButtonMeta.setLore(nextLore);
    
                nextButtonMeta.getPersistentDataContainer().set(
                        new NamespacedKey(plugin, "Button"),
                        PersistentDataType.STRING, "Next");
    
                nextButtonMeta.setDisplayName(cfg.NEXT_PAGE_BUTTON_NAME());
                nextButton.setItemMeta(nextButtonMeta);
    
                gui.setItem(cfg.GUI_SIZE() - 4, nextButton);
    
                ItemStack prevButton = new ItemStack(cfg.PREV_PAGE_MATERIAL());
                ItemMeta prevButtonMeta = prevButton.getItemMeta();
                
                List<String> prevLore = cfg.PREV_PAGE_LORE().stream().map(Utils::format)
                        .collect(Collectors.toList());
    
                prevButtonMeta.setLore(prevLore);
    
                prevButtonMeta.getPersistentDataContainer().set(
                        new NamespacedKey(plugin, "Button"),
                        PersistentDataType.STRING, "Previous");
    
                prevButtonMeta.setDisplayName(cfg.PREV_PAGE_BUTTON_NAME());
                prevButton.setItemMeta(prevButtonMeta);
                
                gui.setItem(cfg.GUI_SIZE() - 6, prevButton);
            }
            
            List<CustomItem> customItems = this.cfg.getCustomItems();
    
            for(CustomItem c : customItems)
            {
                ItemStack item = c.getItem();
                ItemMeta m = item.getItemMeta();
                
                NamespacedKey key = new NamespacedKey(this.plugin, "Slot");
                PersistentDataContainer cont = item.getItemMeta().getPersistentDataContainer();
    
                if(cont.has(key, PersistentDataType.INTEGER))
                {
                    int slot = cont.get(key, PersistentDataType.INTEGER);
    
                    List<String> lore = c.getLore();
                    
                    List<String> newLore = new ArrayList<>();
                    
                    for(String l : lore)
                        newLore.add(PlaceholderAPI.setPlaceholders(player, l));
                    
                    m.setLore(newLore);
                    
                    item.setItemMeta(m);
                    
                    gui.setItem(this.size - 10 + slot, item);
                }
            }
            
            this.guiPages.add(gui);
        }
        
        this.guiPages.get(0).show(player, online.size());
        
        GUIListener.setWatchingPage(player, 1);
    }
    
    private int calculatePages(List<Player> players)
    {
        int pages = 1;
        int counter = 0;
        
        for(int i = 0; i < players.size(); i++)
        {
            counter++;

            if(counter > this.cfg.GUI_SIZE() - 9)
            {
                pages++;
                counter = 0;
            }
        }
        
        return pages;
    }
}
