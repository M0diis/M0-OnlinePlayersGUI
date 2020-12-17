package me.M0dii.OnlinePlayersGUI;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIListener implements Listener
{
    private final Main plugin;
    
    public GUIListener(Main plugin)
    {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void cancelClick(InventoryClickEvent e)
    {
        if(ChatColor.stripColor(e.getView().getTitle())
                .equalsIgnoreCase("Online Players"))
        {
            e.setCancelled(true);
        }
    }
}
