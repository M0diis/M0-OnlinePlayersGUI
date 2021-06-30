package me.M0dii.OnlinePlayersGUI.InventoryHolder;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface CustomGUI
{
    void refresh(Player p);
    
    void execute(Player p, ItemStack item, boolean left);
}
