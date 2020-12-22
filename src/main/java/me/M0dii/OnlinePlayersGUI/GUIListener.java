package me.M0dii.OnlinePlayersGUI;

import net.ess3.api.IEssentials;
import org.bukkit.*;
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
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GUIListener implements Listener
{
    private final Main plugin;
    
    private final IEssentials ess;
    
    public GUIListener(Main plugin, IEssentials ess)
    {
        this.plugin = plugin;
        
        this.ess = ess;
        
        this.viewers = new ArrayList<>();
        
        currentPage = new HashMap<>();
    }
    
    List<HumanEntity> viewers;
    private static HashMap<UUID, Integer> currentPage;
    
    public static void setWatchingPage(Player p, int page)
    {
        currentPage.put(p.getUniqueId(), page);
    }
    
    public static int getWatchingPage(Player p)
    {
        return currentPage.get(p.getUniqueId());
    }
    
    @EventHandler
    public void openGUI(InventoryOpenEvent e)
    {
        String viewName = ChatColor.stripColor(e.getView().getTitle());
    
        if(viewName.equalsIgnoreCase(ChatColor.stripColor(Config.GUI_TITLE)))
        {
            viewers.add(e.getPlayer());
        }
    }
    
    @EventHandler
    public void closeGUI(InventoryCloseEvent e)
    {
        String viewName = ChatColor.stripColor(e.getView().getTitle());
    
        if(viewName.equalsIgnoreCase(ChatColor.stripColor(Config.GUI_TITLE)))
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
        String viewName = ChatColor.stripColor(e.getView().getTitle());
    
        if(viewName.equalsIgnoreCase(ChatColor.stripColor(Config.GUI_TITLE)))
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
        HumanEntity player = e.getWhoClicked();
        
        ItemStack clickedItem = e.getCurrentItem();
        
        if(clickedItem != null)
        {
            if(clickedItem.getType().equals(Material.PLAYER_HEAD))
            {
                SkullMeta sm = (SkullMeta)clickedItem.getItemMeta();
                
                OfflinePlayer owner = sm.getOwningPlayer();
                
                if(owner != null)
                {
                    String ownerName = sm.getOwningPlayer().getName();
    
                    if(ownerName != null)
                    {
                        Player skullOwner = Bukkit.getPlayer(ownerName);
                        
                        for(String cmd : cmds)
                        {
                            String replaced = PlaceholderAPI.setPlaceholders(skullOwner, cmd);
        
                            Bukkit.dispatchCommand(player, replaced);
                        }
                    }
                }
            }
            
            if(clickedItem.getType().equals(Config.PREVIOUS_PAGE_MATERIAL)
            || clickedItem.getType().equals(Config.NEXT_PAGE_MATERIAL))
            {
                int page = getWatchingPage((Player)player);
    
                NamespacedKey key = new NamespacedKey(this.plugin, "Button");
                PersistentDataContainer cont = clickedItem.getItemMeta().getPersistentDataContainer();
    
                if(cont.has(key, PersistentDataType.STRING))
                {
                    String buttonType = cont.get(key, PersistentDataType.STRING);
                    
                    int nextPage = page;
                    
                    if(buttonType == null) return;
                    
                    if(buttonType.equalsIgnoreCase("Next"))
                    {
                        nextPage = page + 1;
                    }
                    else if(buttonType.equalsIgnoreCase("Previous"))
                    {
                        nextPage = page - 1;

                    }
    
                    try
                    {
                        PlayerListGUI next = PlayerListGUI.GUIs.get(nextPage);
        
                        if(next != null)
                        {
                            next.show(player);
                            
                            setWatchingPage((Player)player, nextPage);
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
