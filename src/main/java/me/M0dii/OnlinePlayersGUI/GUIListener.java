package me.M0dii.OnlinePlayersGUI;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

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
        String viewName = ChatColor.stripColor(e.getView().getTitle());
    
        if(viewName.startsWith("Online Players"))
        {
            viewers.add(e.getPlayer());
        }
    }
    
    @EventHandler
    public void closeGUI(InventoryCloseEvent e)
    {
        String viewName = ChatColor.stripColor(e.getView().getTitle());
        
        if(viewName.startsWith("Online Players"))
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
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
                        PlayerListGUI.showPlayers((Player)p), 20L);
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
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
                        PlayerListGUI.showPlayers((Player)p), 20L);
            }
        }
    }
    
    @EventHandler
    public void cancelClick(InventoryClickEvent e)
    {
        if(ChatColor.stripColor(e.getView().getTitle())
                .startsWith("Online Players"))
        {
            if(e.isLeftClick())
            {
                executeClickCommands(e, Config.LEFT_CLICK_COMMANDS);
            }
    
            if(e.isRightClick())
            {
                executeClickCommands(e, Config.RIGHT_CLICK_COMMANDS);
            }
            
            e.setCancelled(true);
        }
    }
    
    private void executeClickCommands(InventoryClickEvent e, List<String> cmds)
    {
        HumanEntity clicked = e.getWhoClicked();
        
        ItemStack clickedItem = e.getCurrentItem();
        
        if(clickedItem != null)
        {
            if(clickedItem.getType().equals(Material.PLAYER_HEAD))
            {
                SkullMeta sm = (SkullMeta)clickedItem.getItemMeta();
    
                String ownerName = sm.getOwningPlayer().getName();
    
                Player player = Bukkit.getPlayer(ownerName);
    
                for(String cmd : cmds)
                {
                    String replaced = PlaceholderAPI.setPlaceholders(player, cmd);
        
                    Bukkit.dispatchCommand(clicked, replaced);
                }
            }
            
            if(clickedItem.getType().equals(Config.PREVIOUS_PAGE_MATERIAL)
            || clickedItem.getType().equals(Config.NEXT_PAGE_MATERIAL))
            {
                String number = e.getView().getTitle().replaceAll("\\D", "");
                
                int page = Integer.parseInt(number) - 1;
    
                NamespacedKey key = new NamespacedKey(this.plugin, "Button");
                PersistentDataContainer cont = clickedItem.getItemMeta().getPersistentDataContainer();
    
                if(cont.has(key, PersistentDataType.STRING))
                {
                    String buttonType = cont.get(key, PersistentDataType.STRING);
                    
                    if(buttonType.equalsIgnoreCase("Next"))
                    {
                        try
                        {
                            PlayerListGUI next = PlayerListGUI.GUIs.get(page + 1);
            
                            if(next != null)
                            {
                                PlayerListGUI.GUIs.get(page + 1).show(e.getWhoClicked());
                            }
                        }
                        catch(IndexOutOfBoundsException ex)
                        {
                            // Handle
                        }
                    }
    
                    if(buttonType.equalsIgnoreCase("Previous"))
                    {
                        try
                        {
                            PlayerListGUI next = PlayerListGUI.GUIs.get(page - 1);
            
                            if(next != null)
                            {
                                PlayerListGUI.GUIs.get(page - 1).show(e.getWhoClicked());
                            }
                        }
                        catch(IndexOutOfBoundsException ex)
                        {
                            // Handle
                        }
                    }
                }
                

            }
        }
    }
}
