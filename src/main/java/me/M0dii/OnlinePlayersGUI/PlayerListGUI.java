package me.M0dii.OnlinePlayersGUI;

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
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerListGUI
{
    public static List<PlayerListGUI> GUIs;
    private final Inventory inv;
    private final String name;
    private static PlayerListGUI playerListGUI;
    private final int page;
    private static final Main plugin = Main.getInstance();
    private final int size;
    
    public PlayerListGUI(int page)
    {
        this.page = page + 1;
        
        this.name = Config.GUI_TITLE;
        
        this.size = initializeSize();

        this.inv = Bukkit.createInventory(null, this.size, this.name);
        
        playerListGUI = this;
    }
    
    private int initializeSize()
    {
        plugin.getLogger().info(String.valueOf(Config.GUI_SIZE));
        
        if(Config.GUI_SIZE % 9 == 0)
        {
            return Config.GUI_SIZE;
        }
        else if (Config.GUI_SIZE < 18)
        {
            return 18;
        }
        else if (Config.GUI_SIZE > 54)
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
    
    public static PlayerListGUI getGUI()
    {
        return playerListGUI;
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
    
    public void show(HumanEntity h)
    {
        Inventory inventory = Bukkit.createInventory(h, this.getInventory().getSize(), name);
    
        inventory.setContents(this.getInventory().getContents());
        
        h.openInventory(inventory);
    }
    
    public static void showPlayers(Player player)
    {
        List<Player> online =
                Bukkit.getOnlinePlayers().stream().filter(p ->
                !p.hasPermission("m0onlinegui.hidden"))
                .collect(Collectors.toList());
        
        for(int i = 0; i < 15; i++) {
            online.add(online.get(0));
        }
        
        GUIs = new ArrayList<>();
        
        int requiredPages = calculatePages(online);
        
        int curr = 0;
        
        for(int page = 0; page < requiredPages; page++)
        {
            PlayerListGUI gui = new PlayerListGUI(page);
            
            for(int slot = 0; slot < Math.min(Config.GUI_SIZE - 9, online.size()); slot++)
            {
                if(curr < online.size())
                {
                    ItemStack head = new ItemStack(Material.PLAYER_HEAD);
    
                    Player p = online.get(curr);
                    
                    curr++;
    
                    ItemMeta meta = head.getItemMeta();
    
                    List<String> lore = new ArrayList<>();
    
                    for(String s : Config.HEAD_LORE)
                    {
                        lore.add(format(PlaceholderAPI.setPlaceholders(p, s)));
                    }
    
                    meta.setDisplayName(format(PlaceholderAPI.setPlaceholders(p, Config.HEAD_NAME)));
    
                    meta.setLore(lore);
    
                    SkullMeta sm = (SkullMeta)meta;
    
                    sm.setOwningPlayer(p);
    
                    head.setItemMeta(sm);
    
                    gui.setItem(slot, head);
                }
            }
            
            if(!Config.HIDE_BUTTONS_ON_SINGLE)
            {
                ItemStack nextButton = new ItemStack(Config.NEXT_PAGE_MATERIAL);
                ItemMeta nextButtonMeta = nextButton.getItemMeta();
    
                List<String> nextLore = new ArrayList<>();
    
                for(String m : Config.NEXT_PAGE_LORE)
                {
                    nextLore.add(format(m));
                }
    
                nextButtonMeta.setLore(nextLore);
    
                nextButtonMeta.getPersistentDataContainer().set(
                        new NamespacedKey(plugin, "Button"),
                        PersistentDataType.STRING, "Next");
    
                nextButtonMeta.setDisplayName(Config.NEXT_PAGE_NAME);
                nextButton.setItemMeta(nextButtonMeta);
    
                gui.setItem(Config.GUI_SIZE - 4, nextButton);
    
                ItemStack prevButton = new ItemStack(Config.PREVIOUS_PAGE_MATERIAL);
                ItemMeta prevButtonMeta = prevButton.getItemMeta();
                
                List<String> prevLore = new ArrayList<>();
                
                for(String m : Config.PREVIOUS_PAGE_LORE)
                {
                    prevLore.add(format(m));
                }
                
                prevButtonMeta.setLore(prevLore);
    
                prevButtonMeta.getPersistentDataContainer().set(
                        new NamespacedKey(plugin, "Button"),
                        PersistentDataType.STRING, "Previous");
    
                prevButtonMeta.setDisplayName(Config.PREVIOUS_PAGE_NAME);
                prevButton.setItemMeta(prevButtonMeta);
                gui.setItem(Config.GUI_SIZE - 6, prevButton);
            }
            
            GUIs.add(gui);
        }
        
        GUIs.get(0).show(player);
        
        GUIListener.setWatchingPage(player, 1);
    }
    
    private static int calculatePages(List<Player> players)
    {
        int pages = 1;
        int size = 0;
        
        for(int i = 0; i < players.size(); i++)
        {
            size++;

            if(size > Config.GUI_SIZE - 9)
            {
                pages++;
                size = 0;
            }
        }
        
        return pages;
    }
    
    private static String format(String text)
    {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
