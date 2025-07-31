package me.m0dii.onlineplayersgui;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CustomItem {
    private final int itemSlot;
    private ItemStack item;

    private List<String> lcc;
    private List<String> mcc;
    private List<String> rcc;

    private List<String> lore;

}
