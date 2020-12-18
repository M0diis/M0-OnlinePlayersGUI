package me.M0dii.OnlinePlayersGUI;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerListGUI
{
    private final Inventory inv;
    private final String name;
    private final Map<Integer, ItemStack> guiContents;
    private static PlayerListGUI playerListGUI;
    static ArrayList<Player> online;
    
    public PlayerListGUI()
    {
        this.name = format("Online Players");
        
        this.inv = Bukkit.createInventory(null, 54, this.name);
        
        this.guiContents = new HashMap<>(54);
        
        online = new ArrayList<>(Bukkit.getOnlinePlayers());
        
        playerListGUI = this;
    }
    
    public void setItem(int slot, ItemStack item)
    {
        this.guiContents.put(slot, item);
        
        this.inv.setItem(slot, item);
    }
    
    public static PlayerListGUI getGUI()
    {
        return playerListGUI;
    }
    
    public ItemStack getItem(int slot)
    {
        return this.guiContents.get(slot);
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
        Inventory inv = Bukkit.createInventory(h, getInventory().getSize(), name);
        
        inv.setContents(getInventory().getContents());
        
        h.openInventory(inv);
    }
    
    public static void showPlayers(Player player)
    {
        PlayerListGUI gui = new PlayerListGUI();
        
        for(int j = 0; j < (Math.min(online.size(), 54)); j++)
        {
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            
            Player p = online.get(j);
            
            ItemMeta meta = head.getItemMeta();

            List<String> lore = new ArrayList<>();
            
            for(String s : Config.HEAD_LORE)
            {
                lore.add(format(s));
            }
    
            meta.setDisplayName(format(Config.HEAD_NAME
                    .replaceAll("%player%", p.getDisplayName())));
            
            meta.setLore(lore);
            
            SkullMeta sm = (SkullMeta)meta;
            
            sm.setOwningPlayer(p);
    
            head.setItemMeta(sm);
            
            gui.setItem(j, head);
        }
        
        gui.show(player);
    }
    
    private static String format(String text)
    {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
