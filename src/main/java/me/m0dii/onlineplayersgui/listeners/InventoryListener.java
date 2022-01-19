package me.m0dii.onlineplayersgui.listeners;

import me.m0dii.onlineplayersgui.inventoryholder.CustomGUI;
import me.m0dii.onlineplayersgui.OnlineGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class InventoryListener implements Listener
{
    private final OnlineGUI plugin;
    
    public InventoryListener(OnlineGUI plugin)
    {
        this.plugin = plugin;
        
        this.guiViewers = new ArrayList<>();
    }
    
    private final List<HumanEntity> guiViewers;
    
    @EventHandler
    public void addOnOpen(InventoryOpenEvent e)
    {
        if(e.getInventory().getHolder() instanceof CustomGUI)
            this.guiViewers.add(e.getPlayer());
    }
    
    @EventHandler
    public void removeOnClose(InventoryCloseEvent e)
    {
        if(e.getInventory().getHolder() instanceof CustomGUI)
            this.guiViewers.remove(e.getPlayer());
    }
    
    @EventHandler
    public void onInteract(InventoryMoveItemEvent e)
    {
        if(e.getSource() instanceof CustomGUI)
            e.setCancelled(true);
        
        if(e.getDestination() instanceof CustomGUI)
            e.setCancelled(true);
    }
    
    @EventHandler
    public void updateOnJoin(PlayerJoinEvent e)
    {
        if(this.plugin.getCfg().UPDATE_ON_JOIN())
        {
            updateView();
        }
    }
    
    @EventHandler
    public void updateOnLeave(PlayerQuitEvent e)
    {
        if(this.plugin.getCfg().UPDATE_ON_LEAVE())
        {
            updateView();
            
            guiViewers.remove(e.getPlayer());
        }
    }
    
    private void updateView()
    {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () ->
        {
            for(HumanEntity p : guiViewers)
            {
                Inventory inv = p.getOpenInventory().getTopInventory();
    
                if(inv.getHolder() instanceof CustomGUI cg)
                    cg.refresh((Player)p);
            }
        });
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e)
    {
        Inventory inv = e.getClickedInventory();
        
        if(inv != null && inv.getHolder() instanceof CustomGUI cg)
        {
            e.setCancelled(true);
    
            cg.execute((Player)e.getWhoClicked(), e.getCurrentItem(), e.getClick());
        }
    }
    
}
