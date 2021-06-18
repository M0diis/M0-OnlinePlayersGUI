package me.M0dii.OnlinePlayersGUI;

import me.M0dii.OnlinePlayersGUI.Utils.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class GUIListener implements Listener
{
    private final OnlineGUI plugin;
    private final Config cfg;
    
    public GUIListener(OnlineGUI plugin)
    {
        this.plugin = plugin;
        this.viewers = new ArrayList<>();
        this.cfg = plugin.getCfg();
        
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
    public void addViewer(InventoryOpenEvent e)
    {
        if(isOnlineGUI(e))
            this.viewers.add(e.getPlayer());
    }
    
    @EventHandler
    public void removeViewer(InventoryCloseEvent e)
    {
        if(isOnlineGUI(e))
            this.viewers.remove(e.getPlayer());
    }
    
    private boolean isOnlineGUI(InventoryEvent e)
    {
        String viewName = Utils.clearFormat(e.getView().getTitle())
                .replaceAll("\\d", "")
                .trim();
        
        String title = Utils.clearFormat(this.cfg.GUI_TITLE())
                .replace("%playercount%", "")
                .trim();
        
        return viewName.contains(title);
    }
    
    @EventHandler
    public void updateOnJoin(PlayerJoinEvent e)
    {
        if(this.cfg.UPDATE_ON_JOIN())
            for(HumanEntity p : viewers)
                Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () ->
                        this.plugin.getGUI().showPlayers((Player)p), 20L);
    }
    
    @EventHandler
    public void updateOnLeave(PlayerQuitEvent e)
    {
        if(this.cfg.UPDATE_ON_LEAVE())
            for(HumanEntity p : viewers)
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
                        this.plugin.getGUI().showPlayers((Player)p), 20L);
    }
    
    @EventHandler
    public void cancelClick(InventoryClickEvent e)
    {
        String viewName = Utils.clearFormat(e.getView().getTitle())
                .replaceAll("\\d", "")
                .trim();
    
        String title = Utils.clearFormat(this.cfg.GUI_TITLE())
                .replace("%playercount%", "")
                .trim();
    
        if(viewName.contains(title))
        {
            if(e.isLeftClick())
            {
                executeClickCommands(e, this.cfg.LEFT_CLICK_CMDS());
                
                if(this.cfg.CLOSE_ON_LEFT_CLICK())
                    e.getView().getPlayer().closeInventory();
            }
    
            if(e.isRightClick())
            {
                executeClickCommands(e, this.cfg.RIGHT_CLICK_CMDS());
    
                if(this.cfg.CLOSE_ON_RIGHT_CLICK())
                    e.getView().getPlayer().closeInventory();
            }
            
            e.setCancelled(true);
        }
    }
    
    private void executeClickCommands(InventoryClickEvent e, List<String> cmds)
    {
        Player player = (Player)e.getWhoClicked();
        
        ItemStack clickedItem = e.getCurrentItem();
        
        if(clickedItem != null && clickedItem.getType()
                .equals(Material.PLAYER_HEAD))
        {
            SkullMeta sm = (SkullMeta)clickedItem.getItemMeta();
            
            String ownerName = sm.getOwner();
            
            if(ownerName != null)
            {
                Player skullOwner = Bukkit.getPlayer(ownerName);
                
                for(String cmd : cmds)
                    sendCommand(player, skullOwner, cmd);
            }
        }
        else if((clickedItem != null) &&
          (clickedItem.getType().equals(this.cfg.PREV_PAGE_MATERIAL())
        || clickedItem.getType().equals(this.cfg.NEXT_PAGE_MATERIAL())))
        {
            int page = getWatchingPage(player);

            NamespacedKey key = new NamespacedKey(this.plugin, "Button");
            PersistentDataContainer cont = clickedItem.getItemMeta().getPersistentDataContainer();

            if(cont.has(key, PersistentDataType.STRING))
            {
                String buttonType = cont.get(key, PersistentDataType.STRING);
                
                int nextPage = page;
                
                if(buttonType == null) return;
                
                if(buttonType.equalsIgnoreCase("Next"))
                    nextPage = page + 1;
                else if(buttonType.equalsIgnoreCase("Previous"))
                    nextPage = page - 1;

                try
                {
                    PlayerListGUI temp = this.plugin.getGUI();
                    
                    if(temp != null)
                    {
                        PlayerListGUI next = temp.getGuiPages().get(nextPage);
    
                        if(next != null)
                        {
                            next.show(player, Bukkit.getOnlinePlayers().size());
        
                            setWatchingPage(player, nextPage);
                        }
                    }
                }
                catch(IndexOutOfBoundsException ex)
                {
                    // TODO
                    // Logger?
                }
            }
        }
        else if(clickedItem != null)
        {
            NamespacedKey key = new NamespacedKey(this.plugin, "IsCustom");
            PersistentDataContainer cont = clickedItem.getItemMeta()
                    .getPersistentDataContainer();
            
            if(cont.has(key, PersistentDataType.STRING))
            {
                key = new NamespacedKey(this.plugin, "Slot");
                
                if(cont.has(key, PersistentDataType.INTEGER))
                {
                    int slot = cont.get(key, PersistentDataType.INTEGER);
                    
                    CustomItem c = this.getCustomItemBySlot(slot);
                    
                    if(c != null)
                    {
                        List<String> cicmds = new ArrayList<>();
                        
                        boolean close = false;
    
                        if(e.isLeftClick())
                        {
                            cicmds = c.getLCC();
                            
                            if(c.closeOnLeft())
                                close = true;
                        }
    
                        if(e.isRightClick())
                        {
                            cicmds = c.getRCC();
    
                            if(c.closeOnRight())
                                close = true;
                        }
    
                        cicmds.forEach(cmd -> sendCommand(player, player, cmd));
                        
                        if(close)
                            player.closeInventory();
                    }
                }
            }
        }
    }
    
    private void sendCommand(Player sender, Player placeholderHolder, String cmd)
    {
        cmd = PlaceholderAPI.setPlaceholders(placeholderHolder, cmd)
                .replace("%sender_name%", sender.getName());
        
        if(cmd.startsWith("["))
        {
            String sendAs = cmd.substring(cmd.indexOf("["), cmd.indexOf("]") + 2);

            cmd = cmd.substring(cmd.indexOf("]") + 2);

            if(sendAs.equalsIgnoreCase("[PLAYER] "))
                Bukkit.dispatchCommand(sender, cmd);
            else if(sendAs.equalsIgnoreCase("[CONSOLE] "))
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        cmd.replace("[CONSOLE] ", ""));
        }
        else Bukkit.dispatchCommand(sender, cmd);
    }
    
    private CustomItem getCustomItemBySlot(int slot)
    {
        List<CustomItem> customItems = this.cfg.getCustomItems();
        
        CustomItem custom = null;
        
        for(CustomItem c : customItems)
            if(c.getItemSlot() == slot)
                custom = c;
        
        return custom;
    }
}
