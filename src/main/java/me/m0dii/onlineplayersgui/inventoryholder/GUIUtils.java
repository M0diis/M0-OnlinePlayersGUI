package me.m0dii.onlineplayersgui.inventoryholder;

import me.clip.placeholderapi.PlaceholderAPI;
import me.m0dii.onlineplayersgui.CustomItem;
import me.m0dii.onlineplayersgui.OnlineGUI;
import me.m0dii.onlineplayersgui.utils.Messenger;
import me.m0dii.onlineplayersgui.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GUIUtils
{
    final OnlineGUI plugin;
    
    public GUIUtils(OnlineGUI plugin)
    {
        this.plugin = plugin;
    }
    
    public List<Player> getOnline(@Nullable String permission, @Nullable String condition)
    {
        List<Player> online;
        
        List<Player> toggled = plugin.getHiddenPlayersToggled();
        
        if(plugin.getCfg().ESSX_HOOK())
        {
            online = Bukkit.getOnlinePlayers().stream().filter(p ->
                    !p.hasPermission("m0onlinegui.hidden")
                    || !plugin.getEssentials().getUser(p).isVanished()
                    || !toggled.contains(p))
                    .collect(Collectors.toList());
        }
        else
        {
            online = Bukkit.getOnlinePlayers().stream().filter(p ->
                    !p.hasPermission("m0onlinegui.hidden")
                    || !toggled.contains(p))
                    .collect(Collectors.toList());
        }
        
        if(permission != null)
            online = online.stream().filter(p -> p.hasPermission(permission))
                    .collect(Collectors.toList());
        
        if(condition != null)
            return filterByCondition(online, condition);
        
        return online;
    }
    
    public void setCustomItems(Inventory inv, Player p, int size,
                               List<CustomItem> customItems)
    {
        for(CustomItem c : customItems)
        {
            ItemStack item = c.getItem();
            ItemMeta m = item.getItemMeta();
        
            NamespacedKey key = new NamespacedKey(OnlineGUI.getInstance(), "Slot");
            PersistentDataContainer cont = item.getItemMeta().getPersistentDataContainer();
        
            if(cont.has(key, PersistentDataType.INTEGER))
            {
                //noinspection ConstantConditions
                int slot = cont.get(key, PersistentDataType.INTEGER);
                
                List<String> lore = c.getLore().stream()
                        .map(str -> PlaceholderAPI.setPlaceholders(p, str))
                        .collect(Collectors.toList());
            
                m.setLore(lore);
            
                item.setItemMeta(m);
            
                inv.setItem(size - 10 + slot, item);
            }
        }
    }
    
    public List<Player> filterByCondition(List<Player> players, String cond)
    {
        List<Player> filtered = new ArrayList<>();
        
        List<String> condSplit = Arrays.asList(cond.split(" "));
        
        if(condSplit.size() == 3)
        {
            String op = condSplit.get(1);
            
            try
            {
                for(Player p : players)
                {
                    String leftStr = condSplit.get(0);
                    String rightStr = condSplit.get(2);
    
                    if(Objects.equals(op, "=") || Objects.equals(op, "=="))
                    {
                        if(!Utils.isDigit(leftStr) && !Utils.isDigit(rightStr))
                        {
                            if(leftStr.equalsIgnoreCase(rightStr))
                                filtered.add(p);
                            
                            continue;
                        }
                    }
                    
                    double left = Double.parseDouble(PlaceholderAPI.setPlaceholders(p, leftStr)
                            .replaceAll("[a-zA-Z!@#$&*()/\\\\\\[\\]{}:\"?]", ""));
                    
                    double right = Double.parseDouble(PlaceholderAPI.setPlaceholders(p, rightStr)
                            .replaceAll("[a-zA-Z!@#$&*()/\\\\\\[\\]{}:\"?]", ""));
                    
                    switch(op)
                    {
                        case ">":
                            if(left > right) filtered.add(p);
                            break;
                        
                        case "<":
                            if(left < right) filtered.add(p);
                            break;
                        
                        case "<=":
                            if(left <= right) filtered.add(p);
                            break;
                        
                        case ">=":
                            if(left >= right) filtered.add(p);
                            break;
                        
                        case "=":
                        case "==":
                            if(left == right) filtered.add(p);
                            break;
                        
                        case "!=":
                            if(left != right) filtered.add(p);
                            break;
                        
                        default:
                            break;
                    }
                }
            }
            catch(NumberFormatException ex)
            {
                Messenger.warn("Error occured trying to parse the condition.");
            }
            
            return filtered;
        }
        else
        {
            for(Player p : players)
            {
                String result = PlaceholderAPI.setPlaceholders(p, cond).toLowerCase();
                
                if(result.equals("yes") || result.equals("true"))
                    filtered.add(p);
            }
        }
        
        return filtered;
    }
}
