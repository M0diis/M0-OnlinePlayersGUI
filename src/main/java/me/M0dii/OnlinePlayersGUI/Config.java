package me.M0dii.OnlinePlayersGUI;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class Config
{
    public static String HEAD_NAME;
    public static String CONFIG_RELOADED;
    public static String NO_PERMISSION;
    
    public static String PREVIOUS_PAGE_NAME;
    public static String NEXT_PAGE_NAME;
    public static String GUI_TITLE;
    
    public static List<String> HEAD_LORE;
    public static List<String> LEFT_CLICK_COMMANDS;
    public static List<String> RIGHT_CLICK_COMMANDS;
    
    public static List<String> PREVIOUS_PAGE_LORE;
    public static List<String> NEXT_PAGE_LORE;
    
    public static boolean UPDATE_ON_JOIN;
    public static boolean UPDATE_ON_LEAVE;
    public static boolean HIDE_BUTTONS_ON_SINGLE;
    public static int GUI_SIZE;
    
    public static Material PREVIOUS_PAGE_MATERIAL;
    public static Material NEXT_PAGE_MATERIAL;

    public static void load(Main plugin)
    {
        FileConfiguration cfg = plugin.getConfig();
    
        UPDATE_ON_JOIN = cfg.getBoolean("M0-OnlinePlayersGUI.GUI.UpdateOn.Join");
        UPDATE_ON_LEAVE = cfg.getBoolean("M0-OnlinePlayersGUI.GUI.UpdateOn.Leave");
        
        HIDE_BUTTONS_ON_SINGLE = cfg.getBoolean("M0-OnlinePlayersGUI.HideButtonsOnSinglePage");
        
        HEAD_NAME = format(cfg.getString("M0-OnlinePlayersGUI.PlayerDisplay.Name"));
        HEAD_LORE = cfg.getStringList("M0-OnlinePlayersGUI.PlayerDisplay.Lore");
    
        GUI_TITLE = format(cfg.getString("M0-OnlinePlayersGUI.GUI.Title"));
        
        NO_PERMISSION = format(cfg.getString("M0-OnlinePlayersGUI.NoPermission"));
        CONFIG_RELOADED = format(cfg.getString("M0-OnlinePlayersGUI.ReloadMessage"));
        
        LEFT_CLICK_COMMANDS = cfg.getStringList("M0-OnlinePlayersGUI.PlayerDisplay.Commands.Left-Click");
        RIGHT_CLICK_COMMANDS = cfg.getStringList("M0-OnlinePlayersGUI.PlayerDisplay.Commands.Right-Click");
        
        GUI_SIZE = cfg.getInt("M0-OnlinePlayersGUI.GUI.Size");
    
        PREVIOUS_PAGE_MATERIAL = Material.getMaterial(cfg.getString("M0-OnlinePlayersGUI.PreviousButton.Material"));
        if(PREVIOUS_PAGE_MATERIAL == null) PREVIOUS_PAGE_MATERIAL = Material.BOOK;
        
        NEXT_PAGE_MATERIAL =Material.getMaterial(cfg.getString("M0-OnlinePlayersGUI.NextButton.Material"));
        if(NEXT_PAGE_MATERIAL == null) NEXT_PAGE_MATERIAL = Material.BOOK;
        
        PREVIOUS_PAGE_LORE = cfg.getStringList("M0-OnlinePlayersGUI.PreviousButton.Lore");
        NEXT_PAGE_LORE = cfg.getStringList("M0-OnlinePlayersGUI.NextButton.Lore");
        
        PREVIOUS_PAGE_NAME = format(cfg.getString("M0-OnlinePlayersGUI.PreviousButton.Name"));
        NEXT_PAGE_NAME = format(cfg.getString("M0-OnlinePlayersGUI.NextButton.Name"));
    }
    
    private static String format(String text)
    {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
