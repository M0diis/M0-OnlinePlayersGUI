package me.m0dii.onlineplayersgui.utils;

import me.m0dii.onlineplayersgui.CustomItem;
import me.m0dii.onlineplayersgui.OnlineGUI;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private int NEXT_PAGE_SLOT, PREVIOUS_PAGE_SLOT;
    
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
        HEAD_NAME = getStringf("player-display.name");
    
        HEAD_LORE = getStringList("player-display.lore");
    
        GUI_TITLE = getStringf("gui.title");
    
        LEFT_CLICK_COMMANDS = getStringList("player-display.commands.left-click");
        MIDDLE_CLICK_COMMANDS = getStringList("player-display.commands.middle-click");
        RIGHT_CLICK_COMMANDS = getStringList("player-display.commands.right-click");
    
        GUI_SIZE = cfg.getInt("gui.size");
    
        String mat1 = cfg.getString("next-button.material", "ENCHANTED_BOOK");
        String mat2 = cfg.getString("previous-button.material", "ENCHANTED_BOOK");
    
        PREVIOUS_PAGE_MATERIAL = Material.getMaterial(mat1);
        if(PREVIOUS_PAGE_MATERIAL == null) PREVIOUS_PAGE_MATERIAL = Material.BOOK;
    
        NEXT_PAGE_MATERIAL = Material.getMaterial(mat2);
        if(NEXT_PAGE_MATERIAL == null) NEXT_PAGE_MATERIAL = Material.BOOK;
    
        PREVIOUS_PAGE_LORE = getStringList("previous-button.lore");
        NEXT_PAGE_LORE = getStringList("next-button.lore");
    
        PREVIOUS_PAGE_NAME = getStringf("previous-button.name");
        NEXT_PAGE_NAME = getStringf("next-button.name");
        
        PREVIOUS_PAGE_SLOT = cfg.getInt("previous-button.slot");
        NEXT_PAGE_SLOT = cfg.getInt("next-button.slot");
        
        PERMISSION_REQUIRED = cfg.getBoolean("condition.permission.required");
        CONDITION = cfg.getString("condition.placeholder");
        PERMISSION = cfg.getString("condition.permission.node");
        
        setUpCustomItems(plugin);
    }

    private Map<Integer, CustomItem> CUSTOM_ITEMS;
    
    private void setUpCustomItems(OnlineGUI plugin)
    {
        CUSTOM_ITEMS = new HashMap<>();
        
        ConfigurationSection sec = cfg.getConfigurationSection("custom-items");
        
        if(sec == null)
        {
            return;
        }
        
        sec.getKeys(false).forEach(key ->
        {
            ConfigurationSection itemSec = sec.getConfigurationSection(key);
            
            if(itemSec == null)
            {
                return;
            }
            
            String itemName = itemSec.getString("material", "BOOK");
            
            Material customItem = Material.getMaterial(itemName);
            
            if(customItem != null && !customItem.equals(Material.AIR))
            {
                ItemStack item = new ItemStack(customItem);
                
                String customItemName = Utils.format(itemSec.getString("name"));
                
                List<String> customItemLore = format(itemSec.getStringList("lore"));
                
                ItemMeta meta = item.getItemMeta();
                
                meta.setDisplayName(customItemName);
                meta.setLore(customItemLore);
    
                List<String> lcc = itemSec.getStringList("commands.left-click");
                List<String> mcc = itemSec.getStringList("commands.middle-click");
                List<String> rcc = itemSec.getStringList("commands.right-click");
    
                meta.getPersistentDataContainer().set(
                        new NamespacedKey(plugin, "IsCustom"), PersistentDataType.STRING, "true");
    
                if(itemSec.contains("slots"))
                {
                    if(itemSec.contains("slots.start"))
                    {
                        int start = itemSec.getInt("slots.start");
                        int end = itemSec.getInt("slots.end");
            
                        for(int i = start; i <= end; i++)
                        {
                            addCustomItem(meta, plugin, i, item, CUSTOM_ITEMS, lcc, mcc, rcc, customItemLore);
                        }
                    }
                    else
                    {
                        Object slots = itemSec.get("slots");
            
                        if(slots instanceof List)
                        {
                            List<Integer> slotList = (List<Integer>)slots;
                
                            for(Integer slot : slotList)
                            {
                                addCustomItem(meta, plugin, slot, item, CUSTOM_ITEMS, lcc, mcc, rcc, customItemLore);
                            }
                        }
                        else
                        {
                            int slot = itemSec.getInt("slot", -1);
                
                            addCustomItem(meta, plugin, slot, item, CUSTOM_ITEMS, lcc, mcc, rcc, customItemLore);
                        }
                    }
                }
                else
                {
                    int slot = itemSec.getInt("slot", -1);
        
                    addCustomItem(meta, plugin, slot, item, CUSTOM_ITEMS, lcc, mcc, rcc, customItemLore);
                }
            }
        });
    }
    
    private void addCustomItem(ItemMeta meta, OnlineGUI plugin, int slot, ItemStack item, Map<Integer, CustomItem> CUSTOM_ITEMS
            , List<String> lcc, List<String> mcc, List<String> rcc, List<String> customItemLore)
    {
        meta.getPersistentDataContainer().set(
                new NamespacedKey(plugin, "Slot"), PersistentDataType.INTEGER, slot);
        
        item.setItemMeta(meta);
        
        CUSTOM_ITEMS.put(slot, new CustomItem(item, slot, lcc, mcc, rcc, customItemLore));
    }

    private List<String> format(List<String> list)
    {
        return list.stream().map(Utils::format).collect(Collectors.toList());
    }
    
    public int PREV_PAGE_SLOT()
    {
        return PREVIOUS_PAGE_SLOT;
    }
    
    public int NEXT_PAGE_SLOT()
    {
        return NEXT_PAGE_SLOT;
    }
    
    public Map<Integer, CustomItem> getCustomItems()
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