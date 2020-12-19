package me.M0dii.OnlinePlayersGUI;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Main extends JavaPlugin
{
    public static Main plugin;
    
    private final PluginManager manager;
    
    private boolean loaded;
    
    public Main()
    {
        this.manager = getServer().getPluginManager();
    }
    
    FileConfiguration config = null;
    File configFile = null;
    
    public void onEnable()
    {
        this.configFile = new File(getDataFolder(), "config.yml");
        this.config = YamlConfiguration.loadConfiguration(this.configFile);
        
        plugin = this;
    
        if(!this.configFile.exists())
        {
            this.configFile.getParentFile().mkdirs();
        
            copy(getResource("config.yml"), configFile);
        }
        
        Config.load(this);
    
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null)
        {
            this.getLogger().warning("Could not find PlaceholderAPI! This plugin is required.");
            
            Bukkit.getPluginManager().disablePlugin(this);
        }
        
        this.manager.registerEvents(new GUIListener(this), this);
        
        this.getCommand("online").setExecutor(new CommandHandler(this));
    }
    
    public static Main getInstance() {
        return plugin;
    }
    
    private void copy(InputStream in, File file)
    {
        if(in == null)
        {
            this.getLogger().warning("Cannot copy, resource null");
            
            return;
        }
        
        try
        {
            OutputStream out = new FileOutputStream(file);
            
            byte[] buf = new byte[1024];
            
            int len;
            
            while((len = in.read(buf)) > 0)
            {
                out.write(buf, 0, len);
            }
            
            out.close();
            in.close();
        }
        catch(Exception e)
        {
            this.getLogger().warning("Error copying resource: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    public void onDisable()
    {
        this.manager.disablePlugin(this);
    }
}
