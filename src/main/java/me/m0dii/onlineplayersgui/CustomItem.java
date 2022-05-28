package me.m0dii.onlineplayersgui;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CustomItem {
    private final int itemSlot;
    private ItemStack itemStack;

    boolean isSkip = false;

    private List<String> leftClickCommands;
    private List<String> middleClickCommands;
    private List<String> rightClickCommands;

    private List<String> lore;

    public CustomItem(int slot) {
        this.itemSlot = slot;
    }

    public CustomItem(ItemStack itemStack, int slot, List<String> lcc, List<String> mcc, List<String> rcc, List<String> lore) {
        this.itemStack = itemStack;

        this.itemSlot = slot;

        this.lore = lore;

        this.leftClickCommands = lcc;
        this.middleClickCommands = mcc;
        this.rightClickCommands = rcc;
    }

    public List<String> getLore() {
        return this.lore;
    }

    public ItemStack getItem() {
        return this.itemStack;
    }

    public int getItemSlot() {
        return this.itemSlot;
    }

    public List<String> getLCC() {
        return this.leftClickCommands;
    }

    public List<String> getMCC() {
        return this.middleClickCommands;
    }

    public List<String> getRCC() {
        return this.rightClickCommands;
    }

    public boolean isSkip() {
        return this.isSkip;
    }

    public void setSkip(boolean skip) {
        this.isSkip = skip;
    }
}
