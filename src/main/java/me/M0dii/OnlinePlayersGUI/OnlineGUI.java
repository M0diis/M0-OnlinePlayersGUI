package me.M0dii.OnlinePlayersGUI;

import net.ess3.api.IEssentials;
import org.bstats.charts.CustomChart;
import org.bstats.charts.MultiLineChart;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bstats.bukkit.Metrics;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class OnlineGUI extends JavaPlugin
{
    private final PluginManager manager;
    
    public OnlineGUI()
    {
        this.manager = getServer().getPluginManager();
        
        this.cfg = new Config(this);
    }
    
    private FileConfiguration fileCfg = null;
    private File configFile = null;
    
    private final Config cfg;
    
    public Config getCfg()
    {
        return this.cfg;
    }
    
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
        this.fileCfg = YamlConfiguration.loadConfiguration(this.configFile);
        
        this.cfg.reload();
    }
    
    public IEssentials getEssentials()
    {
        return this.ess;
    }
    
    public void onEnable()
    {
        this.prepareConfig();
    
        this.removeOldKeys();
    
        this.cfg.load();
    
        registerHooks();
    
        this.manager.registerEvents(new GUIListener(this), this);
    
        PluginCommand cmd = this.getCommand("online");
        
        if(cmd != null)
            cmd.setExecutor(new CommandHandler(this));
        
        Metrics metrics = new Metrics(this, 10924);
    
        CustomChart c = new MultiLineChart("players_and_servers", () ->
        {
            Map<String, Integer> valueMap = new HashMap<>();
            
            valueMap.put("servers", 1);
            valueMap.put("players", Bukkit.getOnlinePlayers().size());
            
            return valueMap;
        });
        
        metrics.addCustomChart(c);
        
        info("  __  __  ___  ");
        info(" |  \\/  |/ _ \\ ");
        info(" | \\  / | | | |");
        info(" | |\\/| | | | |");
        info(" | |  | | |_| |");
        info(" |_|  |_|\\___/");
        info(" ");
        info("M0-OnlinePlayersGUI has been successfully enabled!");
        info("");
    }
    
    private void registerHooks()
    {
        if(this.cfg.ESSX_HOOK())
        {
            this.ess = (IEssentials)this.manager.getPlugin("Essentials");
            
            if(this.ess == null)
                this.warning("Could not find EssentialsX.");
        }
        
        if (this.manager.getPlugin("PlaceholderAPI") == null)
        {
            this.warning("Could not find PlaceholderAPI! This plugin is required.");
            this.warning("Disabling M0-OnlinePlayersGUI..");
            
            this.manager.disablePlugin(this);
        }
    }
    
    public void onDisable()
    {
        info("");
        info("M0-OnlinePlayersGUI has been successfully disabled!");
        info("");
        
        if(this.isEnabled())
            this.manager.disablePlugin(this);
    }
    
    private void prepareConfig()
    {
        this.configFile = new File(this.getDataFolder(), "config.yml");
        
        if(!this.configFile.exists())
        {
            //noinspection ResultOfMethodCallIgnored
            this.configFile.getParentFile().mkdirs();
            
            this.copy(this.getResource("config.yml"), this.configFile);
        }
        
        try
        {
            this.getConfig().options().copyDefaults(true);
            this.getConfig().save(this.configFile);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        
        this.fileCfg = YamlConfiguration.loadConfiguration(this.configFile);
    }
    
    public void removeOldKeys()
    {
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            
            String cfgV = getConfig().getString(
                    "M0-OnlinePlayersGUI.config-version");
            
            String curr = "1.1";
    
            if(cfgV == null || !cfgV.equalsIgnoreCase(curr))
            {
                boolean prevLC = getConfig().getBoolean(
                        "M0-OnlinePlayersGUI.CloseOnLeftClick");
    
                boolean prevRC = getConfig().getBoolean(
                        "M0-OnlinePlayersGUI.CloseOnRightClick");
                
                getConfig().set("M0-OnlinePlayersGUI.CloseOnLeftClick", "unused");
                getConfig().set("M0-OnlinePlayersGUI.CloseOnRightClick", "unused");
    
                getConfig().set("M0-OnlinePlayersGUI.CloseOnLeftClick", null);
                getConfig().set("M0-OnlinePlayersGUI.CloseOnRightClick", null);
    
                getConfig().set("M0-OnlinePlayersGUI.GUI.CloseOn.LeftClick", prevLC);
                getConfig().set("M0-OnlinePlayersGUI.GUI.CloseOn.RightClick", prevRC);
    
                getConfig().set("M0-OnlinePlayersGUI.config-version", curr);
    
                try
                {
                    getConfig().save(configFile);
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
        },20L);
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
                    out.write(buf, 0, len);
        
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
