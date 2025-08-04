package me.m0dii.onlineplayersgui.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;

public class CommandActionParser {

    private CommandActionParser() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void parse(@NotNull Player player, @NotNull Player placeholderHolder, @NotNull List<String> commands) {
        for (String command : commands) {
            parse(player, placeholderHolder, command);
        }
    }

    /**
     * Parses and sends the appropriate action to the player.
     * Available actions:
     * [MESSAGE] - Sends a message to the player: [MESSAGE] Hello, %player_name%!
     * [CHAT] - Sends a chat message to the player: [CHAT] Hello, %player_name%!
     * [TITLE] - Sends a title to the player: [TITLE] Hello, %player_name%!, 20, 60, 20
     * [ACTIONBAR] - Sends an action bar message to the player: [ACTIONBAR] Hello, %player_name%!
     * [SOUND] - Plays a sound to the player: [SOUND] ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1
     * [PLAYER] - Executes a command as the player: [PLAYER] /command %player_name%
     * [CONSOLE] - Executes a command as the console: [CONSOLE] /command %player_name%
     * [BROADCAST] - Broadcasts a message to the server: [BROADCAST] Hello, %player_name%!
     * [PARTICLE] - Spawns a particle at the player's location: [PARTICLE] FLAME, 1, 0, 0, 0
     *
     * @param sender The player to send the action to.
     * @param placeholderHolder The player to use for placeholders.
     * @param command The command to parse and send.
     */
    public static void parse(@NotNull Player sender, @NotNull Player placeholderHolder, @NotNull String command) {
        command = command.replaceAll("%([sS]ender|[sS]ender(_|.*)[nN]ame)%", sender.getName());
        command = Utils.setPlaceholders(command, placeholderHolder);

        if (!command.startsWith("[")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);

            return;
        }

        String action = command.substring(command.indexOf("["), command.indexOf("]") + 1);

        if (command.startsWith("[CLOSE]") || command.equalsIgnoreCase("[CLOSE]")) {
            sender.closeInventory();
        }

        int closeIdx = command.indexOf("]") + 1;
        if (closeIdx < command.length() && command.charAt(closeIdx) == ' ') {
            closeIdx++;
        }
        command = command.substring(closeIdx);

        if (action.equalsIgnoreCase("[MESSAGE]") || action.equalsIgnoreCase("[TEXT]")) {
            sender.sendMessage(TextUtils.kyorify(command));
        } else if (action.equalsIgnoreCase("[TITLE]")) {
            parseTitle(sender, command);
        } else if (action.equalsIgnoreCase("[CHAT]")) {
            sender.chat(command);
        } else if (action.equalsIgnoreCase("[SOUND]")) {
            parseSound(sender, command);
        } else if (action.equalsIgnoreCase("[PLAYER]")) {
            Bukkit.dispatchCommand(sender, command);
        } else if (action.equalsIgnoreCase("[CONSOLE]")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        } else if (action.equalsIgnoreCase("[ACTIONBAR]")) {
            sender.sendActionBar(TextUtils.kyorify(command));
        } else if (action.equalsIgnoreCase("[BROADCAST]")) {
            Bukkit.broadcast(TextUtils.kyorify(command));
        } else if (action.equalsIgnoreCase("[PARTICLE]")) {
            parseParticle(sender, command);
        } else if(!StringUtils.isBlank(command)) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }

    private static void parseParticle(@NotNull Player player, @NotNull String command) {
        String[] split = command.split(", ");

        if (split.length == 5) {
            try {
                int count = Integer.parseInt(split[1]);
                float offsetX = Float.parseFloat(split[2]);
                float offsetY = Float.parseFloat(split[3]);
                float offsetZ = Float.parseFloat(split[4]);

                player.spawnParticle(Particle.valueOf(split[0]), player.getLocation(), count, offsetX, offsetY, offsetZ);
            } catch (Exception ex) {
                Messenger.warn("Invalid particle format: " + command);
                Messenger.debug(ex.getMessage());
            }
        }
    }

    private static void parseTitle(@NotNull Player player, @NotNull String command) {
        String[] split = command.split(", ");

        int fadeIn = 20;
        int stay = 60;
        int fadeOut = 20;

        Title.Times defaultTimes = Title.Times.times(Duration.ofSeconds(fadeIn / 20),
                Duration.ofSeconds(stay / 20),
                Duration.ofSeconds(fadeOut / 20));

        if (split.length == 1) {
            Title title = Title.title(
                    TextUtils.colorize(command),
                    Component.empty(),
                    defaultTimes
            );

            player.showTitle(title);

            return;
        }

        if (split.length == 2) {
            Title title = Title.title(
                    TextUtils.colorize(split[0]),
                    TextUtils.colorize(split[1]),
                    defaultTimes
            );

            player.showTitle(title);

            return;
        }

        if (split.length == 4) {
            try {
                fadeIn = Integer.parseInt(split[1]);
                stay = Integer.parseInt(split[2]);
                fadeOut = Integer.parseInt(split[3]);

                Title title = Title.title(
                        TextUtils.colorize(split[0]),
                        Component.empty(),
                        Title.Times.times(Duration.ofSeconds(fadeIn / 20),
                                Duration.ofSeconds(stay / 20),
                                Duration.ofSeconds(fadeOut / 20))
                );

                player.showTitle(title);
            } catch (NumberFormatException ex) {
                Messenger.warn("Invalid fade-in, stay, or fade-out time for title action.");
                Messenger.debug(ex.getMessage());
            }

            return;
        }

        if (split.length == 5) {
            try {
                fadeIn = Integer.parseInt(split[2]);
                stay = Integer.parseInt(split[3]);
                fadeOut = Integer.parseInt(split[4]);

                Title title = Title.title(
                        TextUtils.colorize(split[0]),
                        TextUtils.colorize(split[1]),
                        Title.Times.times(Duration.ofSeconds(fadeIn / 20),
                                Duration.ofSeconds(stay / 20),
                                Duration.ofSeconds(fadeOut / 20))
                );

                player.showTitle(title);
            } catch (NumberFormatException ex) {
                Messenger.warn("Invalid fadeIn, stay, or fadeOut time for title action.");
                Messenger.debug(ex.getMessage());
            }
        }
    }

    private static void parseSound(@NotNull Player player, String command) {
        String[] split = command.split(", ");

        if (split.length == 2) {
            try {
                player.playSound(player.getLocation(), Sound.valueOf(split[0]), Float.parseFloat(split[1]), Float.parseFloat(split[1]));
            } catch (Exception ex) {
                Messenger.warn("Invalid sound format: " + command);
                Messenger.debug(ex.getMessage());
            }
        }
    }
}
