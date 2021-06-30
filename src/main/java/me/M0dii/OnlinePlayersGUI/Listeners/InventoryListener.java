package me.M0dii.OnlinePlayersGUI.Listeners;

import me.M0dii.OnlinePlayersGUI.InventoryHolder.ConditionalGUIInventory;
import me.M0dii.OnlinePlayersGUI.InventoryHolder.OnlineGUIInventory;
import me.M0dii.OnlinePlayersGUI.OnlineGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import java.util.ArrayList;
import java.util.List;

public class InventoryListener implements Listener
{
    private final OnlineGUI plugin;
    
    public InventoryListener(OnlineGUI plugin)
    {
        this.plugin = plugin;
        
        this.viewers = new ArrayList<>();
    }
    
    private final List<HumanEntity> viewers;
    
    @EventHandler
    public void addViewer(InventoryOpenEvent e)
    {
        if(e.getInventory().getHolder() instanceof OnlineGUIInventory)
            this.viewers.add(e.getPlayer());
    }
    
    @EventHandler
    public void removeViewer(InventoryCloseEvent e)
    {
        if(e.getInventory().getHolder() instanceof OnlineGUIInventory)
            this.viewers.remove(e.getPlayer());
    }
    
    @EventHandler
    public void onInteract(InventoryMoveItemEvent e)
    {
        Inventory inv = e.getSource();
        
        if(inv.getHolder() instanceof OnlineGUIInventory
            || inv.getHolder() instanceof ConditionalGUIInventory)
            e.setCancelled(true);
    }
    
    @EventHandler
    public void updateOnJoin(PlayerJoinEvent e)
    {
        if(this.plugin.getCfg().UPDATE_ON_JOIN())
            updateView();
    }
    
    @EventHandler
    public void updateOnLeave(PlayerQuitEvent e)
    {
        if(this.plugin.getCfg().UPDATE_ON_LEAVE())
            updateView();
    }
    
    private void updateView()
    {
        for(HumanEntity p : viewers)
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () ->
            {
                InventoryView curr = p.getOpenInventory();
                Inventory inv = curr.getTopInventory();
                
                if(inv.getHolder() instanceof OnlineGUIInventory)
                {
                    OnlineGUIInventory ogi = (OnlineGUIInventory)inv.getHolder();
                    
                    ogi.refresh();
                    
                    p.openInventory(ogi.getInventory());
                }
            }, 20L);
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
