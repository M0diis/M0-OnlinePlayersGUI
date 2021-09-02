package me.M0dii.OnlinePlayersGUI.Utils;

import me.M0dii.OnlinePlayersGUI.CustomItem;
import me.M0dii.OnlinePlayersGUI.OnlineGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Config
{
    private String HEAD_NAME;
    private String CONFIG_RELOADED, NO_PERMISSION, NO_PERMISSION_COND;
    
    private String NEXT_PAGE_NAME, PREVIOUS_PAGE_NAME;
    private String GUI_TITLE;
    
    private String TOGGLE_MESSAGE;
    
    private List<String> HEAD_LORE, NEXT_PAGE_LORE, PREVIOUS_PAGE_LORE;
    private List<String> LEFT_CLICK_COMMANDS, RIGHT_CLICK_COMMANDS;
    
    private boolean CLOSE_ON_LEFT_CLICK, CLOSE_ON_RIGHT_CLICK;
    private boolean UPDATE_ON_JOIN, UPDATE_ON_LEAVE, ALWAYS_SHOW_BUTTONS;
    private int GUI_SIZE;
    
    private Material NEXT_PAGE_MATERIAL, PREVIOUS_PAGE_MATERIAL;
    
    private boolean ESSENTIALSX_HOOK;
    
    private boolean CONDITION_REQUIRED, PERMISSION_REQUIRED;
    private String CONDITION, PERMISSION;
    
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
    
    private static final String prefix = "M0-OnlinePlayersGUI.";
    
    private boolean getBool(String path)
    {
        return cfg.getBoolean(prefix + path);
    }
    
    private String getStringf(String path)
    {
        return format(cfg.getString(prefix + path));
    }
    
    private List<String> getStringList(String path)
    {
        return cfg.getStringList(prefix + path);
    }

    public void load()
    {
        UPDATE_ON_JOIN = getBool("GUI.UpdateOn.Join");
        UPDATE_ON_LEAVE = getBool("GUI.UpdateOn.Leave");
        
        CLOSE_ON_LEFT_CLICK = getBool("GUI.CloseOn.LeftClick");
        CLOSE_ON_RIGHT_CLICK = getBool("GUI.CloseOn.RightClick");
        
        ALWAYS_SHOW_BUTTONS = getBool("AlwaysShowPageButtons");
        
        HEAD_NAME = getStringf("PlayerDisplay.Name");
        
        HEAD_LORE = getStringList("PlayerDisplay.Lore");
    
        GUI_TITLE = getStringf("GUI.Title");
        
        NO_PERMISSION = getStringf("NoPermission");
        NO_PERMISSION_COND = getStringf("NoPermissionConditional");
        CONFIG_RELOADED = getStringf("ReloadMessage");
        
        TOGGLE_MESSAGE = getStringf("ToggleMessage");
        
        LEFT_CLICK_COMMANDS = getStringList("PlayerDisplay.Commands.Left-Click");
        RIGHT_CLICK_COMMANDS = getStringList("PlayerDisplay.Commands.Right-Click");
        
        GUI_SIZE = cfg.getInt("M0-OnlinePlayersGUI.GUI.Size");
    
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
        
        ESSENTIALSX_HOOK = getBool("EssentialsXHook");
        
        CONDITION_REQUIRED = getBool("Condition.Required");
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
                    String.format("M0-OnlinePlayersGUI.CustomItems.%d.Material", i), "BOOK");
    
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
                    meta.setDisplayName(CI_NAME);

                List<String> lore = new ArrayList<>();

                if(CI_LORE.size() != 0)
                {
                    for(String l : CI_LORE)
                        lore.add(format(l));
                    
                    meta.setLore(lore);
                }

                List<String> lcc = cfg.getStringList(
                        String.format(prefix + "CustomItems.%d.Commands.Left-Click", i));

                List<String> rcc = cfg.getStringList(
                        String.format(prefix + "CustomItems.%d.Commands.Right-Click", i));

                meta.getPersistentDataContainer().set(
                        new NamespacedKey(plugin, "Slot"), PersistentDataType.INTEGER, i);

                meta.getPersistentDataContainer().set(
                        new NamespacedKey(plugin, "IsCustom"), PersistentDataType.STRING, "true");

                item.setItemMeta(meta);

                boolean colc = cfg.getBoolean(
                        String.format(prefix + "CustomItems.%d.Commands.CloseOnLeftClick", i));

                boolean corc = cfg.getBoolean(
                        String.format(prefix + "CustomItems.%d.Commands.CloseOnRightClick", i));

                CustomItem ci = new CustomItem(item, i, lcc, rcc, colc, corc, lore);

                this.CUSTOM_ITEMS.add(ci);
            }
        }
    }
    
    private String format(@Nullable String text)
    {
        if(text == null || text.isEmpty())
            return text;
        
        return ChatColor.translateAlternateColorCodes('&', text);
    }
    
    public boolean CLOSE_ON_LEFT_CLICK()
    {
        return this.CLOSE_ON_LEFT_CLICK;
    }
    
    public boolean CLOSE_ON_RIGHT_CLICK()
    {
        return this.CLOSE_ON_RIGHT_CLICK;
    }
    
    public List<CustomItem> getCustomItems()
    {
        return this.CUSTOM_ITEMS;
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
}
