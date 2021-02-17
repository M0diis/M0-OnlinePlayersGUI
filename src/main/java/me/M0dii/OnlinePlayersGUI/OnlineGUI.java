package me.M0dii.OnlinePlayersGUI;

import net.ess3.api.IEssentials;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class OnlineGUI extends JavaPlugin
{
    public OnlineGUI plugin;
    
    private final PluginManager manager;
    
    public OnlineGUI()
    {
        this.manager = getServer().getPluginManager();
        
        this.config = new Config();
    }
    
    FileConfiguration cfg = null;
    File configFile = null;
    
    private Config config;
    
    private IEssentials ess = null;
    private PlayerListGUI playerListGUI = null;
    
    public PlayerListGUI getGUI()
    {
        return this.playerListGUI;
    }
    
    public void setGUI(PlayerListGUI gui)
    {
        this.playerListGUI = gui;
    }
    
    public void renewConfig()
    {
        this.configFile = new File(this.getDataFolder(), "config.yml");
        this.cfg = YamlConfiguration.loadConfiguration(this.configFile);
        
        this.config.load(this, this.cfg);
    }
    
    public IEssentials getEssentials()
    {
        return this.ess;
    }
    
    public void onEnable()
    {
        this.configFile = new File(this.getDataFolder(), "config.yml");
        
        this.plugin = this;
    
        if(!this.configFile.exists())
        {
            //noinspection ResultOfMethodCallIgnored
            this.configFile.getParentFile().mkdirs();
        
            this.copy(this.getResource("config.yml"), configFile);
        }
        
        this.cfg = YamlConfiguration.loadConfiguration(this.configFile);
        
        this.config.load(this, this.cfg);
        
        if(this.config.ESSX_HOOK())
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
    
    @NotNull
    public Config getCfg()
    {
        return this.config;
    }
    
    public FileConfiguration getFileConfig()
    {
        return this.cfg;
    }
}
