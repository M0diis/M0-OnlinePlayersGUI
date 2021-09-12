package me.m0dii.onlineplayersgui.inventoryholder;

import me.m0dii.onlineplayersgui.CustomItem;
import me.m0dii.onlineplayersgui.OnlineGUI;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
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
import java.util.stream.Collectors;

public class GUIUtils
{
    OnlineGUI plugin;
    
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
                
                List<Component> lore = c.getLore().stream()
                        .map(str -> Component.text(PlaceholderAPI.setPlaceholders(p, ((TextComponent)str).content())))
                        .collect(Collectors.toList());
            
                m.lore(lore);
            
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
                    double left = Double.parseDouble(PlaceholderAPI.setPlaceholders(p,
                            condSplit.get(0)).replaceAll("[a-zA-Z!@#$&*()/\\\\\\[\\]{}:\"?]", ""));
                    
                    double right = Double.parseDouble(PlaceholderAPI.setPlaceholders(p,
                            condSplit.get(2)).replaceAll("[a-zA-Z!@#$&*()/\\\\\\[\\]{}:\"?]", ""));
                    
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
                OnlineGUI.getInstance().getLogger()
                        .warning("Error occured trying to parse the condition.");
            }
            
            return filtered;
        }
        else
        {
            for(Player p : players)
            {
                String result = PlaceholderAPI.setPlaceholders(p, cond)
                        .toLowerCase();
                
                if(result.equals("yes") || result.equals("true"))
                    filtered.add(p);
            }
        }
        
        return filtered;
    }
}
