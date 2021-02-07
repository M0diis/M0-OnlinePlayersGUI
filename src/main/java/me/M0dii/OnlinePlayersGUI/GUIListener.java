package me.M0dii.OnlinePlayersGUI;

import me.clip.placeholderapi.PlaceholderAPI;
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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GUIListener implements Listener
{
    private final OnlineGUI plugin;
    private final Config config;
    
    public GUIListener(OnlineGUI plugin)
    {
        this.plugin = plugin;
        this.viewers = new ArrayList<>();
        this.config = plugin.getCfg();
        
        currentPage = new HashMap<>();
    }
    
    private final List<HumanEntity> viewers;
    
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
    
        if(viewName.equalsIgnoreCase(ChatColor.stripColor(this.config.GUI_TITLE())))
            viewers.add(e.getPlayer());
    }
    
    @EventHandler
    public void closeGUI(InventoryCloseEvent e)
    {
        String viewName = ChatColor.stripColor(e.getView().getTitle());
    
        if(viewName.equalsIgnoreCase(ChatColor.stripColor(this.config.GUI_TITLE())))
            this.viewers.remove(e.getPlayer());
    }
    
    @EventHandler
    public void updateOnJoin(PlayerJoinEvent e)
    {
        if(this.config.UPDATE_ON_JOIN())
        {
            for(HumanEntity p : viewers)
            {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
                        this.plugin.getGUI().showPlayers((Player)p), 20L);
            }
        }
    }
    
    @EventHandler
    public void updateOnLeave(PlayerQuitEvent e)
    {
        if(this.config.UPDATE_ON_LEAVE())
        {
            for(HumanEntity p : viewers)
            {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
                        this.plugin.getGUI().showPlayers((Player)p), 20L);
            }
        }
    }
    
    @EventHandler
    public void cancelClick(InventoryClickEvent e)
    {
        String viewName = ChatColor.stripColor(e.getView().getTitle());
    
        if(viewName.equalsIgnoreCase(ChatColor.stripColor(this.config.GUI_TITLE())))
        {
            if(e.isLeftClick())
                executeClickCommands(e, this.config.LEFT_CLICK_CMDS());
    
            if(e.isRightClick())
                executeClickCommands(e, this.config.RIGHT_CLICK_CMDS());
            
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
            
            if(clickedItem.getType().equals(this.config.PREV_PAGE_MATERIAL())
            || clickedItem.getType().equals(this.config.NEXT_PAGE_MATERIAL()))
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
                        PlayerListGUI temp = this.plugin.getGUI();
                        
                        if(temp != null)
                            return;
                        
                        PlayerListGUI next = temp.getGuiPages().get(nextPage);
        
                        if(next != null)
                        {
                            next.show(player, Bukkit.getOnlinePlayers().size());
                            
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
