package me.M0dii.OnlinePlayersGUI;

import org.bukkit.ChatColor;

public class Utils
{
    public static String format(String text)
    {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
    
    public static String clearFormat(String text)
    {
        return ChatColor.stripColor(text);
    }
}
