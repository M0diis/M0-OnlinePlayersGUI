package me.M0dii.OnlinePlayersGUI;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CustomItem
{
    private final ItemStack itemStack;
    private int itemSlot;
    private final List<String> leftClickCommands;
    private final List<String> rightClickCommands;
    
    public CustomItem(ItemStack itemStack, int slot,
                      List<String> lcc, List<String> rcc)
    {
        this.itemStack = itemStack;
        
        this.itemSlot = slot;
        
        this.leftClickCommands = lcc;
        this.rightClickCommands = rcc;
    }
    
    public ItemStack getItem()
    {
        return this.itemStack;
    }
    
    public int getItemSlot()
    {
        return this.itemSlot;
    }
    
    public List<String> getLeftClickCommands()
    {
        return this.leftClickCommands;
    }
    
    public List<String> getRightClickCommands()
    {
        return this.rightClickCommands;
    }
}
