package me.m0dii.onlineplayersgui.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import me.m0dii.onlineplayersgui.OnlineGUI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Utils {
    private Utils() {
        // Utility class, no instantiation allowed
    }

    public static boolean isDigit(@NotNull String str) {
        try {
            Double.parseDouble(str);
        } catch (NumberFormatException ex) {
            return false;
        }

        return true;
    }

    public static String setPlaceholders(@NotNull String text, @Nullable Player player) {
        if (OnlineGUI.PAPI) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }

        if (player != null) {
            text = text.replaceAll("%([pP]layer|[pP]layer(_|.*)[nN]ame)%", player.getName());
        }

        return TextUtils.format(text);
    }

}
