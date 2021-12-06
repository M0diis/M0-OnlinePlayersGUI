package me.m0dii.onlineplayersgui.utils;

import me.m0dii.onlineplayersgui.OnlineGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Messenger
{
    private static final OnlineGUI plugin = OnlineGUI.getInstance();
    
    public static void sendf(CommandSender s, String message)
    {
        s.sendMessage(format(message));
    }
    
    public static void sendfr(CommandSender s, String message, String what, Object to)
    {
        sendf(s, replace(message, what, to));
    }
    
    public static String replace(String in, String what, Object to)
    {
        return in.replace(what, String.valueOf(to));
    }
    
    public static String format(String text)
    {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
    
    public static void debug(String msg)
    {
        if(plugin.getCfg().DEBUG_ENABLED())
        {
            String prefix = "&3[&bOnlineGUI - DEBUG&3]&r ";
            
            Bukkit.getConsoleSender().sendMessage(format(prefix + msg));
        }
    }
    
    public static void info(String msg)
    {
        String prefix = "&2[&aOnlineGUI - INFO&2]&r ";
        
        Bukkit.getConsoleSender().sendMessage(format(prefix + msg));
    }
    
    public static void warn(String msg)
    {
        String prefix = "&6[&eOnlineGUI - WARN&6]&r ";
        
        Bukkit.getConsoleSender().sendMessage(format(prefix + msg));
    }
    
    public static void error(String msg)
    {
        String prefix = "&4[&cOnlineGUI - ERROR&4]&r ";
        
        Bukkit.getConsoleSender().sendMessage(format(prefix + msg));
    }
}