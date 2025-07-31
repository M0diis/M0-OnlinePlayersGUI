package me.m0dii.onlineplayersgui.utils;

import me.m0dii.onlineplayersgui.OnlineGUI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class VersionUtils {
    private VersionUtils() {
        // Utility class, no instantiation needed
    }

    public static Player getSkullOwner(ItemStack item) {
        SkullMeta sm = (SkullMeta) item.getItemMeta();

        Player skullOwner = null;

        if (Version.serverIsNewerThan(Version.v1_12_R1)) {
            skullOwner = sm.getOwningPlayer() != null ? sm.getOwningPlayer().getPlayer() : null;
        }
        else {
            String owner = sm.getOwner();

            if (owner != null) {
                skullOwner = Bukkit.getPlayer(owner);
            }
        }

        if (skullOwner == null) {
            skullOwner = Bukkit.getPlayer(TextUtils.stripColor(item.getItemMeta().displayName()));
        }

        return skullOwner;
    }

    public static ItemStack getSkull(Player player, List<String> lore, String name) {
        ItemStack head = OnlineGUI.getInstance().getCfg().getPlayerHeadDisplay();

        ItemMeta meta = head.getItemMeta();

        List<Component> newLore = lore.stream()
                .map(str -> Utils.setPlaceholders(str, player))
                .map(TextUtils::kyorify)
                .toList();

        meta.displayName(TextUtils.kyorify(Utils.setPlaceholders(name, player)));
        meta.lore(newLore);

        if (meta instanceof SkullMeta sm) {

            if (Version.getServerVersion(Bukkit.getServer()).isNewerThan(Version.v1_12_R1)) {
                sm.setOwningPlayer(player);
            }
            else {
                sm.setOwner(player.getName());
            }

            head.setItemMeta(sm);
        }

        return head;
    }
}
