package me.M0dii.OnlinePlayersGUI.InventoryHolder;

import me.M0dii.OnlinePlayersGUI.CustomItem;
import me.M0dii.OnlinePlayersGUI.OnlineGUI;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GUIUtils
{
    public void setCustomItems(Inventory inv, Player p, int size, List<CustomItem> customItems)
    {
        for(CustomItem c : customItems)
        {
            ItemStack item = c.getItem();
            ItemMeta m = item.getItemMeta();
        
            NamespacedKey key = new NamespacedKey(OnlineGUI.instance, "Slot");
            PersistentDataContainer cont = item.getItemMeta().getPersistentDataContainer();
        
            if(cont.has(key, PersistentDataType.INTEGER))
            {
                //noinspection ConstantConditions
                int slot = cont.get(key, PersistentDataType.INTEGER);
            
                List<String> lore = c.getLore();
            
                List<String> newLore = new ArrayList<>();
            
                for(String l : lore)
                    newLore.add(PlaceholderAPI.setPlaceholders(p, l));
            
                m.setLore(newLore);
            
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
                    double left = Double.parseDouble(PlaceholderAPI.setPlaceholders(p, condSplit.get(0)).replaceAll("[a-zA-Z!@#$&*()/\\\\\\[\\]{}:\"?]", ""));
                    
                    double right = Double.parseDouble(PlaceholderAPI.setPlaceholders(p, condSplit.get(2)).replaceAll("[a-zA-Z!@#$&*()/\\\\\\[\\]{}:\"?]", ""));
                    
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
                OnlineGUI.instance.getLogger().warning("Error occured trying to parse the condition.");
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
