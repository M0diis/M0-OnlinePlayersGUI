package me.M0dii.OnlinePlayersGUI;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class GUIListener implements Listener
{
    private final Main plugin;
    
    public GUIListener(Main plugin)
    {
        this.plugin = plugin;
        
        this.viewers = new ArrayList<>();
    }
    
    List<HumanEntity> viewers;
    
    @EventHandler
    public void openGUI(InventoryOpenEvent e)
    {
        if(e.getView().getTitle().equalsIgnoreCase("Online Players"))
        {
            viewers.add(e.getPlayer());
        }
    }
    
    @EventHandler
    public void closeGUI(InventoryCloseEvent e)
    {
        if(e.getView().getTitle().equalsIgnoreCase("Online Players"))
        {
            viewers.remove(e.getPlayer());
        }
    }
    
    @EventHandler
    public void updateOnJoin(PlayerJoinEvent e)
    {
        if(Config.UPDATE_ON_JOIN)
        {
            for(HumanEntity p : viewers)
            {
                PlayerListGUI.showPlayers((Player)p);
            }
        }
    }
    
    @EventHandler
    public void updateOnLeave(PlayerQuitEvent e)
    {
        if(Config.UPDATE_ON_LEAVE)
        {
            for(HumanEntity p : viewers)
            {
                PlayerListGUI.showPlayers((Player)p);
            }
        }
    }
    
    @EventHandler
    public void cancelClick(InventoryClickEvent e)
    {
        if(ChatColor.stripColor(e.getView().getTitle())
                .equalsIgnoreCase("Online Players"))
        {
            if(e.isLeftClick())
            {
                List<String> lccmds = Config.LEFT_CLICK_COMMANDS;
    
                HumanEntity clicked = e.getWhoClicked();
                
                ItemStack clickedItem = e.getCurrentItem();
                
                if(clickedItem != null)
                {
                    SkullMeta sm = (SkullMeta)clickedItem.getItemMeta();

                    String ownerName = sm.getOwningPlayer().getName();
                    
                    for(String cmd : lccmds)
                    {
                        String replaced = cmd.replaceAll("%player%", ownerName);
        
                        Bukkit.dispatchCommand(clicked, replaced);
                    }
                }
            }
    
            if(e.isRightClick())
            {
                List<String> rccmds = Config.RIGHT_CLICK_COMMANDS;
                
                HumanEntity clicked = e.getWhoClicked();
    
                ItemStack clickedItem = e.getCurrentItem();
    
                if(clickedItem != null)
                {
                    SkullMeta sm = (SkullMeta)clickedItem.getItemMeta();
        
                    String ownerName = sm.getOwningPlayer().getName();
        
                    for(String cmd : rccmds)
                    {
                        String replaced = cmd.replaceAll("%player%", ownerName);
            
                        Bukkit.dispatchCommand(clicked, replaced);
                    }
                }
            }
            
            e.setCancelled(true);
        }
    }
}
