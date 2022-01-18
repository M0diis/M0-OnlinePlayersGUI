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

public class Config
{
    private String HEAD_NAME;
    private String CONFIG_RELOADED, NO_PERMISSION, NO_PERMISSION_COND;
    
    private String NEXT_PAGE_NAME, PREVIOUS_PAGE_NAME;
    private String GUI_TITLE;
    
    private String TOGGLE_MESSAGE;
    
    private List<String> HEAD_LORE, NEXT_PAGE_LORE, PREVIOUS_PAGE_LORE;
    private List<String> LEFT_CLICK_COMMANDS, MIDDLE_CLICK_COMMANDS, RIGHT_CLICK_COMMANDS;
    
    private boolean UPDATE_ON_JOIN, UPDATE_ON_LEAVE, ALWAYS_SHOW_BUTTONS;
    private int GUI_SIZE;
    
    private Material NEXT_PAGE_MATERIAL, PREVIOUS_PAGE_MATERIAL;
    
    private boolean ESSENTIALSX_HOOK;
    
    private boolean CONDITION_REQUIRED, PERMISSION_REQUIRED;
    private String CONDITION, PERMISSION;
    
    private boolean DEBUG_ENABLED;
    
    private final OnlineGUI plugin;
    
    public Config(OnlineGUI plugin)
    {
        this.plugin = plugin;
        this.cfg = plugin.getConfig();
    }
    
    public void reload()
    {
        plugin.reloadConfig();
        this.cfg = plugin.getConfig();
        
        this.load();
    }
    
    FileConfiguration cfg;
    

    private boolean getBool(String path)
    {
        return cfg.getBoolean(path);
    }
    
    private String getStringf(String path)
    {
        return Utils.format(cfg.getString(path));
    }
    
    private List<String> getStringList(String path)
    {
        return cfg.getStringList(path).stream().map(Utils::format).collect(Collectors.toList());
    }

    public void load()
    {
        UPDATE_ON_JOIN = getBool("gui.update-on.join");
        UPDATE_ON_LEAVE = getBool("gui.update-on.leave");
        
        ALWAYS_SHOW_BUTTONS = getBool("buttons-always-visible");
        
        DEBUG_ENABLED = cfg.getBoolean("debug", false);
        
        HEAD_NAME = getStringf("player-display.name");
        
        HEAD_LORE = getStringList("player-display.lore");
    
        GUI_TITLE = getStringf("gui.title");
        
        NO_PERMISSION = getStringf("messages.no-permission");
        NO_PERMISSION_COND = getStringf("messages.no-permission-conditional");
        CONFIG_RELOADED = getStringf("messages.reload");
        
        TOGGLE_MESSAGE = getStringf("messages.toggle-visibility");
        
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
        
        ESSENTIALSX_HOOK = getBool("essentialsx-hook");
        
        CONDITION_REQUIRED = getBool("condition.required");
        PERMISSION_REQUIRED = getBool("condition.permission.required");
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
    
    public Map<Integer, CustomItem> getCustomItems()
    {
        return this.CUSTOM_ITEMS;
    }
    
    public List<Integer> getCustomItemSlots()
    {
        return this.CUSTOM_ITEMS.keySet().stream().sorted().collect(Collectors.toList());
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
    
    public boolean ALWAYS_SHOW_BUTTONS()
    {
        return ALWAYS_SHOW_BUTTONS;
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
        if(this.plugin.getEssentials() == null)
            return false;
            
        return ESSENTIALSX_HOOK;
    }
    
    public Material NEXT_PAGE_MATERIAL()
    {
        return this.NEXT_PAGE_MATERIAL;
    }
    
    public Material PREV_PAGE_MATERIAL()
    {
        return this.PREVIOUS_PAGE_MATERIAL;
    }
    
    public boolean isCONDITION_REQUIRED()
    {
        return this.CONDITION_REQUIRED;
    }
    
    public String getCONDITION()
    {
        return this.CONDITION;
    }
    
    public String TOGGLE_MESSAGE()
    {
        return TOGGLE_MESSAGE;
    }
    
    public String NO_PERMISSION_COND_MSG()
    {
        return NO_PERMISSION_COND;
    }
    
    public String getREQUIRED_PERMISSION()
    {
        return this.PERMISSION;
    }
    public boolean isPERMISSION_REQUIRED()
    {
        return this.PERMISSION_REQUIRED;
    }
    
    public List<String> MIDDLE_CLICK_CMDS()
    {
        return this.MIDDLE_CLICK_COMMANDS;
    }
    
    public boolean DEBUG_ENABLED()
    {
        return this.DEBUG_ENABLED;
    }
    
}
