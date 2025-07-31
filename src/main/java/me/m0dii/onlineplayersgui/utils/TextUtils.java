package me.m0dii.onlineplayersgui.utils;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public class TextUtils {
    private TextUtils() {
        // Utility class, no instantiation
    }

    private static final MiniMessage USER_MESSAGE_MINI_MESSAGE = MiniMessage.miniMessage();
    public static final Pattern DEFAULT_URL_PATTERN = Pattern.compile("(?:(https?)://)?([-\\w_.]+\\.\\w{2,})(/\\S*)?");
    public static final Pattern URL_SCHEME_PATTERN = Pattern.compile("^[a-z][a-z\\d+\\-.]*:");

    private static final TextReplacementConfig URL_REPLACER_CONFIG = TextReplacementConfig.builder()
            .match(DEFAULT_URL_PATTERN)
            .replacement(builder -> {
                String clickUrl = builder.content();
                if (!URL_SCHEME_PATTERN.matcher(clickUrl).find()) {
                    clickUrl = "https://" + clickUrl;
                }
                return builder.clickEvent(ClickEvent.openUrl(clickUrl));
            })
            .build();

    public static Component kyorify(@Nullable String message) {
        if (message == null || message.isEmpty()) {
            return Component.empty();
        }

        return USER_MESSAGE_MINI_MESSAGE.deserialize(Kyorifier.kyorify(message)).replaceText(URL_REPLACER_CONFIG);
    }

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9])([A-Fa-f0-9])([A-Fa-f0-9])([A-Fa-f0-9])([A-Fa-f0-9])([A-Fa-f0-9])");

    public static String stripColor(Component component) {
        return ChatColor.stripColor(PlainTextComponentSerializer.plainText().serializeOr(component, ""));
    }

    public static String stripColor(@NotNull String text) {
        return ChatColor.stripColor(text);
    }

    public static Component colorize(@Nullable String text) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }

        return LegacyComponentSerializer.legacyAmpersand().deserialize(text)
                .asComponent()
                .decoration(TextDecoration.ITALIC, false);
    }

    public static String format(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        return ChatColor.translateAlternateColorCodes(
                '&',
                HEX_PATTERN.matcher(text).replaceAll("&x&$1&$2&$3&$4&$5&$6")
        );
    }
}