package me.M0dii.OnlinePlayersGUI.Listeners;

import me.M0dii.OnlinePlayersGUI.InventoryHolder.ConditionalGUIInventory;
import me.M0dii.OnlinePlayersGUI.InventoryHolder.OnlineGUIInventory;
import me.M0dii.OnlinePlayersGUI.OnlineGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class InventoryListener implements Listener
{
    private final OnlineGUI plugin;
    
    public InventoryListener(OnlineGUI plugin)
    {
        this.plugin = plugin;
    }
    
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e)
    {
        Inventory inv = e.getClickedInventory();
        
        if(inv != null && inv.getHolder() instanceof OnlineGUIInventory)
        {
            e.setCancelled(true);
            
            OnlineGUIInventory guiInv = (OnlineGUIInventory)inv.getHolder();
            
            guiInv.execute((Player)e.getWhoClicked(), e.getCurrentItem(), e.isLeftClick());
        }
    
        if(inv != null && inv.getHolder() instanceof ConditionalGUIInventory)
        {
            e.setCancelled(true);
    
            ConditionalGUIInventory guiInv = (ConditionalGUIInventory)inv.getHolder();
        
            guiInv.execute((Player)e.getWhoClicked(), e.getCurrentItem(), e.isLeftClick());
        }
    }
    
}
