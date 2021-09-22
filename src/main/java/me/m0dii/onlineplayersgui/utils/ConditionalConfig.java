package me.m0dii.onlineplayersgui.utils;

import me.m0dii.onlineplayersgui.CustomItem;
import me.m0dii.onlineplayersgui.OnlineGUI;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConditionalConfig
{
    private String HEAD_NAME;
    
    private String NEXT_PAGE_NAME, PREVIOUS_PAGE_NAME;
    private String GUI_TITLE;
    
    private List<String> HEAD_LORE, NEXT_PAGE_LORE, PREVIOUS_PAGE_LORE;
    private List<String> LEFT_CLICK_COMMANDS, MIDDLE_CLICK_COMMANDS, RIGHT_CLICK_COMMANDS;
    
    private int GUI_SIZE;
    
    private Material NEXT_PAGE_MATERIAL, PREVIOUS_PAGE_MATERIAL;
    
    private boolean PERMISSION_REQUIRED;
    private String CONDITION, PERMISSION;
    
    private final OnlineGUI plugin;
    
    public ConditionalConfig(OnlineGUI plugin, FileConfiguration cfg)
    {
        this.plugin = plugin;
        this.cfg = cfg;
        
        this.load();
    }
    
    final FileConfiguration cfg;
    
    private boolean getBool(String path)
    {
        return cfg.getBoolean(path, false);
    }
    
    private String getStringf(String path)
    {
        return Utils.format(cfg.getString(path));
    }
    
    private List<String> getStringList(String path)
    {
        return cfg.getStringList(path);
    }
    
    public void load()
    {
        HEAD_NAME = getStringf("PlayerDisplay.Name");
        
        HEAD_LORE = getStringList("PlayerDisplay.Lore");
        
        GUI_TITLE = getStringf("GUI.Title");
        
        LEFT_CLICK_COMMANDS = getStringList("PlayerDisplay.Commands.Left-Click");
        MIDDLE_CLICK_COMMANDS = getStringList("PlayerDisplay.Commands.Middle-Click");
        RIGHT_CLICK_COMMANDS = getStringList("PlayerDisplay.Commands.Right-Click");
        
        GUI_SIZE = cfg.getInt("GUI.Size");
        
        String mat1 = cfg.getString("NextButton.Material", "ENCHANTED_BOOK");
        String mat2 = cfg.getString("PreviousButton.Material", "ENCHANTED_BOOK");
        
        PREVIOUS_PAGE_MATERIAL = Material.getMaterial(mat1);
        if(PREVIOUS_PAGE_MATERIAL == null) PREVIOUS_PAGE_MATERIAL = Material.BOOK;
        
        NEXT_PAGE_MATERIAL = Material.getMaterial(mat2);
        if(NEXT_PAGE_MATERIAL == null) NEXT_PAGE_MATERIAL = Material.BOOK;
        
        PREVIOUS_PAGE_LORE = getStringList("PreviousButton.Lore");
        NEXT_PAGE_LORE = getStringList("NextButton.Lore");
        
        PREVIOUS_PAGE_NAME = getStringf("PreviousButton.Name");
        NEXT_PAGE_NAME = getStringf("NextButton.Name");
        
        CONDITION = cfg.getString("Condition.Placeholder");
        
        PERMISSION_REQUIRED = getBool("Condition.Permission.Required");
        CONDITION = cfg.getString("M0-OnlinePlayersGUI.Condition.Placeholder");
        PERMISSION = cfg.getString("M0-OnlinePlayersGUI.Condition.Permission.Node");
        
        setUpCustomItems(plugin);
    }
    
    private List<CustomItem> CUSTOM_ITEMS;
    
    private void setUpCustomItems(OnlineGUI plugin)
    {
        CUSTOM_ITEMS = new ArrayList<>();
        
        int[] slots = new int[]{1, 2, 3, 5, 7, 8, 9};
        
        for(int i : slots)
        {
            String itemName = cfg.getString(
                    String.format("CustomItems.%d.Material", i), "BOOK");
            
            Material CI_ITEM = Material.getMaterial(itemName);
            
            if(CI_ITEM != null && !CI_ITEM.equals(Material.AIR))
            {
                ItemStack item = new ItemStack(CI_ITEM);
                
                String CI_NAME = getStringf(
                        String.format("CustomItems.%d.Name", i));
                
                List<String> CI_LORE = getStringList(
                        String.format("CustomItems.%d.Lore", i));
                
                ItemMeta meta = item.getItemMeta();
                
                if(CI_NAME.length() != 0)
                    meta.setDisplayName(Utils.format(CI_NAME));
                
                List<String> lore = new ArrayList<>();
    
                if(CI_LORE.size() != 0)
                {
                    lore = CI_LORE.stream()
                            .map(Utils::format)
                            .collect(Collectors.toList());
        
                    meta.setLore(lore);
                }
                
                List<String> lcc = cfg.getStringList(
                        String.format("CustomItems.%d.Commands.Left-Click", i));
                
                List<String> mcc = cfg.getStringList(
                        String.format("CustomItems.%d.Commands.Middle-Click", i));
                
                List<String> rcc = cfg.getStringList(
                        String.format("CustomItems.%d.Commands.Right-Click", i));
                
                meta.getPersistentDataContainer().set(
                        new NamespacedKey(plugin, "Slot"), PersistentDataType.INTEGER, i);
                
                meta.getPersistentDataContainer().set(
                        new NamespacedKey(plugin, "IsCustom"), PersistentDataType.STRING, "true");
                
                item.setItemMeta(meta);
                
                CUSTOM_ITEMS.add(new CustomItem(item, i, lcc, mcc, rcc, lore));
            }
        }
    }
    
    public List<CustomItem> getCustomItems()
    {
        return this.CUSTOM_ITEMS;
    }
    
    public int GUI_SIZE()
    {
        return GUI_SIZE;
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
    
    public String NEXT_PAGE_BUTTON_NAME()
    {
        return NEXT_PAGE_NAME;
    }
    
    public String PREV_PAGE_BUTTON_NAME()
    {
        return PREVIOUS_PAGE_NAME;
    }
    
    public String HEAD_DISPLAY_NAME()
    {
        return HEAD_NAME;
    }
    
    public Material NEXT_PAGE_MATERIAL()
    {
        return NEXT_PAGE_MATERIAL;
    }
    
    public Material PREV_PAGE_MATERIAL()
    {
        return PREVIOUS_PAGE_MATERIAL;
    }
    
    public String getCondition()
    {
        return CONDITION;
    }
    public boolean isPERMISSION_REQUIRED()
    {
        return PERMISSION_REQUIRED;
    }
    public String getREQUIRED_PERMISSION()
    {
        return PERMISSION;
    }
    public String getGUI_TITLE()
    {
        return GUI_TITLE;
    }
    public List<String> MIDDLE_CLICK_CMDS()
    {
        return MIDDLE_CLICK_COMMANDS;
    }
}