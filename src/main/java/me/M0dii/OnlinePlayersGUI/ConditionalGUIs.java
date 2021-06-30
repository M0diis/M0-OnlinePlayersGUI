package me.M0dii.OnlinePlayersGUI;

import me.M0dii.OnlinePlayersGUI.InventoryHolder.ConditionalGUIInventory;
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
        this.conditionalNames = new ArrayList<>();
        
        this.plugin = plugin;
        
        this.loadGUIs();
    }
    
    public boolean isConditional(String name)
    {
        return conditionalNames.contains(name);
    }
    
    public void loadGUIs()
    {
        File folder = plugin.getDataFolder();
        
        if(folder != null)
        {
            for (File file : folder.listFiles())
            {
                String name = file.getName();
                
                if (file.isFile() && name.endsWith(".yml") && !name.startsWith("config"))
                    conditionalNames.add(file.getName().replace(".yml", ""));
            }
        }
    }
    
    public void displayConditional(String name, Player p)
    {
        File file = new File(plugin.getDataFolder() + File.separator + name + ".yml");
        
        YamlConfiguration cfg =
                YamlConfiguration.loadConfiguration(file);
    
        ConditionalGUIInventory cgi = new ConditionalGUIInventory(plugin, name, 0, cfg);
        cgi.setCustomItems(p);
        
        p.openInventory(cgi.getInventory());
    }
}
