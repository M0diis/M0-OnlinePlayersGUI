package me.M0dii.OnlinePlayersGUI;

import net.ess3.api.IEssentials;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import me.clip.placeholderapi.PlaceholderAPI;
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
        return this.guiPages;
    }
    
    private final Inventory inv;
    private final String name;
    private final int page;
    private final OnlineGUI plugin;
    private final int size;
    private final IEssentials ess;
    private final Config config;

    public PlayerListGUI(OnlineGUI plugin, int page)
    {
        this.plugin = plugin;
        this.ess = this.plugin.getEssentials();
        this.config = this.plugin.getCfg();
        
        this.page = page + 1;
        
        this.name = this.config.GUI_TITLE();
        
        this.size = initializeSize();

        this.inv = Bukkit.createInventory(null, this.size, this.name);
        
        this.plugin.setGUI(this);
    }
    
    private int initializeSize()
    {
        int size = config.GUI_SIZE();
        
        if(size % 9 == 0)
        {
            return size;
        }
        else if (size < 18)
        {
            return 18;
        }
        else if (size > 54)
        {
            return 54;
        }
        
        return 54;
    }
    
    public void setItem(int slot, ItemStack item)
    {
        this.inv.setItem(slot, item);
    }
    
    public int getPage()
    {
        return this.page;
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
        
        if(hook)
        {
            online = Bukkit.getOnlinePlayers().stream().filter(p ->
                    !p.hasPermission("m0onlinegui.hidden") && !ess.getUser(p).isVanished())
                    .collect(Collectors.toList());
        }
        else
        {
            online = Bukkit.getOnlinePlayers().stream().filter(p ->
                    !p.hasPermission("m0onlinegui.hidden"))
                    .collect(Collectors.toList());
        }
        
        return online;
    }
    
    public void showPlayers(Player player)
    {
        List<Player> online = getOnline(config.ESSX_HOOK());
        
        this.guiPages = new ArrayList<>();
        
        int requiredPages = calculatePages(online);
        
        int curr = 0;
        
        for(int page = 0; page < requiredPages; page++)
        {
            PlayerListGUI gui = new PlayerListGUI(this.plugin, page);
            
            for(int slot = 0; slot < Math.min(config.GUI_SIZE() - 9, online.size()); slot++)
            {
                if(curr < online.size())
                {
                    ItemStack head = new ItemStack(Material.PLAYER_HEAD);
    
                    Player p = online.get(curr);
                    
                    curr++;
    
                    ItemMeta meta = head.getItemMeta();
    
                    List<String> lore = new ArrayList<>();
    
                    for(String s : config.HEAD_LORE())
                    {
                        lore.add(format(PlaceholderAPI.setPlaceholders(p, s)));
                    }
    
                    meta.setDisplayName(format(PlaceholderAPI.setPlaceholders(p, config.HEAD_DISPLAY_NAME())));
    
                    meta.setLore(lore);
    
                    SkullMeta sm = (SkullMeta)meta;
    
                    sm.setOwningPlayer(p);
    
                    head.setItemMeta(sm);
    
                    gui.setItem(slot, head);
                }
            }
            
            if(!config.HIDE_BUTTONS_SINGLE_PAGE())
            {
                ItemStack nextButton = new ItemStack(config.NEXT_PAGE_MATERIAL());
                ItemMeta nextButtonMeta = nextButton.getItemMeta();
    
                List<String> nextLore = new ArrayList<>();
    
                for(String m : config.NEXT_PAGE_LORE())
                {
                    nextLore.add(format(m));
                }
    
                nextButtonMeta.setLore(nextLore);
    
                nextButtonMeta.getPersistentDataContainer().set(
                        new NamespacedKey(plugin, "Button"),
                        PersistentDataType.STRING, "Next");
    
                nextButtonMeta.setDisplayName(config.NEXT_PAGE_BUTTON_NAME());
                nextButton.setItemMeta(nextButtonMeta);
    
                gui.setItem(config.GUI_SIZE() - 4, nextButton);
    
                ItemStack prevButton = new ItemStack(config.PREV_PAGE_MATERIAL());
                ItemMeta prevButtonMeta = prevButton.getItemMeta();
                
                List<String> prevLore = new ArrayList<>();
                
                for(String m : config.PREV_PAGE_LORE())
                {
                    prevLore.add(format(m));
                }
                
                prevButtonMeta.setLore(prevLore);
    
                prevButtonMeta.getPersistentDataContainer().set(
                        new NamespacedKey(plugin, "Button"),
                        PersistentDataType.STRING, "Previous");
    
                prevButtonMeta.setDisplayName(config.PREV_PAGE_BUTTON_NAME());
                prevButton.setItemMeta(prevButtonMeta);
                
                gui.setItem(config.GUI_SIZE() - 6, prevButton);
            }
            
            List<ItemStack> customItems = this.config.getCustomItems();
    
            for(ItemStack c : customItems)
            {
                NamespacedKey key = new NamespacedKey(this.plugin, "Slot");
                PersistentDataContainer cont = c.getItemMeta().getPersistentDataContainer();
    
                if(cont.has(key, PersistentDataType.INTEGER))
                {
                    int slot = cont.get(key, PersistentDataType.INTEGER);
    
                    gui.setItem(this.size - 10 + slot, c);
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

            if(counter > this.config.GUI_SIZE() - 9)
            {
                pages++;
                counter = 0;
            }
        }
        
        return pages;
    }
    
    private static String format(String text)
    {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
