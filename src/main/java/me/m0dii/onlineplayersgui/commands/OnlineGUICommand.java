package me.m0dii.onlineplayersgui.commands;

import me.m0dii.onlineplayersgui.ConditionalGUIs;
import me.m0dii.onlineplayersgui.inventoryholder.OnlineGUIInventory;
import me.m0dii.onlineplayersgui.OnlineGUI;
import me.m0dii.onlineplayersgui.utils.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OnlineGUICommand implements CommandExecutor, TabCompleter
{
    private final OnlineGUI plugin;
    private final Config config;
    
    public OnlineGUICommand(OnlineGUI plugin)
    {
        this.plugin = plugin;
        this.config = plugin.getCfg();
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd,
                             @NotNull String alias, @NotNull String[] args)
    {
        ConditionalGUIs cgis = plugin.getCgis();
        
        if(args.length == 1)
        {
            if(alias(args[0], "reload"))
            {
                if(sender.hasPermission("m0onlinegui.command.reload"))
                {
                    this.plugin.reloadConfig();
                    this.plugin.saveConfig();
            
                    this.plugin.renewConfig();
            
                    cgis.loadGUIs();
            
                    sender.sendMessage(this.config.getCfgReloadMsg());
                }
                else sender.sendMessage(this.config.getNoPermMsg());
            }
        }
        
        if(sender instanceof Player p && args.length == 1)
        {
            if(cgis.isConditional(args[0]))
            {
                if(p.hasPermission("m0onlinegui.conditional." + args[0]))
                {
                    cgis.displayConditional(args[0], p);
    
                    return true;
                }
                else p.sendMessage(this.config.getNoPermissionCondMsg());
            }
            
            if(alias(args[0], "toggleself"))
            {
                if(p.hasPermission("m0onlinegui.command.toggleself"))
                {
                    this.plugin.toggleHiddenPlayer(p);
        
                    p.sendMessage(this.config.getToggleMsg());
                }
                else p.sendMessage(this.config.getNoPermMsg());
            }
        }
        
        if(sender instanceof Player p && args.length == 0)
        {
            OnlineGUIInventory ogi = new OnlineGUIInventory(this.plugin,
                    this.plugin.getCfg().getGuiTitle(), 0, p);
            
            p.openInventory(ogi.getInventory());
        }

        return true;
    }
    
    private boolean alias(String arg, String... aliases)
    {
        return Arrays.stream(aliases).anyMatch(arg::equalsIgnoreCase);
    }
    
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
                                      @NotNull String label, @NotNull String[] args)
    {
        List<String> completes = new ArrayList<>();
        
        if(args.length == 1)
        {
            if(sender.hasPermission("m0onlinegui.command.reload"))
                completes.add("reload");
            
            if(sender.hasPermission("m0onlinegui.command.toggleself"))
                completes.add("toggleself");
            
            for(String cond : this.plugin.getCgis().getConditionalNames())
                if(sender.hasPermission("m0onlinegui.conditional." + cond.toLowerCase()))
                    completes.add(cond);
        }
        
        return completes;
    }
}
