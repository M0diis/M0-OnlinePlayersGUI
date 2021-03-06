package me.M0dii.OnlinePlayersGUI;

import me.M0dii.OnlinePlayersGUI.InventoryHolder.ConditionalGUIInventory;
import me.M0dii.OnlinePlayersGUI.Utils.Utils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;

public class ConditionalGUIs
{
    private final OnlineGUI plugin;
    private final ArrayList<String> conditionalNames;
    
    public ArrayList<String> getConditionalNames()
    {
        return this.conditionalNames;
    }
    
    public ConditionalGUIs(OnlineGUI plugin)
    {
        this.plugin = plugin;
    
        this.conditionalNames = new ArrayList<>();
    
        this.loadGUIs();
    }
    
    public boolean isConditional(String name)
    {
        return conditionalNames.contains(name);
    }
    
    public void loadGUIs()
    {
        File folder = new File(plugin.getDataFolder() + File.separator + "Custom");
    
        if(!folder.exists())
            folder.mkdirs();
        
        File[] files = folder.listFiles();
    
        if(files != null)
        {
            for (File file : files)
            {
                String name = file.getName();
    
                if (file.isFile() && name.endsWith(".yml") && !name.startsWith("config")
                && !conditionalNames.contains(file.getName().replace(".yml", "")))
                    conditionalNames.add(file.getName().replace(".yml", ""));
            }
        }
    
    }
    
    public void displayConditional(String name, Player p)
    {
        File file = new File(plugin.getDataFolder() + File.separator
                + "Custom" + File.separator + name + ".yml");
        
        YamlConfiguration cfg =
                YamlConfiguration.loadConfiguration(file);
        
        ConditionalGUIInventory cgi = new ConditionalGUIInventory(plugin, Utils.format(cfg.getString("GUI.Title")), 0, cfg);
        cgi.setCustomItems(p);
        
        p.openInventory(cgi.getInventory());
    }
}
