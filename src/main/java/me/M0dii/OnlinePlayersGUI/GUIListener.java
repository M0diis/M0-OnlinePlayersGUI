package me.M0dii.OnlinePlayersGUI;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

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
