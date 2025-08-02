package me.m0dii.onlineplayersgui.utils;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import me.m0dii.onlineplayersgui.CustomItem;
import me.m0dii.onlineplayersgui.OnlineGUI;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
public class Config {
    private String playerHeadName;
    private ItemStack playerHeadDisplay;
    private Component configReloadedMsg;
    private Component noPermissionMsg;
    private Component noPermissionCondMsg;

    private Component nextPageName;
    private Component prevPageName;
    private Component guiTitle;

    private Component visibilityToggleMsg;

    private List<String> playerHeadLore;
    private List<String> nextPageLore;
    private List<String> prevPageLore;

    private List<String> leftClickCmds;
    private List<String> middleClickCmds;
    private List<String> rightClickCmds;

    private boolean updateOnJoin;
    private boolean updateOnLeave;
    private boolean buttonsAlwaysVisible;

    private int guiSize;

    private Material nextPageMat;
    private Material prevPageMat;

    private int nextPageSlot;
    private int prevPageSlot;

    private boolean essxHook;
    private boolean premiumVanishHook;

    private boolean conditionRequired;
    private boolean permissionRequired;

    private String condition;
    private List<String> requiredPermissions;

    private boolean DEBUG_ENABLED;

    private final OnlineGUI plugin;

    public Config(OnlineGUI plugin) {
        this.plugin = plugin;
        this.cfg = plugin.getConfig();
    }

    public void reload() {
        plugin.reloadConfig();
        cfg = plugin.getConfig();

        load();
    }

    FileConfiguration cfg;

    private boolean getBool(String path) {
        return cfg.getBoolean(path);
    }

    private Component getStringMiniMessage(@NotNull String path) {
        return TextUtils.kyorify(cfg.getString(path));
    }

    private String getStringFormatted(@NotNull String path) {
        return TextUtils.format(cfg.getString(path));
    }

    private List<String> getStringList(String path) {
        return cfg.getStringList(path).stream().map(TextUtils::format).toList();
    }

    public void load() {
        updateOnJoin = getBool("gui.update-on.join");
        updateOnLeave = getBool("gui.update-on.leave");

        buttonsAlwaysVisible = getBool("buttons-always-visible");

        DEBUG_ENABLED = cfg.getBoolean("debug", false);

        playerHeadName = getStringFormatted("player-display.name");
        playerHeadLore = getStringList("player-display.lore");

        guiTitle = getStringMiniMessage("gui.title");

        noPermissionMsg = getStringMiniMessage("messages.no-permission");
        noPermissionCondMsg = getStringMiniMessage("messages.no-permission-conditional");
        configReloadedMsg = getStringMiniMessage("messages.reload");

        visibilityToggleMsg = getStringMiniMessage("messages.toggle-visibility");

        leftClickCmds = getStringList("player-display.commands.left-click");
        middleClickCmds = getStringList("player-display.commands.middle-click");
        rightClickCmds = getStringList("player-display.commands.right-click");

        guiSize = cfg.getInt("gui.size");

        String mat1 = cfg.getString("next-button.material", "ENCHANTED_BOOK");
        String mat2 = cfg.getString("previous-button.material", "ENCHANTED_BOOK");

        prevPageMat = Material.getMaterial(mat1);
        if (prevPageMat == null) {
            prevPageMat = Material.BOOK;
        }

        nextPageMat = Material.getMaterial(mat2);
        if (nextPageMat == null) {
            nextPageMat = Material.BOOK;
        }

        if (Version.serverIsNewerThan(Version.v1_12_R1)) {
            playerHeadDisplay = new ItemStack(Material.PLAYER_HEAD);
        } else {
            Optional<XMaterial> mat = XMaterial.matchXMaterial("PLAYER_HEAD");

            if (mat.isEmpty()) {
                mat = XMaterial.matchXMaterial("SKULL_ITEM");
            }

            if (mat.isPresent()) {
                ItemStack item = mat.get().parseItem();

                if (item == null) {
                    return;
                }

                item.setDurability((short) 3);
                playerHeadDisplay = item;
            }
        }

        if (playerHeadDisplay == null) {
            playerHeadDisplay = new ItemStack(XMaterial.PLAYER_HEAD.parseMaterial());
        }

        prevPageLore = getStringList("previous-button.lore");
        nextPageLore = getStringList("next-button.lore");

        prevPageName = getStringMiniMessage("previous-button.name");
        nextPageName = getStringMiniMessage("next-button.name");

        prevPageSlot = cfg.getInt("previous-button.slot", 0);
        nextPageSlot = cfg.getInt("next-button.slot", 8);

        essxHook = getBool("hooks.essentialsx-hook");
        premiumVanishHook = getBool("hooks.premium-vanish-hook");

        conditionRequired = getBool("condition.required");
        permissionRequired = getBool("condition.permission.required");
        condition = cfg.getString("condition.placeholder");
        String requiredPermission = cfg.getString("condition.permission.node");
        requiredPermissions = cfg.getStringList("condition.permission.nodes");
        if (requiredPermission != null && !requiredPermission.isEmpty()) {
            requiredPermissions.add(requiredPermission);
        }

        setUpCustomItems(plugin);
    }

    private Map<Integer, CustomItem> customItems;

    private void setUpCustomItems(OnlineGUI plugin) {
        customItems = new HashMap<>();

        plugin.reloadConfig();

        ConfigurationSection sec = plugin.getConfig().getConfigurationSection("custom-items");

        if (sec == null) {
            return;
        }

        sec.getKeys(false).forEach(key -> {
            ConfigurationSection itemSec = sec.getConfigurationSection(key);

            if (itemSec == null) {
                return;
            }

            String itemName = itemSec.getString("material");

            if (itemName == null) {
                Messenger.warn("Invalid material for custom item: " + key);

                return;
            }

            Optional<XMaterial> xmat = Optional.empty();

            if (Utils.isDigit(itemName)) {
                xmat = XMaterial.matchXMaterial(Integer.parseInt(itemName), (byte) 0);
            }

            if (itemName.contains(":")) {
                String[] split = itemName.split(":");

                if (split.length == 2) {
                    xmat = XMaterial.matchXMaterial(Integer.parseInt(split[0]), Byte.parseByte(split[1]));
                }
            }

            Material material = xmat.map(XMaterial::parseMaterial).orElseGet(() -> Material.getMaterial(itemName));

            if (material == null) {
                material = Material.AIR; // Fallback to AIR if material is invalid
            }

            ItemStack item = new ItemStack(material);

            Component customItemName = TextUtils.kyorify(Optional.ofNullable(itemSec.getString("name"))
                    .orElse(""));

            List<String> customItemLore = itemSec.getStringList("lore").stream()
                    .map(TextUtils::format)
                    .toList();

            ItemMeta meta = item.getItemMeta();

            meta.displayName(customItemName);
            meta.setLore(customItemLore);

            List<String> lcc = itemSec.getStringList("commands.left-click");
            List<String> mcc = itemSec.getStringList("commands.middle-click");
            List<String> rcc = itemSec.getStringList("commands.right-click");

            if (itemSec.contains("slots")) {
                if (itemSec.contains("slots.start")) {
                    int start = itemSec.getInt("slots.start");
                    int end = itemSec.getInt("slots.end");

                    for (int i = start; i <= end; i++) {
                        addCustomItem(meta, i, item, lcc, mcc, rcc, customItemLore);
                    }
                } else {
                    Object slots = itemSec.get("slots");

                    if (slots instanceof List<?> slotList) {
                        for (Object slotObj : slotList) {
                            try {
                                int slot;
                                if (slotObj instanceof Integer) {
                                    slot = (Integer) slotObj;
                                } else if (slotObj instanceof String) {
                                    slot = Integer.parseInt((String) slotObj);
                                } else {
                                    Messenger.warn("Invalid slot type for custom item: " + key + " (" + slotObj + ")");
                                    continue;
                                }
                                addCustomItem(meta, slot, item, lcc, mcc, rcc, customItemLore);
                            } catch (Exception eex) {
                                Messenger.warn("Failed to parse slot for custom item: " + key + " (" + slotObj + "): " + eex.getMessage());
                            }
                        }
                    } else {
                        int slot = itemSec.getInt("slot", -1);

                        addCustomItem(meta, slot, item, lcc, mcc, rcc, customItemLore);
                    }
                }
            } else {
                int slot = itemSec.getInt("slot", -1);

                addCustomItem(meta, slot, item, lcc, mcc, rcc, customItemLore);
            }
        });
    }

    private void addCustomItem(ItemMeta meta,
                               int slot,
                               ItemStack item,
                               List<String> lcc,
                               List<String> mcc,
                               List<String> rcc,
                               List<String> customItemLore) {
        item.setItemMeta(meta);

        this.customItems.put(slot, new CustomItem(slot, item, lcc, mcc, rcc, customItemLore));
    }

    public boolean ESSX_HOOK() {
        if (this.plugin.getEssentials() == null) {
            return false;
        }

        return essxHook;
    }

    public boolean DEBUG_ENABLED() {
        return this.DEBUG_ENABLED;
    }
}
