package me.M0dii.OnlinePlayersGUI.Utils;

import me.M0dii.OnlinePlayersGUI.OnlineGUI;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils
{
    public static String format(String text)
    {
        if(text == null || text.isEmpty())
            return text;
        
        return ChatColor.translateAlternateColorCodes('&', text);
    }
    
    public static void sendCommand(Player sender, Player placeholderHolder, String cmd)
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
    
    public static String clearFormat(String text)
    {
        return ChatColor.stripColor(text);
    }
    
    public static List<Player> filterByCondition(List<Player> players, String cond)
    {
        List<Player> filtered = new ArrayList<>();
        
        List<String> condSplit = Arrays.asList(cond.split(" "));
        
        try
        {
            if(condSplit.size() == 3)
            {
                String op = condSplit.get(1);
    
                double right = Double.parseDouble(condSplit.get(2));
                
                for(Player p : players)
                {
                    double left = Double.parseDouble(
                            PlaceholderAPI.setPlaceholders(p, condSplit.get(0))
                                    .replaceAll("[a-zA-Z!@#$%&*()/\\\\\\[\\]{}:\"?]", ""));
                    switch (op)
                    {
                        case ">":
                            if(left > right)
                                filtered.add(p);
                            break;
                        
                        case "<":
                            if(left < right)
                                filtered.add(p);
                            break;
                        
                        case "<=":
                            if(left <= right)
                                filtered.add(p);
                            break;
                        
                        case ">=":
                            if(left >= right)
                                filtered.add(p);
                            break;
                        
                        case "=": case "==":
                        if(left == right)
                            filtered.add(p);
                        break;
                        
                        case "!=":
                            if(left != right)
                                filtered.add(p);
                            break;
                        
                        default:
                            break;
                    }
                }
                
            }
            
            return filtered;
        }
        catch(NumberFormatException ex)
        {
            OnlineGUI.instance.getLogger().warning("Error occured trying to parse the condition.");
        }
        
        for(Player p : players)
        {
            String result = PlaceholderAPI.setPlaceholders(p, cond)
                    .toLowerCase();
            
            if(result.equals("yes") || result.equals("true"))
                filtered.add(p);
        }
        
        return filtered;
    }
}
