package me.M0dii.OnlinePlayersGUI;

import me.M0dii.OnlinePlayersGUI.Commands.OnlineGUICommand;
import me.M0dii.OnlinePlayersGUI.Listeners.InventoryListener;
import me.M0dii.OnlinePlayersGUI.Utils.Config;
import me.M0dii.OnlinePlayersGUI.Utils.UpdateChecker;
import net.ess3.api.IEssentials;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.CustomChart;
import org.bstats.charts.MultiLineChart;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OnlineGUI extends JavaPlugin
{
    public static OnlineGUI instance;
    
    private final PluginManager manager;

    public OnlineGUI()
    {
        this.manager = getServer().getPluginManager();
        
        this.hiddenPlayersToggled = new ArrayList<>();
        
        this.cfg = new Config(this);
    }
    
    private ConditionalGUIs cgis;
    
    public ConditionalGUIs getCgis()
    {
        return this.cgis;
    }
    
    private List<Player> hiddenPlayersToggled;
    
    public void toggleHiddenPlayer(Player p)
    {
        if(hiddenPlayersToggled == null)
            this.hiddenPlayersToggled = new ArrayList<>();
            
        if(hiddenPlayersToggled.contains(p))
            hiddenPlayersToggled.remove(p);
        else hiddenPlayersToggled.add(p);
    }
    
    public List<Player> getHiddenPlayersToggled()
    {
        return this.hiddenPlayersToggled;
    }
    
    private File configFile = null;
    
    private final Config cfg;
    
    public Config getCfg()
    {
        return this.cfg;
    }
    
    private IEssentials ess = null;

    public void renewConfig()
    {
        this.configFile = new File(this.getDataFolder(), "config.yml");
        YamlConfiguration.loadConfiguration(this.configFile);
        
        this.cfg.reload();
    }
    
    public IEssentials getEssentials()
    {
        return this.ess;
    }
    
    public void onEnable()
    {
        instance = this;
        
        this.prepareConfig();
        
        this.cfg.load();
        
        cgis = new ConditionalGUIs(this);
    
        registerHooks();

        this.manager.registerEvents(new InventoryListener(this), this);
    
        PluginCommand cmd = this.getCommand("online");
        
        if(cmd != null)
            cmd.setExecutor(new OnlineGUICommand(this));
        
        info("  __  __  ___  ");
        info(" |  \\/  |/ _ \\ ");
        info(" | \\  / | | | |");
        info(" | |\\/| | | | |");
        info(" | |  | | |_| |");
        info(" |_|  |_|\\___/");
        info(" ");
        info("M0-OnlinePlayersGUI has been successfully enabled!");
        info("");
    
        setupMetrics();
        
        checkForUpdates();
    }
    
    private void setupMetrics()
    {
        Metrics metrics = new Metrics(this, 10924);
    
        CustomChart c = new MultiLineChart("players_and_servers", () ->
        {
            Map<String, Integer> valueMap = new HashMap<>();
        
            valueMap.put("servers", 1);
            valueMap.put("players", Bukkit.getOnlinePlayers().size());
        
            return valueMap;
        });
    
        metrics.addCustomChart(c);
    }
    
    private void checkForUpdates()
    {
        new UpdateChecker(this, 86813).getVersion(ver ->
        {
            if (!this.getDescription().getVersion().equalsIgnoreCase(
                    ver.replace("v", "")))
            {
                info("You are running an outdated version of M0-CoreCord.");
                info("You can download the latest version on Spigot:");
                info("https://www.spigotmc.org/resources/86813/");
            }
        });
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
    
        YamlConfiguration.loadConfiguration(this.configFile);
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
