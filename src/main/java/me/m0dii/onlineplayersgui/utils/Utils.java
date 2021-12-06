package me.m0dii.onlineplayersgui.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import me.m0dii.onlineplayersgui.OnlineGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.regex.Pattern;

public class Utils
{
    static final Pattern HEX_PATTERN = Pattern.compile("#([A-Fa-f0-9])([A-Fa-f0-9])([A-Fa-f0-9])([A-Fa-f0-9])([A-Fa-f0-9])([A-Fa-f0-9])");
    
    public static String format(String text)
    {
        if(text == null || text.isEmpty())
            return text;
    
        return ChatColor.translateAlternateColorCodes('&',
                HEX_PATTERN.matcher(text).replaceAll("&x&$1&$2&$3&$4&$5&$6"));
    }
    
    public static boolean isDigit(String str)
    {
        try
        {
            Double.parseDouble(str);
        }
        catch(NumberFormatException ex)
        {
            return false;
        }
        
        return true;
    }
    
    public static String setPlaceholders(String text, @Nullable Player player)
    {
        String replaced = text.replaceAll("%([pP]layer|[pP]layer(_|.*)[nN]ame)%", player.getName());
    
        if(OnlineGUI.PAPI)
        {
            replaced = PlaceholderAPI.setPlaceholders(player, replaced);
        }
        
        return format(replaced);
    }
    
    public static void sendCommand(Player sender, Player placeholderHolder, String cmd)
    {
        cmd = cmd.replace("%sender_name%", sender.getName());
        
        cmd = setPlaceholders(cmd, placeholderHolder);
        
        if(cmd.startsWith("[CLOSE]"))
            return;
        
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
}
