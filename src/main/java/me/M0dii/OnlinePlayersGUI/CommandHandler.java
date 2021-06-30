package me.M0dii.OnlinePlayersGUI;

import me.M0dii.OnlinePlayersGUI.InventoryHolder.OnlineGUIInventory;
import me.M0dii.OnlinePlayersGUI.Utils.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler implements CommandExecutor, TabCompleter
{
    private final OnlineGUI plugin;
    private final Config config;
    
    public CommandHandler(OnlineGUI plugin)
    {
        this.plugin = plugin;
        this.config = plugin.getCfg();
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd,
                             @NotNull String alias, @NotNull String[] args)
    {
        if(args.length == 1)
        {
            ConditionalGUIs cgis = plugin.getCgis();
            
            if(cgis.isConditional(args[0]))
            {
                cgis.displayConditional(args[0], (Player)sender);
                
                return true;
            }
            
            if(isCommand(args[0], "reload"))
            {
                if(sender.hasPermission("m0onlinegui.command.reload"))
                {
                    this.plugin.reloadConfig();
                    this.plugin.saveConfig();
    
                    this.plugin.renewConfig();
                    
                    cgis.loadGUIs();
    
                    sender.sendMessage(this.config.CONFIG_RELOAD_MSG());
                }
                else sender.sendMessage(this.config.NO_PERMISSION_MSG());
            }
            if(sender instanceof Player)
            {
                Player p = (Player)sender;
    
                if(isCommand(args[0], "toggleself"))
                {
                    if(sender.hasPermission("m0onlinegui.command.toggleself"))
                    {
                        this.plugin.toggleHiddenPlayer(p);
            
                        p.sendMessage(this.config.TOGGLE_MESSAGE());
                    }
                    else sender.sendMessage(this.config.NO_PERMISSION_MSG());
                }
            }
        
            return true;
        }
        
        if(sender instanceof Player)
        {
            Player p = (Player)sender;
            
            OnlineGUIInventory ogi = new OnlineGUIInventory(this.plugin, this.plugin.getCfg().GUI_TITLE(), 0);
            ogi.setCustomItems(p);
            
            p.openInventory(ogi.getInventory());
    
            return true;
        }

        return true;
    }
    
    private boolean isCommand(String arg, String cmd)
    {
        return arg.equalsIgnoreCase(cmd);
    }
    
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
                                      @NotNull String label, @NotNull String[] args)
    {
        List<String> completes = new ArrayList<>();
        
        if(args.length == 1)
        {
            completes.add("reload");
            completes.add("toggleself");
    
            completes.addAll(new ConditionalGUIs(this.plugin).getConditionalNames());
        }
        
        return completes;
    }
}
