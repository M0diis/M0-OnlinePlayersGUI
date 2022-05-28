package me.m0dii.onlineplayersgui;

import me.m0dii.onlineplayersgui.commands.OnlineGUICommand;
import me.m0dii.onlineplayersgui.inventoryholder.GUIUtils;
import me.m0dii.onlineplayersgui.listeners.InventoryListener;
import me.m0dii.onlineplayersgui.utils.Config;
import me.m0dii.onlineplayersgui.utils.Messenger;
import me.m0dii.onlineplayersgui.utils.UpdateChecker;
import net.ess3.api.IEssentials;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.CustomChart;
import org.bstats.charts.MultiLineChart;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OnlineGUI extends JavaPlugin {
    private static OnlineGUI instance;

    public static OnlineGUI getInstance() {
        return instance;
    }

    private PluginManager manager;

    protected OnlineGUI(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    public OnlineGUI() {
        super();
    }

    private ConditionalGUIs cgis;

    public ConditionalGUIs getCgis() {
        return this.cgis;
    }

    private List<Player> hiddenPlayersToggled;

    public void toggleHiddenPlayer(Player p) {
        if (hiddenPlayersToggled == null) {
            this.hiddenPlayersToggled = new ArrayList<>();
        }

        if (hiddenPlayersToggled.contains(p)) {
            hiddenPlayersToggled.remove(p);
        }
        else {
            hiddenPlayersToggled.add(p);
        }
    }

    public List<Player> getHiddenPlayersToggled() {
        return this.hiddenPlayersToggled;
    }

    private File configFile = null;

    private Config cfg;

    public Config getCfg() {
        return this.cfg;
    }

    private IEssentials ess = null;

    public void renewConfig() {
        this.configFile = new File(this.getDataFolder(), "config.yml");
        YamlConfiguration.loadConfiguration(this.configFile);

        this.cfg.reload();
    }

    public IEssentials getEssentials() {
        return this.ess;
    }

    private GUIUtils guiUtils;

    public GUIUtils getGuiUtils() {
        return this.guiUtils;
    }

    public void onEnable() {
        instance = this;

        this.manager = getServer().getPluginManager();

        this.hiddenPlayersToggled = new ArrayList<>();

        this.cfg = new Config(this);

        this.prepareConfig();

        cgis = new ConditionalGUIs(this);

        guiUtils = new GUIUtils(this);

        registerHooks();

        this.manager.registerEvents(new InventoryListener(this), this);

        PluginCommand cmd = this.getCommand("online");

        if (cmd != null) {
            cmd.setExecutor(new OnlineGUICommand(this));
        }

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

    private void setupMetrics() {
        Metrics metrics = new Metrics(this, 10924);

        CustomChart c = new MultiLineChart("players_and_servers", () -> {
            Map<String, Integer> valueMap = new HashMap<>();

            valueMap.put("servers", 1);
            valueMap.put("players", Bukkit.getOnlinePlayers().size());

            return valueMap;
        });

        metrics.addCustomChart(c);
    }

    private void checkForUpdates() {
        new UpdateChecker(this, 86813).getVersion(ver -> {
            if (!this.getDescription().getVersion().equalsIgnoreCase(ver.replace("v", ""))) {
                Messenger.info("You are running an outdated version of M0-OnlinePlayersGUI.");
                Messenger.info("You can download the latest version on Spigot:");
                Messenger.info("https://www.spigotmc.org/resources/86813/");
            }
        });
    }

    public static boolean PAPI = true;

    private void registerHooks() {
        if (this.cfg.ESSX_HOOK()) {
            this.ess = (IEssentials) this.manager.getPlugin("Essentials");

            if (this.ess == null) {
                Messenger.warn("EssX hook is enabled but could not find EssentialsX plugin.");
            }
        }

        if (this.manager.getPlugin("PlaceholderAPI") == null) {
            Messenger.warn("Could not find PlaceholderAPI! Placeholders will not work.");

            PAPI = false;
        }
    }

    public void onDisable() {
        info("");
        info("M0-OnlinePlayersGUI has been successfully disabled!");
        info("");

        if (this.isEnabled()) {
            this.manager.disablePlugin(this);
        }
    }

    private void prepareConfig() {
        this.configFile = new File(this.getDataFolder(), "config.yml");

        if (!this.configFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            this.configFile.getParentFile().mkdirs();

            this.copy(this.getResource("config.yml"), this.configFile);
        }

        try {
            this.getConfig().options().copyDefaults(true);
            this.getConfig().save(this.configFile);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        YamlConfiguration.loadConfiguration(this.configFile);

        this.cfg.load();
        this.copy(this.getResource("config.yml_backup"), new File(this.getDataFolder(), "config.yml_backup"));
        this.copy(this.getResource("custom" + File.separator + "custom_gui.yml_example"), this.configFile);

    }

    private void info(String message) {
        getLogger().info(message);
    }

    private void copy(InputStream in, File file) {
        if (in != null) {
            try {
                OutputStream out = new FileOutputStream(file);

                byte[] buf = new byte[1024];

                int len;

                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                out.close();
                in.close();
            }
            catch (Exception ex) {
                Messenger.error("Error copying resource: " + ex.getMessage());

                ex.printStackTrace();
            }
        }
    }
}
