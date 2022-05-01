package me.m0dii.onlineplayersgui.utils;

import me.m0dii.onlineplayersgui.OnlineGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.stream.Collectors;

public class VersionUtils
{
    public static Player getSkullOwner(ItemStack item)
    {
        SkullMeta sm = (SkullMeta)item.getItemMeta();
    
        Player skullOwner = null;
    
        if(Version.serverIsNewerThan(Version.v1_12_R1))
        {
            skullOwner = sm.getOwningPlayer() != null ? sm.getOwningPlayer().getPlayer() : null;
        }
        else
        {
            String owner = sm.getOwner();
        
            if(owner != null)
            {
                skullOwner = Bukkit.getPlayer(owner);
            }
        }
    
        if(skullOwner == null)
        {
            skullOwner = Bukkit.getPlayer(Utils.clearFormat(item.getItemMeta().getDisplayName()));
        }
        
        return skullOwner;
    }
    
    public static ItemStack getSkull(Player player, List<String> lore, String name)
    {
        ItemStack head = OnlineGUI.getInstance().getCfg().getDisplay();
    
        ItemMeta meta = head.getItemMeta();
    
        lore = lore.stream()
                .map(str -> Utils.setPlaceholders(str, player))
                .collect(Collectors.toList());
        
        meta.setDisplayName(Utils.setPlaceholders(name, player));
        meta.setLore(lore);
    
        if(meta instanceof SkullMeta)
        {
            SkullMeta sm = (SkullMeta)meta;
        
            if(Version.getServerVersion(Bukkit.getServer()).isNewerThan(Version.v1_12_R1))
            {
                sm.setOwningPlayer(player);
            }
            else
            {
                sm.setOwner(player.getName());
            }
        
            head.setItemMeta(sm);
        }
        
        return head;
    }
}
