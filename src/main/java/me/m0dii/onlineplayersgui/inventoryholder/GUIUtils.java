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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GUIUtils {
    final OnlineGUI plugin;

    public GUIUtils(OnlineGUI plugin) {
        this.plugin = plugin;
    }

    public List<Player> getOnline(@Nullable List<String> permissions, @Nullable String condition) {
        List<Player> online;

        List<Player> toggled = plugin.getHiddenPlayersToggled();

        if (plugin.getCfg().ESSX_HOOK()) {
            online = Bukkit.getOnlinePlayers().stream()
                    .filter(p -> !p.hasPermission("m0onlinegui.hidden") ||
                            !plugin.getEssentials().getUser(p).isVanished() || !toggled.contains(p))
                    .collect(Collectors.toList());
        } else {
            online = Bukkit.getOnlinePlayers().stream()
                    .filter(p -> !p.hasPermission("m0onlinegui.hidden")
                            || !toggled.contains(p))
                    .collect(Collectors.toList());
        }

        if (permissions != null && !permissions.isEmpty()) {
            online = online.stream().filter(onlinePlayer -> permissions.stream()
                            .anyMatch(onlinePlayer::hasPermission))
                    .toList();
        }

        if (condition != null) {
            return filterByCondition(online, condition);
        }

        return online;
    }

    public void setCustomItems(@NotNull Inventory inv, @NotNull Player player, @NotNull Map<Integer, CustomItem> customItems) {
        for (Map.Entry<Integer, CustomItem> entry : customItems.entrySet()) {
            CustomItem c = entry.getValue();
            ItemStack item = c.getItem();

            ItemMeta m = item.getItemMeta();

            int slot = c.getItemSlot();

            List<String> lore =
                    c.getLore().stream().map(str -> PlaceholderAPI.setPlaceholders(player, str)).collect(Collectors.toList());

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

                if (!Utils.isDigit(leftStr) && !Utils.isDigit(rightStr)) {
                    final String leftStrPlaceholders = PlaceholderAPI.setPlaceholders(p, leftStr);
                    final String rightStrPlaceholders = PlaceholderAPI.setPlaceholders(p, rightStr);

                    switch (op) {
                        case "=", "==", "eq", "equal_to", "equals" -> {
                            if (leftStrPlaceholders.equalsIgnoreCase(rightStrPlaceholders)) {
                                filtered.add(p);
                            }
                        }
                        case "!=", "neq", "ne", "not_equal" -> {
                            if (!leftStrPlaceholders.equalsIgnoreCase(rightStrPlaceholders)) {
                                filtered.add(p);
                            }
                        }
                        case "~=", "~~", "contains", "contains_string" -> {
                            if (leftStrPlaceholders.toLowerCase().contains(rightStrPlaceholders.toLowerCase())) {
                                filtered.add(p);
                            }
                        }
                        case "!~=", "!~~", "!contains", "not_contains", "not_contains_string" -> {
                            if (!leftStrPlaceholders.toLowerCase().contains(rightStrPlaceholders.toLowerCase())) {
                                filtered.add(p);
                            }
                        }
                        case "starts", "starts_with" -> {
                            if (leftStrPlaceholders.toLowerCase().startsWith(rightStrPlaceholders.toLowerCase())) {
                                filtered.add(p);
                            }
                        }
                        case "ends", "ends_with" -> {
                            if (leftStrPlaceholders.toLowerCase().endsWith(rightStrPlaceholders.toLowerCase())) {
                                filtered.add(p);
                            }
                        }
                        case "matches", "regex" -> {
                            if (leftStrPlaceholders.matches(rightStrPlaceholders)) {
                                filtered.add(p);
                            }
                        }
                        case "!matches", "!regex", "not_matches", "not_regex" -> {
                            if (!leftStrPlaceholders.matches(rightStrPlaceholders)) {
                                filtered.add(p);
                            }
                        }
                        default -> {
                        }
                    }

                    continue;
                }


                double left = Double.parseDouble(PlaceholderAPI.setPlaceholders(p, leftStr).replaceAll("[a-zA-Z!@#$&*()" +
                        "/\\\\\\[\\]{}:\"?]", ""));

                double right = Double.parseDouble(PlaceholderAPI.setPlaceholders(p, rightStr).replaceAll("[a-zA-Z!@#$&*()" +
                        "/\\\\\\[\\]{}:\"?]", ""));

                switch (op) {
                    case ">", "gt", "greater_than" -> {
                        if (left > right) {
                            filtered.add(p);
                        }
                    }
                    case "<", "lt", "less_than" -> {
                        if (left < right) {
                            filtered.add(p);
                        }
                    }
                    case "<=", "lte", "less_than_or_equal" -> {
                        if (left <= right) {
                            filtered.add(p);
                        }
                    }
                    case ">=", "gte", "greater_than_or_equal" -> {
                        if (left >= right) {
                            filtered.add(p);
                        }
                    }
                    case "=", "==", "eq", "equal_to", "equals" -> {
                        if (left == right) {
                            filtered.add(p);
                        }
                    }
                    case "!=", "neq", "ne", "not_equal" -> {
                        if (left != right) {
                            filtered.add(p);
                        }
                    }
                    case "~=", "~~", "contains", "contains_string" -> {
                        if (String.valueOf(left).toLowerCase().contains(String.valueOf(right).toLowerCase())) {
                            filtered.add(p);
                        }
                    }
                    case "!~=", "!~~", "!contains", "not_contains", "not_contains_string" -> {
                        if (!String.valueOf(left).toLowerCase().contains(String.valueOf(right).toLowerCase())) {
                            filtered.add(p);
                        }
                    }
                    case "starts", "starts_with" -> {
                        if (String.valueOf(left).toLowerCase().startsWith(String.valueOf(right).toLowerCase())) {
                            filtered.add(p);
                        }
                    }
                    case "ends", "ends_with" -> {
                        if (String.valueOf(left).toLowerCase().endsWith(String.valueOf(right).toLowerCase())) {
                            filtered.add(p);
                        }
                    }
                    case "matches", "regex" -> {
                        if (String.valueOf(left).matches(String.valueOf(right))) {
                            filtered.add(p);
                        }
                    }
                    case "!matches", "!regex", "not_matches", "not_regex" -> {
                        if (!String.valueOf(left).matches(String.valueOf(right))) {
                            filtered.add(p);
                        }
                    }
                    default -> {
                    }
                }
            }
        } catch (NumberFormatException ex) {
            Messenger.warn("Error occured trying to parse the condition.");
        }

        return filtered;
    }
}
