package me.m0dii.onlineplayersgui.utils;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import me.m0dii.onlineplayersgui.CustomItem;
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
public class ConditionalConfig {
    private String headName;
    private ItemStack playerHeadDisplay;
    private Component nextPageName, prevPageName;
    private Component guiTitle;

    private List<String> headLore, nextPageLore, prevPageLore;
    private List<String> leftClickCommands, middleClickCommands, rightClickCommands;

    private int guiSize;

    private Material nextPageMat, prevPageMat;
    private int nextPageSlot, prevPageSlot;

    private boolean permissionRequired;
    private String condition;
    private List<String> requiredPermissions;

    public ConditionalConfig(@NotNull FileConfiguration cfg) {
        this.cfg = cfg;

        this.load();
    }

    final FileConfiguration cfg;

    private Component getStringMiniMessage(@NotNull String path) {
        return TextUtils.kyorify(cfg.getString(path));
    }

    private String getStringFormatted(@NotNull String path) {
        return TextUtils.format(cfg.getString(path));
    }

    private List<String> getStringList(String path) {
        return cfg.getStringList(path);
    }

    public void load() {
        headName = getStringFormatted("player-display.name");
        headLore = getStringList("player-display.lore");

        guiTitle = getStringMiniMessage("gui.title");

        leftClickCommands = getStringList("player-display.commands.left-click");
        middleClickCommands = getStringList("player-display.commands.middle-click");
        rightClickCommands = getStringList("player-display.commands.right-click");

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

        prevPageSlot = cfg.getInt("previous-button.slot");
        nextPageSlot = cfg.getInt("next-button.slot");

        permissionRequired = cfg.getBoolean("condition.permission.required");
        condition = cfg.getString("condition.placeholder");

        String requiredPermission = cfg.getString("condition.permission.node");
        requiredPermissions = cfg.getStringList("condition.permission.nodes");
        if (requiredPermission != null && !requiredPermission.isEmpty()) {
            requiredPermissions.add(requiredPermission);
        }

        setUpCustomItems();
    }

    private Map<Integer, CustomItem> customItems;

    private void setUpCustomItems() {
        customItems = new HashMap<>();

        ConfigurationSection sec = cfg.getConfigurationSection("custom-items");

        if (sec == null) {
            return;
        }

        sec.getKeys(false).forEach(key -> {
            ConfigurationSection itemSec = sec.getConfigurationSection(key);

            if (itemSec == null) {
                return;
            }

            String itemName = itemSec.getString("material", "BOOK");

            Material customItem = Material.getMaterial(itemName);

            if (customItem != null && !customItem.equals(Material.AIR)) {
                ItemStack item = new ItemStack(customItem);

                Component customItemName = TextUtils.kyorify(itemSec.getString("name"));

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

                        if (slots instanceof List) {
                            List<Integer> slotList = (List<Integer>) slots;

                            for (Integer slot : slotList) {
                                addCustomItem(meta, slot, item, lcc, mcc, rcc, customItemLore);
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
            }
        });
    }

    private void addCustomItem(ItemMeta meta, int slot, ItemStack item, List<String> lcc, List<String> mcc, List<String> rcc,
                               List<String> customItemLore) {
        item.setItemMeta(meta);

        customItems.put(slot, new CustomItem(slot, item, lcc, mcc, rcc, customItemLore));
    }

}