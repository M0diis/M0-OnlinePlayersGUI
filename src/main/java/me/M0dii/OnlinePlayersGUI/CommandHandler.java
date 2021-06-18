package me.M0dii.OnlinePlayersGUI;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

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
    public boolean onCommand(CommandSender sender, Command cmd,
                             String alias, String[] args)
    {
        if(args.length == 1)
        {
            if(canUse(args[0], "reload", "m0onlinegui.command.reload", sender))
            {
                this.plugin.reloadConfig();
                this.plugin.saveConfig();
            
                this.plugin.renewConfig();

                sender.sendMessage(this.config.CONFIG_RELOAD_MSG());
            }
            else sender.sendMessage(this.config.NO_PERMISSION_MSG());
    
            if(sender instanceof Player)
            {
                Player p = (Player)sender;
                
                if(canUse(args[0], "toggleself", "m0onlinegui.command.toggleself", p))
                {
                    this.plugin.toggleHiddenPlayer(p);
    
                    p.sendMessage(this.config.TOGGLE_MESSAGE());
                }
            }
        
            return true;
        }
        
        if(sender instanceof Player)
        {
            Player p = (Player)sender;
            
            if(p.hasPermission("m0onlinegui.command.onlinegui"))
                new PlayerListGUI(this.plugin, 0).showPlayers(p);
            else
                p.sendMessage(this.config.NO_PERMISSION_MSG());
            
            return true;
        }

        return true;
    }
    
    private boolean canUse(String arg, String cmd, String perm, CommandSender p)
    {
        return arg.equalsIgnoreCase(cmd) && p.hasPermission(perm);
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd,
                                      String label, String[] args)
    {
        List<String> completes = new ArrayList<>();
        
        if(args.length == 1)
        {
            completes.add("reload");
            completes.add("hideself");
        }
        
        return completes;
    }
}
