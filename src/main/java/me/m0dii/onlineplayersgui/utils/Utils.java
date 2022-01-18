package me.m0dii.onlineplayersgui.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import me.m0dii.onlineplayersgui.OnlineGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
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
        if(OnlineGUI.PAPI)
        {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }
        
        if(player != null)
        {
            text =  text.replaceAll("%([pP]layer|[pP]layer(_|.*)[nN]ame)%", player.getName());
        }
        
        return format(text);
    }
    
    public static void sendCommand(Player sender, Player placeholderHolder, String cmd)
    {
        cmd = cmd.replace("%sender_name%", sender.getName());
        
        cmd = setPlaceholders(cmd, placeholderHolder);
        
        if(cmd.startsWith("[CLOSE]"))
            return;
        
        if(cmd.startsWith("["))
        {
            String sendAs = cmd.substring(cmd.indexOf("["), cmd.indexOf("]") + 1).toUpperCase();
            
            cmd = cmd.substring(cmd.indexOf("]") + 2);
    
            Messenger.info(sendAs);
            Messenger.info(cmd);
    
            if(sendAs.equalsIgnoreCase("[MESSAGE]") || sendAs.equalsIgnoreCase("[TEXT]"))
            {
                sender.sendMessage(cmd);
            }
            else if(sendAs.equalsIgnoreCase("[TITLE]"))
            {
                String[] split = cmd.split(", ");
        
                int fadeIn = 20;
                int stay = 60;
                int fadeOut = 20;
    
                switch(split.length)
                {
                    case 1 -> sender.sendTitle(split[0], "", fadeIn, stay, fadeOut);
                    case 2 -> sender.sendTitle(split[0], split[1], fadeIn, stay, fadeOut);
                    case 4 -> {
                        try
                        {
                            fadeIn = Integer.parseInt(split[1]);
                            stay = Integer.parseInt(split[2]);
                            fadeOut = Integer.parseInt(split[3]);
                        }
                        catch(NumberFormatException ex)
                        {
                            Messenger.warn("Invalid fadeIn, stay, or fadeOut time for title action.");
                        }
                        sender.sendTitle(split[0], "", fadeIn, stay, fadeOut);
                    }
                    case 5 -> {
                        String subtitle = split[1];
                        try
                        {
                            fadeIn = Integer.parseInt(split[2]);
                            stay = Integer.parseInt(split[3]);
                            fadeOut = Integer.parseInt(split[4]);
                        }
                        catch(NumberFormatException ex)
                        {
                            Messenger.warn("Invalid fadeIn, stay, or fadeOut time for title action.");
                        }
                        sender.sendTitle(split[0], subtitle, fadeIn, stay, fadeOut);
                    }
                }
            }
            else if(sendAs.equalsIgnoreCase("[CHAT]"))
            {
                sender.chat(cmd);
            }
            else if(sendAs.equalsIgnoreCase("[SOUND]"))
            {
                String[] split = cmd.split(", ");
        
                if(split.length == 2)
                {
                    try
                    {
                        sender.playSound(sender.getLocation(), Sound.valueOf(split[0]), Float.parseFloat(split[1]), Float.parseFloat(split[1]));
                    }
                    catch (Exception ex)
                    {
                        Messenger.warn("Invalid sound format: " + cmd);
                    }
                }
            }
            else if(sendAs.startsWith("[PLAYER]"))
                Bukkit.dispatchCommand(sender, cmd);
            else if(sendAs.startsWith("[CONSOLE]"))
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }
        else Bukkit.dispatchCommand(sender, cmd);
    }
    
    public static String clearFormat(String text)
    {
        return ChatColor.stripColor(text);
    }
}
