package me.M0dii.OnlinePlayersGUI;

import net.ess3.api.IEssentials;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class Main extends JavaPlugin
{
    public static Main plugin;
    
    private final PluginManager manager;
    
    public Main()
    {
        this.manager = getServer().getPluginManager();
    }
    
    FileConfiguration config = null;
    File configFile = null;
    
    private IEssentials ess = null;
    
    public IEssentials getEssentials()
    {
        return this.ess;
    }
    
    public void onEnable()
    {
        this.configFile = new File(this.getDataFolder(), "config.yml");
        this.config = YamlConfiguration.loadConfiguration(this.configFile);
        
        plugin = this;
    
        if(!this.configFile.exists())
        {
            //noinspection ResultOfMethodCallIgnored
            this.configFile.getParentFile().mkdirs();
        
            this.copy(this.getResource("config.yml"), configFile);
        }
        
        Config.load(this);
        
        if(Config.ESSENTIALSX_HOOK)
        {
            this.ess = (IEssentials)this.manager.getPlugin("Essentials");
            
            if(ess == null)
            {
                this.warning("Could not find EssentialsX.");
            }
        }
    
        if (this.manager.getPlugin("PlaceholderAPI") == null)
        {
            this.warning("Could not find PlaceholderAPI! This plugin is required.");
            
            this.manager.disablePlugin(this);
        }
        
        this.manager.registerEvents(new GUIListener(this), this);
        
        Objects.requireNonNull(this.getCommand("online")).setExecutor(new CommandHandler(this));
    
        info("");
        info("+-----------------------------------------+");
        info(" ");
        info("  __  __  ___  ");
        info(" |  \\/  |/ _ \\ ");
        info(" | \\  / | | | |");
        info(" | |\\/| | | | |");
        info(" | |  | | |_| |");
        info(" |_|  |_|\\___/");
        info(" ");
        info("M0-OnlinePlayersGUI has been successfully enabled!");
        info(" ");
        info("+-----------------------------------------+");
        info("");
    }
    
    
    public void onDisable()
    {
        info("");
        info("+-----------------------------------------+");
        info(" ");
        info("  __  __  ___  ");
        info(" |  \\/  |/ _ \\ ");
        info(" | \\  / | | | |");
        info(" | |\\/| | | | |");
        info(" | |  | | |_| |");
        info(" |_|  |_|\\___/");
        info(" ");
        info("M0-OnlinePlayersGUI has been successfully disabled!");
        info(" ");
        info("+-----------------------------------------+");
        info("");
        
        this.manager.disablePlugin(this);
    }
    
    
    private void info(String message)
    {
        this.getLogger().info(message);
    }
    
    private void warning(String message)
    {
        this.getLogger().warning(message);
    }
    
    public static Main getInstance()
    {
        return plugin;
    }
    
    private void copy(InputStream in, File file)
    {
        if(in != null)
        {
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
                this.warning("Error copying resource: " + e.getMessage());
        
                e.printStackTrace();
            }
        }
    }
}
