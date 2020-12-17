package me.M0dii.OnlinePlayersGUI;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class Config
{
    public static String HEAD_NAME;
    public static List<String> HEAD_LORE;

    public static void load(Main plugin)
    {
        FileConfiguration cfg = plugin.getConfig();
    
        HEAD_NAME = format(cfg.getString("M0-OnlinePlayersGUI.Name"));
        HEAD_LORE = cfg.getStringList("M0-OnlinePlayersGUI.Lore");
    }
    
    private static String format(String text)
    {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
