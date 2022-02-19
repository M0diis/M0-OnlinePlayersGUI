package me.m0dii.onlineplayersgui.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Constructor;

public class VersionUtils
{
    private static String OBC_PREFIX;
    
    static
    {
        try {
            OBC_PREFIX = Bukkit.getServer().getClass().getPackage().getName();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static ItemStack getItemStack(final String item)
    {
        if (item.contains(":"))
        {
            final String[] split = item.split(":");
            
            if (split[0].matches("\\d+"))
            {
                String ID = split[0];
                short metadata = 0;
                
                try
                {
                    metadata = Short.parseShort(split[1]);
                }
                catch (NumberFormatException ignored) { }
                
                try
                {
                    final Constructor<?> constructor = Class.forName(OBC_PREFIX + ".inventory.CraftItemStack")
                            .getDeclaredConstructor(int.class, int.class, short.class,
                                    ItemMeta.class);
                    constructor.setAccessible(true);
                    
                    return (ItemStack) constructor.newInstance(Integer.parseInt(ID), 1, metadata, null);
                }
                catch (Exception ex)
                {
                    Messenger.debug("Failed to get " + OBC_PREFIX + ".inventory.CraftItemStack Constructor");
                    
                    Material m = Material.getMaterial(split[0]);
                    
                    try
                    {
                        if(m == null)
                        {
                            return new ItemStack(Material.getMaterial(split[0]), 1, metadata);
                        }
                    }
                    catch (NumberFormatException ignored) { }
                    
                    return new ItemStack(Material.getMaterial(split[0]), 1, metadata);
                }
            }
        }
        
        if (item.matches("\\d+"))
        {
            try
            {
                final Constructor<?> constructor = Class.forName(OBC_PREFIX + ".inventory.CraftItemStack")
                        .getDeclaredConstructor(int.class, int.class, short.class, ItemMeta.class);
                
                constructor.setAccessible(true);
                
                return (ItemStack) constructor.newInstance(Integer.parseInt(item), 1, (short) 0, null);
            }
            catch (Exception ex)
            {
                Messenger.debug("Failed to get " + OBC_PREFIX + ".inventory.CraftItemStack Constructor");
                
                return new ItemStack(Material.getMaterial(item));
            }
        }
        
        return new ItemStack(Material.getMaterial(item));
    }
}
