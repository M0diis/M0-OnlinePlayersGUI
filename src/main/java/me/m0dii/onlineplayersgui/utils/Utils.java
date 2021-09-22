package me.m0dii.onlineplayersgui.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class Utils
{
    private static final Pattern HEX_PATTERN = Pattern.compile("#([A-Fa-f0-9])([A-Fa-f0-9])([A-Fa-f0-9])([A-Fa-f0-9])([A-Fa-f0-9])([A-Fa-f0-9])");
    
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
    
    public static void sendCommand(Player sender, Player placeholderHolder, String cmd)
    {
        cmd = PlaceholderAPI.setPlaceholders(placeholderHolder, cmd)
                .replace("%sender_name%", sender.getName());
        
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
