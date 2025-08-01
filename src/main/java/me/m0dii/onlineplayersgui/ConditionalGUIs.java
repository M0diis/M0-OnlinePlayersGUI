package me.m0dii.onlineplayersgui;

import me.m0dii.onlineplayersgui.inventoryholder.ConditionalGUIInventory;
import me.m0dii.onlineplayersgui.utils.ConditionalConfig;
import me.m0dii.onlineplayersgui.utils.TextUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConditionalGUIs {
    private final OnlineGUI plugin;
    private final ArrayList<String> conditionalNames;

    public List<String> getConditionalNames() {
        return this.conditionalNames;
    }

    public ConditionalGUIs(OnlineGUI plugin) {
        this.plugin = plugin;

        this.conditionalNames = new ArrayList<>();

        this.loadGUIs();
    }

    public boolean isConditional(String name) {
        return conditionalNames.contains(name);
    }

    public void loadGUIs() {
        File folder = new File(plugin.getDataFolder() + File.separator + "custom");

        conditionalNames.clear();

        if (!folder.exists()) {
            folder.mkdirs();
        }

        File[] files = folder.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {
            String name = file.getName();

            if (file.isFile() && name.endsWith(".yml") && !name.startsWith("config")) {
                conditionalNames.add(name.replace(".yml", ""));
            }
        }
    }

    public void displayConditional(String name, Player p) {
        File file = new File(plugin.getDataFolder() + File.separator + "custom" + File.separator + name + ".yml");

        if (!file.exists()) {
            return;
        }

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        ConditionalGUIInventory cgi = new ConditionalGUIInventory(plugin,
                TextUtils.kyorify(cfg.getString("GUI.Title")),
                0,
                new ConditionalConfig(cfg)
        );

        cgi.setCustomItems(p);

        p.openInventory(cgi.getInventory());
    }
}
