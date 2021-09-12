package me.m0dii.onlineplayersgui.inventoryholder;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface CustomGUI
{
    void refresh(Player p);
    
    void execute(Player p, ItemStack item, boolean left);
}
