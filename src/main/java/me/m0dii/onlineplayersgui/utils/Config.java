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

public class Config
{
    private String playerHeadName;
    private String configReloadedMsg, noPermissionMsg, noPermissionCondMsg;
    
    private String nextPageName, prevPageName;
    private String guiTitle;
    
    private String visibilityToggleMsg;
    
    private List<String> playerHeadLore, nextPageLore, prevPageLore;
    private List<String> leftClickCmds, middleClickCmds, rightClickCmds;
    
    private boolean updateOnJoin, updateOnLeave, buttonsAlwaysVisible;
    private int guiSize;
    
    private Material nextPageMat, prevPageMat;
    private int nextPageSlot, prevPageSlot;
    
    private boolean essxHook;
    
    private boolean conditionRequired, permissionRequired;
    private String condition, permission;
    
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
        cfg = plugin.getConfig();
        
        load();
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
        updateOnJoin = getBool("gui.update-on.join");
        updateOnLeave = getBool("gui.update-on.leave");
        
        buttonsAlwaysVisible = getBool("buttons-always-visible");
        
        DEBUG_ENABLED = cfg.getBoolean("debug", false);
        
        playerHeadName = getStringf("player-display.name");
        playerHeadLore = getStringList("player-display.lore");
    
        guiTitle = getStringf("gui.title");
        
        noPermissionMsg = getStringf("messages.no-permission");
        noPermissionCondMsg = getStringf("messages.no-permission-conditional");
        configReloadedMsg = getStringf("messages.reload");
        
        visibilityToggleMsg = getStringf("messages.toggle-visibility");
        
        leftClickCmds = getStringList("player-display.commands.left-click");
        middleClickCmds = getStringList("player-display.commands.middle-click");
        rightClickCmds = getStringList("player-display.commands.right-click");
        
        guiSize = cfg.getInt("gui.size");
    
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
        
        prevPageSlot = cfg.getInt("previous-button.slot", 0);
        nextPageSlot = cfg.getInt("next-button.slot", 8);
        
        essxHook = getBool("essentialsx-hook");
        
        conditionRequired = getBool("condition.required");
        permissionRequired = getBool("condition.permission.required");
        condition = cfg.getString("condition.placeholder");
        permission = cfg.getString("condition.permission.node");
        
        setUpCustomItems(plugin);
    }
    
    private Map<Integer, CustomItem> customItems;

    private void setUpCustomItems(OnlineGUI plugin)
    {
        customItems = new HashMap<>();
        
        plugin.reloadConfig();
    
        ConfigurationSection sec = plugin.getConfig().getConfigurationSection("custom-items");
        
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

            String itemName = itemSec.getString("material", "WHITE_STAINED_GLASS_PANE");
    
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
        
        this.customItems.put(slot, new CustomItem(item, slot, lcc, mcc, rcc, customItemLore));
    }
    
    private List<String> format(List<String> list)
    {
        return list.stream().map(Utils::format).collect(Collectors.toList());
    }
    
    public Map<Integer, CustomItem> getCustomItems()
    {
        return this.customItems;
    }
    
    public List<Integer> getCustomItemSlots()
    {
        return this.customItems.keySet().stream().sorted().collect(Collectors.toList());
    }
    
    public int getGuiSize()
    {
        return guiSize;
    }
    
    public boolean doUpdateOnJoin()
    {
        return updateOnJoin;
    }
    
    public boolean doUpdateOnLeave()
    {
        return updateOnLeave;
    }
    
    public boolean areButtonsAlwaysOn()
    {
        return buttonsAlwaysVisible;
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
        return rightClickCmds;
    }
    
    public List<String> getLeftClickCmds()
    {
        return leftClickCmds;
    }
    
    public List<String> getMiddleClickCmds()
    {
        return this.middleClickCmds;
    }
    
    public List<String> getHeadLore()
    {
        return playerHeadLore;
    }
    
    public String getGuiTitle()
    {
        return guiTitle;
    }
    
    public int getNextPageSlot()
    {
        return nextPageSlot;
    }
    
    public int getPrevPageSlot()
    {
        return prevPageSlot;
    }
    
    public String getNextPageName()
    {
        return nextPageName;
    }
    
    public String getPrevPageName()
    {
        return prevPageName;
    }
    
    public String getNoPermMsg()
    {
        return noPermissionMsg;
    }
    
    public String getCfgReloadMsg()
    {
        return configReloadedMsg;
    }
    
    public String getHeadDisplay()
    {
        return playerHeadName;
    }
    
    public boolean ESSX_HOOK()
    {
        if(this.plugin.getEssentials() == null)
            return false;
            
        return essxHook;
    }
    
    public Material getNextPageMat()
    {
        return this.nextPageMat;
    }
    
    public Material getPrevPageMat()
    {
        return this.prevPageMat;
    }
    
    public boolean isConditionRequired()
    {
        return this.conditionRequired;
    }
    
    public String getCondition()
    {
        return this.condition;
    }
    
    public String getToggleMsg()
    {
        return visibilityToggleMsg;
    }
    
    public String getNoPermissionCondMsg()
    {
        return noPermissionCondMsg;
    }
    
    public String getRequiredPerm()
    {
        return this.permission;
    }
    
    public boolean isPermissionRequired()
    {
        return this.permissionRequired;
    }

    public boolean DEBUG_ENABLED()
    {
        return this.DEBUG_ENABLED;
    }
}
