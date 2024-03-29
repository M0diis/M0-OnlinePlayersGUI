package me.m0dii.onlineplayersgui.inventoryholder;

import me.clip.placeholderapi.PlaceholderAPI;
import me.m0dii.onlineplayersgui.CustomItem;
import me.m0dii.onlineplayersgui.OnlineGUI;
import me.m0dii.onlineplayersgui.utils.Messenger;
import me.m0dii.onlineplayersgui.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class GUIUtils {
    final OnlineGUI plugin;

    public GUIUtils(OnlineGUI plugin) {
        this.plugin = plugin;
    }

    public List<Player> getOnline(String permission, String condition) {
        List<Player> online;

        List<Player> toggled = plugin.getHiddenPlayersToggled();

        if (plugin.getCfg().ESSX_HOOK()) {
            online =
                    Bukkit.getOnlinePlayers().stream().filter(p -> !p.hasPermission("m0onlinegui.hidden") ||
                            !plugin.getEssentials().getUser(p).isVanished() || !toggled.contains(p))
                            .collect(Collectors.toList());
        }
        else {
            online =
                    Bukkit.getOnlinePlayers().stream().filter(p -> !p.hasPermission("m0onlinegui.hidden")
                            || !toggled.contains(p))
                            .collect(Collectors.toList());
        }

        if (permission != null) {
            online = online.stream().filter(p -> p.hasPermission(permission)).collect(Collectors.toList());
        }

        if (condition != null) {
            return filterByCondition(online, condition);
        }

        return online;
    }

    public void setCustomItems(Inventory inv, Player p, Map<Integer, CustomItem> customItems) {
        for (Map.Entry<Integer, CustomItem> entry : customItems.entrySet()) {
            CustomItem c = entry.getValue();
            ItemStack item = c.getItem();

            ItemMeta m = item.getItemMeta();

            int slot = c.getItemSlot();

            List<String> lore =
                    c.getLore().stream().map(str -> PlaceholderAPI.setPlaceholders(p, str)).collect(Collectors.toList());

            m.setLore(lore);

            item.setItemMeta(m);

            if (inv.getItem(slot) == null) {
                inv.setItem(slot, item);
            }
        }
    }

    public List<Player> filterByCondition(List<Player> players, String cond) {
        List<Player> filtered = new ArrayList<>();

        List<String> condSplit = Arrays.asList(cond.split(" "));

        if (condSplit.size() != 3) {
            for (Player p : players) {
                String result = PlaceholderAPI.setPlaceholders(p, cond).toLowerCase();

                if (result.equals("yes") || result.equals("true")) {
                    filtered.add(p);
                }
            }

            return filtered;
        }

        String op = condSplit.get(1);

        try {
            for (Player p : players) {
                String leftStr = condSplit.get(0);
                String rightStr = condSplit.get(2);

                if (Objects.equals(op, "=") || Objects.equals(op, "==")) {
                    if (!Utils.isDigit(leftStr) && !Utils.isDigit(rightStr)) {
                        if (leftStr.equalsIgnoreCase(rightStr)) {
                            filtered.add(p);
                        }

                        continue;
                    }
                }

                double left = Double.parseDouble(PlaceholderAPI.setPlaceholders(p, leftStr).replaceAll("[a-zA-Z!@#$&*()" +
                        "/\\\\\\[\\]{}:\"?]", ""));

                double right = Double.parseDouble(PlaceholderAPI.setPlaceholders(p, rightStr).replaceAll("[a-zA-Z!@#$&*()" +
                        "/\\\\\\[\\]{}:\"?]", ""));

                switch (op) {
                    case ">":
                    case "greater_than":
                        if (left > right) {
                            filtered.add(p);
                        }
                        break;

                    case "<":
                    case "less_than":
                        if (left < right) {
                            filtered.add(p);
                        }
                        break;

                    case "<=":
                    case "less_than_or_equal":
                        if (left <= right) {
                            filtered.add(p);
                        }
                        break;

                    case ">=":
                    case "greater_than_or_equal":
                        if (left >= right) {
                            filtered.add(p);
                        }
                        break;

                    case "=":
                    case "==":
                    case "equal_to":
                    case "equals":
                        if (left == right) {
                            filtered.add(p);
                        }
                        break;

                    case "!=":
                    case "not_equal":
                        if (left != right) {
                            filtered.add(p);
                        }
                        break;

                    default:
                        break;
                }
            }
        }
        catch (NumberFormatException ex) {
            Messenger.warn("Error occured trying to parse the condition.");
        }

        return filtered;
    }
}
