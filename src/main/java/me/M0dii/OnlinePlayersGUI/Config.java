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
    
    public static List<String> HEAD_LORE;
    public static List<String> LEFT_CLICK_COMMANDS;
    public static List<String> RIGHT_CLICK_COMMANDS;
    public static boolean UPDATE_ON_JOIN;
    public static boolean UPDATE_ON_LEAVE;
    public static boolean HIDE_BUTTONS_ON_SINGLE;
    public static int GUI_SIZE;
    
    public static Material PREVIOUS_PAGE_MATERIAL;
    public static Material NEXT_PAGE_MATERIAL;

    public static void load(Main plugin)
    {
        FileConfiguration cfg = plugin.getConfig();
    
        UPDATE_ON_JOIN = cfg.getBoolean("M0-OnlinePlayersGUI.UpdateOnJoin");
        UPDATE_ON_LEAVE = cfg.getBoolean("M0-OnlinePlayersGUI.UpdateOnLeave");
        HIDE_BUTTONS_ON_SINGLE = cfg.getBoolean("M0-OnlinePlayersGUI.HideButtonsOnSinglePage");
        
        HEAD_NAME = format(cfg.getString("M0-OnlinePlayersGUI.Name"));
        HEAD_LORE = cfg.getStringList("M0-OnlinePlayersGUI.Lore");
        
        NO_PERMISSION = format(cfg.getString("M0-OnlinePlayersGUI.NoPermission"));
        CONFIG_RELOADED = format(cfg.getString("M0-OnlinePlayersGUI.ReloadMessage"));
    
        PREVIOUS_PAGE_NAME = format(cfg.getString("M0-OnlinePlayersGUI.PreviousPageName"));
        NEXT_PAGE_NAME = format(cfg.getString("M0-OnlinePlayersGUI.NextPageName"));
        
        LEFT_CLICK_COMMANDS = cfg.getStringList("M0-OnlinePlayersGUI.Commands.Left-Click");
        RIGHT_CLICK_COMMANDS = cfg.getStringList("M0-OnlinePlayersGUI.Commands.Right-Click");
        
        GUI_SIZE = cfg.getInt("M0-OnlinePlayersGUI.GUISize");
    
        PREVIOUS_PAGE_MATERIAL = Material.getMaterial(cfg.getString("M0-OnlinePlayersGUI.PreviousPageMaterial"));
        NEXT_PAGE_MATERIAL =Material.getMaterial(cfg.getString("M0-OnlinePlayersGUI.NextPageMaterial"));
    }
    
    private static String format(String text)
    {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
