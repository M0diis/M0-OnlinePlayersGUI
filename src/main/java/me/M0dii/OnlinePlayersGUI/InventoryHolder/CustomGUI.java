package me.M0dii.OnlinePlayersGUI.InventoryHolder;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface CustomGUI
{
    public void refresh(Player p);
    
    public void execute(Player p, ItemStack item, boolean left);
}
