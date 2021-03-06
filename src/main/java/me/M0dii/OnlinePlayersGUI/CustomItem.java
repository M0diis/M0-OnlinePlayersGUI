package me.M0dii.OnlinePlayersGUI;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CustomItem
{
    private final int itemSlot;
    private final ItemStack itemStack;
    
    private final List<String> leftClickCommands;
    private final List<String> rightClickCommands;
    
    private final List<String> lore;
    
    private final boolean closeOnLeftClick;
    private final boolean closeOnRightClick;
    
    public CustomItem(ItemStack itemStack, int slot,
                      List<String> lcc, List<String> rcc,
                      boolean closeOnLeft, boolean closeOnRight,
                      List<String> lore)
    {
        this.itemStack = itemStack;
        
        this.itemSlot = slot;
        
        this.lore = lore;
        
        this.closeOnLeftClick = closeOnLeft;
        this.closeOnRightClick = closeOnRight;
        
        this.leftClickCommands = lcc;
        this.rightClickCommands = rcc;
    }
    
    public List<String> getLore()
    {
        return this.lore;
    }
    
    public ItemStack getItem()
    {
        return this.itemStack;
    }
    
    public int getItemSlot()
    {
        return this.itemSlot;
    }
    
    public List<String> getLCC()
    {
        return this.leftClickCommands;
    }
    
    public List<String> getRCC()
    {
        return this.rightClickCommands;
    }
    
    public boolean closeOnLeft()
    {
        return this.closeOnLeftClick;
    }
    
    public boolean closeOnRight()
    {
        return this.closeOnRightClick;
    }
}
