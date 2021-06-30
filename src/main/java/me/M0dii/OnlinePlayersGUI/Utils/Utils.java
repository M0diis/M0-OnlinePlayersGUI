package me.M0dii.OnlinePlayersGUI.Utils;

import org.bukkit.ChatColor;

public class Utils
{
    public static String format(String text)
    {
        if(text == null || text.isEmpty())
            return text;
        
        return ChatColor.translateAlternateColorCodes('&', text);
    }
    
    public static String clearFormat(String text)
    {
        return ChatColor.stripColor(text);
    }
}
