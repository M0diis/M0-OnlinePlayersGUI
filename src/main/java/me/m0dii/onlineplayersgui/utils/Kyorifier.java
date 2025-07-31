package me.m0dii.onlineplayersgui.utils;


import com.google.common.collect.ImmutableMap;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Kyorifier {
    private Kyorifier() {
        // Utility class, no instantiation.
    }

    private static final ImmutableMap<Character, String> COLOURS = new ImmutableMap.Builder<Character, String>()
            .put('0', NamedTextColor.BLACK.toString())
            .put('1', NamedTextColor.DARK_BLUE.toString())
            .put('2', NamedTextColor.DARK_GREEN.toString())
            .put('3', NamedTextColor.DARK_AQUA.toString())
            .put('4', NamedTextColor.DARK_RED.toString())
            .put('5', NamedTextColor.DARK_PURPLE.toString())
            .put('6', NamedTextColor.GOLD.toString())
            .put('7', NamedTextColor.GRAY.toString())
            .put('8', NamedTextColor.DARK_GRAY.toString())
            .put('9', NamedTextColor.BLUE.toString())
            .put('a', NamedTextColor.GREEN.toString())
            .put('b', NamedTextColor.AQUA.toString())
            .put('c', NamedTextColor.RED.toString())
            .put('d', NamedTextColor.LIGHT_PURPLE.toString())
            .put('e', NamedTextColor.YELLOW.toString())
            .put('f', NamedTextColor.WHITE.toString())
            .build();

    private static final ImmutableMap<Character, String> FORMATTERS = new ImmutableMap.Builder<Character, String>()
            .put('k', TextDecoration.OBFUSCATED.toString())
            .put('l', TextDecoration.BOLD.toString())
            .put('m', TextDecoration.STRIKETHROUGH.toString())
            .put('n', TextDecoration.UNDERLINED.toString())
            .put('o', TextDecoration.ITALIC.toString())
            // There is no way to get the reset tag name from Adventure. Also doesn't really matter as we never actually
            // use it. Instead, we close all the opened tags.
            .put('r', "reset")
            .build();

    private static final Pattern LEGACY_HEX_COLORS_PATTERN = Pattern.compile(
            "&(?<code>[\\da-fk-or])?",
            Pattern.CASE_INSENSITIVE // Turns out colors are not case-sensitive.
    );

    private static StringBuilder closeAll(Deque<String> activeFormatters) {
        final var out = new StringBuilder();
        while (!activeFormatters.isEmpty()) {
            out.append("</").append(activeFormatters.pop()).append(">");
        }
        return out;
    }

    public static String kyorify(String input) {
        final Deque<String> activeFormatters = new ArrayDeque<>();
        return LEGACY_HEX_COLORS_PATTERN.matcher(input.replace("§", "&")).replaceAll(result -> {
            final Matcher matcher = (Matcher) result;

            final var code = matcher.group("code");

            final var colour = COLOURS.get(Character.toLowerCase(code.charAt(0)));

            if (colour == null) {
                final var formatter = FORMATTERS.get(Character.toLowerCase(code.charAt(0)));
                if (formatter != null && formatter.equals("reset")) {
                    return closeAll(activeFormatters).toString();
                }
                activeFormatters.push(formatter);
                return "<" + formatter + ">";
            }

            final var out = closeAll(activeFormatters);
            out.append("<").append(colour).append(">");
            return out.toString();
        });
    }

}