package me.m0dii.onlineplayersgui;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CustomItem
{
    private final int itemSlot;
    private final ItemStack itemStack;
    
    private final List<String> leftClickCommands;
    private final List<String> middleClickCommands;
    private final List<String> rightClickCommands;
    
    private final List<String> lore;
    
    public CustomItem(ItemStack itemStack, int slot,
                      List<String> lcc, List<String> mcc, List<String> rcc,
                      List<String> lore)
    {
        this.itemStack = itemStack;
        
        this.itemSlot = slot;
        
        this.lore = lore;
        
        this.leftClickCommands = lcc;
        this.middleClickCommands = mcc;
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
    
    public List<String> getMCC()
    {
        return this.middleClickCommands;
    }
    
    public List<String> getRCC()
    {
        return this.rightClickCommands;
    }
}
