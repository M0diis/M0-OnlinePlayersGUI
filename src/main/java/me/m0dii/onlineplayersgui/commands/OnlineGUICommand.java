package me.m0dii.onlineplayersgui.commands;

import me.m0dii.onlineplayersgui.ConditionalGUIs;
import me.m0dii.onlineplayersgui.OnlineGUI;
import me.m0dii.onlineplayersgui.inventoryholder.OnlineGUIInventory;
import me.m0dii.onlineplayersgui.utils.Config;
import me.m0dii.onlineplayersgui.utils.TextUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OnlineGUICommand implements CommandExecutor, TabCompleter {
    private final OnlineGUI plugin;
    private final ConditionalGUIs cgis;
    private final Config cfg;

    public OnlineGUICommand(OnlineGUI plugin) {
        this.plugin = plugin;
        this.cfg = plugin.getCfg();
        this.cgis = plugin.getCgis();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command cmd,
                             @NotNull String label,
                             String[] args) {
        if (args.length == 1) {
            if (alias(args[0], "reload")) {
                if (sender.hasPermission("m0onlinegui.command.reload")) {
                    this.plugin.reloadConfig();
                    this.plugin.saveConfig();

                    this.plugin.renewConfig();

                    this.cgis.loadGUIs();

                    sender.sendMessage(this.cfg.getConfigReloadedMsg());
                }
                else {
                    sender.sendMessage(this.cfg.getNoPermissionMsg());
                }
            }
        }

        if (sender instanceof Player player && args.length == 1) {

            if (cgis.isConditional(args[0])) {
                if (player.hasPermission("m0onlinegui.conditional." + args[0])) {
                    cgis.displayConditional(args[0], player);

                    return true;
                }
                else {
                    player.sendMessage(this.cfg.getNoPermissionCondMsg());
                }
            }

            if (alias(args[0], "toggleself")) {
                if (player.hasPermission("m0onlinegui.command.toggleself")) {
                    this.plugin.toggleHiddenPlayer(player);

                    player.sendMessage(this.cfg.getVisibilityToggleMsg());
                }
                else {
                    player.sendMessage(this.cfg.getNoPermissionMsg());
                }
            }

            if (alias(args[0], "version")) {
                if (player.hasPermission("m0onlinegui.command.version")) {
                    player.sendMessage(TextUtils.kyorify(
                            "&aYou are using M0-OnlinePlayersGUI version &2" +
                            this.plugin.getDescription().getVersion() + ".")
                    );
                }
                else {
                    player.sendMessage(this.cfg.getNoPermissionMsg());
                }
            }
        }

        if (sender instanceof Player player && args.length == 0) {

            OnlineGUIInventory ogi = new OnlineGUIInventory(this.plugin, this.plugin.getCfg().getGuiTitle(), 0, player);

            player.openInventory(ogi.getInventory());
        }

        return true;
    }

    private boolean alias(String arg, String... aliases) {
        return Arrays.stream(aliases).anyMatch(arg::equalsIgnoreCase);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender,
                                      @NotNull Command cmd,
                                      @NotNull String label,
                                      String[] args) {
        List<String> completes = new ArrayList<>();

        if (args.length == 1) {
            String arg0 = args[0].toLowerCase();

            if (sender.hasPermission("m0onlinegui.command.reload")) {
                if ("reload".contains(arg0)) {
                    completes.add("reload");
                }
            }

            if (sender.hasPermission("m0onlinegui.command.toggleself")) {
                if ("toggleself".contains(arg0)) {
                    completes.add("toggleself");
                }
            }

            if (sender.hasPermission("m0onlinegui.command.version")) {
                if ("version".contains(arg0)) {
                    completes.add("version");
                }
            }

            for (String cond : this.plugin.getCgis().getConditionalNames()) {
                if (sender.hasPermission("m0onlinegui.conditional." + cond.toLowerCase())) {
                    completes.add(cond);
                }
            }
        }

        return completes;
    }
}
