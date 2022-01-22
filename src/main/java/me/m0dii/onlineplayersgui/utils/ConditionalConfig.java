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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConditionalConfig
{
    private String headName;
    
    private String nextPageName, prevPageName;
    private String guiTitle;
    
    private List<String> headLore, nextPageLore, prevPageLore;
    private List<String> leftClickCommands, middleClickCommands, rightClickCommands;
    
    private int GUI_SIZE;
    
    private Material nextPageMat, prevPageMat;
    private int nextPageSlot, prevPageSlot;
    
    private boolean permissionRequired;
    private String condition, permission;
    
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
        headName = getStringf("player-display.name");
        headLore = getStringList("player-display.lore");
    
        guiTitle = getStringf("gui.title");
    
        leftClickCommands = getStringList("player-display.commands.left-click");
        middleClickCommands = getStringList("player-display.commands.middle-click");
        rightClickCommands = getStringList("player-display.commands.right-click");
    
        GUI_SIZE = cfg.getInt("gui.size");
    
        String mat1 = cfg.getString("next-button.material", "ENCHANTED_BOOK");
        String mat2 = cfg.getString("previous-button.material", "ENCHANTED_BOOK");
    
        prevPageMat = Material.getMaterial(mat1);
        if(prevPageMat == null) prevPageMat = Material.BOOK;
    
        nextPageMat = Material.getMaterial(mat2);
        if(nextPageMat == null) nextPageMat = Material.BOOK;
    
        prevPageLore = getStringList("previous-button.lore");
        nextPageLore = getStringList("next-button.lore");
    
        prevPageName = getStringf("previous-button.name");
        nextPageName = getStringf("next-button.name");
        
        prevPageSlot = cfg.getInt("previous-button.slot");
        nextPageSlot = cfg.getInt("next-button.slot");
        
        permissionRequired = cfg.getBoolean("condition.permission.required");
        condition = cfg.getString("condition.placeholder");
        permission = cfg.getString("condition.permission.node");
        
        setUpCustomItems(plugin);
    }

    private Map<Integer, CustomItem> customItems;
    
    private void setUpCustomItems(OnlineGUI plugin)
    {
        customItems = new HashMap<>();
        
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
                            addCustomItem(meta, i, item, lcc, mcc, rcc, customItemLore);
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
                                addCustomItem(meta, slot, item, lcc, mcc, rcc, customItemLore);
                            }
                        }
                        else
                        {
                            int slot = itemSec.getInt("slot", -1);
                
                            addCustomItem(meta, slot, item, lcc, mcc, rcc, customItemLore);
                        }
                    }
                }
                else
                {
                    int slot = itemSec.getInt("slot", -1);
        
                    addCustomItem(meta, slot, item, lcc, mcc, rcc, customItemLore);
                }
            }
        });
    }
    
    private void addCustomItem(ItemMeta meta, int slot, ItemStack item, List<String> lcc, List<String> mcc, List<String> rcc, List<String> customItemLore)
    {
        meta.getPersistentDataContainer().set(
                new NamespacedKey(this.plugin, "Slot"), PersistentDataType.INTEGER, slot);
        
        item.setItemMeta(meta);
        
        customItems.put(slot, new CustomItem(item, slot, lcc, mcc, rcc, customItemLore));
    }

    private List<String> format(List<String> list)
    {
        return list.stream().map(Utils::format).collect(Collectors.toList());
    }
    
    public int getPrevPageSlot()
    {
        return prevPageSlot;
    }
    
    public int getNextPageSlot()
    {
        return nextPageSlot;
    }
    
    public Map<Integer, CustomItem> getCustomItems()
    {
        return this.customItems;
    }
    
    public int getGuiSize()
    {
        return GUI_SIZE;
    }
    
    public List<String> getNextPageLore()
    {
        return nextPageLore;
    }
    
    public List<String> getPrevPageLore()
    {
        return prevPageLore;
    }
    
    public List<String> getRightClickCmds()
    {
        return rightClickCommands;
    }
    
    public List<String> getMiddleClickCmds()
    {
        return middleClickCommands;
    }
    
    public List<String> getLeftClickCmds()
    {
        return leftClickCommands;
    }
    
    public List<String> getHeadLore()
    {
        return headLore;
    }
    
    public String getNextPageName()
    {
        return nextPageName;
    }
    
    public String getPrevPageName()
    {
        return prevPageName;
    }
    
    public String getHeadDisplay()
    {
        return headName;
    }
    
    public Material getNextPageMat()
    {
        return nextPageMat;
    }
    
    public Material getPrevPageMat()
    {
        return prevPageMat;
    }
    
    public String getCondition()
    {
        return condition;
    }
    
    public boolean isPermissionRequired()
    {
        return permissionRequired;
    }
    
    public String getREQUIRED_PERMISSION()
    {
        return permission;
    }
    
    public String getGuiTitle()
    {
        return guiTitle;
    }

}