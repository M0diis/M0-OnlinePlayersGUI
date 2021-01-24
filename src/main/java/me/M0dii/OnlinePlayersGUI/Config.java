package me.M0dii.OnlinePlayersGUI;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class Config
{
    private String HEAD_NAME;
    private String CONFIG_RELOADED;
    private String NO_PERMISSION;
    
    private String PREVIOUS_PAGE_NAME;
    private String NEXT_PAGE_NAME;
    private String GUI_TITLE;
    
    private List<String> HEAD_LORE;
    private List<String> LEFT_CLICK_COMMANDS;
    private List<String> RIGHT_CLICK_COMMANDS;
    
    private List<String> PREVIOUS_PAGE_LORE;
    private List<String> NEXT_PAGE_LORE;
    
    private boolean UPDATE_ON_JOIN;
    private boolean UPDATE_ON_LEAVE;
    private boolean HIDE_BUTTONS_ON_SINGLE;
    private int GUI_SIZE;
    
    private Material PREVIOUS_PAGE_MATERIAL;
    private Material NEXT_PAGE_MATERIAL;
    
    private boolean ESSENTIALSX_HOOK;
    
    public Config() { }

    public void load(OnlineGUI plugin)
    {
        FileConfiguration cfg = plugin.getFileConfig();
    
        UPDATE_ON_JOIN = cfg.getBoolean("M0-OnlinePlayersGUI.GUI.UpdateOn.Join");
        UPDATE_ON_LEAVE = cfg.getBoolean("M0-OnlinePlayersGUI.GUI.UpdateOn.Leave");
        
        HIDE_BUTTONS_ON_SINGLE = cfg.getBoolean("M0-OnlinePlayersGUI.HideButtonsOnSinglePage");
        
        HEAD_NAME = format(cfg.getString("M0-OnlinePlayersGUI.PlayerDisplay.Name"));
        
        HEAD_LORE = cfg.getStringList("M0-OnlinePlayersGUI.PlayerDisplay.Lore");
    
        GUI_TITLE = format(cfg.getString("M0-OnlinePlayersGUI.GUI.Title"));
        
        NO_PERMISSION = format(cfg.getString("M0-OnlinePlayersGUI.NoPermission"));
        CONFIG_RELOADED = format(cfg.getString("M0-OnlinePlayersGUI.ReloadMessage"));
        
        LEFT_CLICK_COMMANDS = cfg.getStringList("M0-OnlinePlayersGUI.PlayerDisplay.Commands.Left-Click");
        RIGHT_CLICK_COMMANDS = cfg.getStringList("M0-OnlinePlayersGUI.PlayerDisplay.Commands.Right-Click");
        
        GUI_SIZE = cfg.getInt("M0-OnlinePlayersGUI.GUI.Size");
    
        PREVIOUS_PAGE_MATERIAL = Material.getMaterial(cfg.getString("M0-OnlinePlayersGUI.PreviousButton.Material"));
        if(PREVIOUS_PAGE_MATERIAL == null) PREVIOUS_PAGE_MATERIAL = Material.BOOK;
        
        NEXT_PAGE_MATERIAL = Material.getMaterial(cfg.getString("M0-OnlinePlayersGUI.NextButton.Material"));
        if(NEXT_PAGE_MATERIAL == null) NEXT_PAGE_MATERIAL = Material.BOOK;
        
        PREVIOUS_PAGE_LORE = cfg.getStringList("M0-OnlinePlayersGUI.PreviousButton.Lore");
        NEXT_PAGE_LORE = cfg.getStringList("M0-OnlinePlayersGUI.NextButton.Lore");
        
        PREVIOUS_PAGE_NAME = format(cfg.getString("M0-OnlinePlayersGUI.PreviousButton.Name"));
        NEXT_PAGE_NAME = format(cfg.getString("M0-OnlinePlayersGUI.NextButton.Name"));
        
        ESSENTIALSX_HOOK = cfg.getBoolean("M0-OnlinePlayersGUI.EssentialsXHook");
        
        setUpCustomItems(cfg, plugin);
    }
    
    private List<ItemStack> CUSTOM_ITEMS;
    
    private void setUpCustomItems(FileConfiguration cfg, OnlineGUI plugin)
    {
        CUSTOM_ITEMS = new ArrayList<>();
        
        int[] slots = new int[]{1, 2, 3, 5, 7, 8, 9};
        
        for(int i : slots)
        {
            Material CI_ITEM = Material.getMaterial(
                    cfg.getString(String.format("M0-OnlinePlayersGUI.CustomItems.%d.Material", i)));
            
            if(CI_ITEM != null && !CI_ITEM.equals(Material.AIR))
            {
                ItemStack item = new ItemStack(CI_ITEM);
                
                String CI_NAME = format(cfg.getString(String.format("M0-OnlinePlayersGUI.CustomItems.%d.Name", i)));
    
                List<String> CI_LORE = cfg.getStringList(String.format("M0-OnlinePlayersGUI.CustomItems.%d.Lore", i));
                
                //List<String> CI_LC_CMDS = cfg.getStringList(String.format("M0-OnlinePlayersGUI.CustomItems.%d.Commands" +
                //    ".Left-Click", i));
                //List<String> CI_RC_CMDS = cfg.getStringList(String.format("M0-OnlinePlayersGUI.CustomItems.%d.Commands" +
                //    ".Right-Click", i));
                
                ItemMeta meta = item.getItemMeta();
                
                if(CI_NAME.length() != 0)
                    meta.setDisplayName(CI_NAME);
                
                List<String> lore = new ArrayList<>();
                
                if(CI_LORE.size() != 0)
                {
                    for(String l : CI_LORE)
                    {
                        lore.add(format(l));
                    }
                    
                    meta.setLore(lore);
                }
                
                meta.getPersistentDataContainer().set(
                        new NamespacedKey(plugin, "Slot"),
                        PersistentDataType.INTEGER, i);
                
                item.setItemMeta(meta);
                
                CUSTOM_ITEMS.add(item);
            }
        }
    }
    
    public List<ItemStack> getCustomItems()
    {
        return this.CUSTOM_ITEMS;
    }
    
    private static String format(String text)
    {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
    
    public int GUI_SIZE()
    {
        return GUI_SIZE;
    }
    
    public boolean UPDATE_ON_JOIN()
    {
        return UPDATE_ON_JOIN;
    }
    
    public boolean UPDATE_ON_LEAVE()
    {
        return UPDATE_ON_LEAVE;
    }
    
    public boolean HIDE_BUTTONS_SINGLE_PAGE()
    {
        return HIDE_BUTTONS_ON_SINGLE;
    }
    
    public List<String> NEXT_PAGE_LORE()
    {
        return NEXT_PAGE_LORE;
    }
    
    public List<String> PREV_PAGE_LORE()
    {
        return PREVIOUS_PAGE_LORE;
    }
    
    public List<String> RIGHT_CLICK_CMDS()
    {
        return RIGHT_CLICK_COMMANDS;
    }
    
    public List<String> LEFT_CLICK_CMDS()
    {
        return LEFT_CLICK_COMMANDS;
    }
    
    public List<String> HEAD_LORE()
    {
        return HEAD_LORE;
    }
    
    public String GUI_TITLE()
    {
        return GUI_TITLE;
    }
    
    public String NEXT_PAGE_BUTTON_NAME()
    {
        return NEXT_PAGE_NAME;
    }
    
    public String PREV_PAGE_BUTTON_NAME()
    {
        return PREVIOUS_PAGE_NAME;
    }
    
    public String NO_PERMISSION_MSG()
    {
        return NO_PERMISSION;
    }
    
    public String CONFIG_RELOAD_MSG()
    {
        return CONFIG_RELOADED;
    }
    
    public String HEAD_DISPLAY_NAME()
    {
        return HEAD_NAME;
    }
    
    public boolean ESSX_HOOK()
    {
        return ESSENTIALSX_HOOK;
    }
    
    public Material NEXT_PAGE_MATERIAL() {
        return this.NEXT_PAGE_MATERIAL;
    }
    
    public Material PREV_PAGE_MATERIAL() {
        return this.PREVIOUS_PAGE_MATERIAL;
    }
}
