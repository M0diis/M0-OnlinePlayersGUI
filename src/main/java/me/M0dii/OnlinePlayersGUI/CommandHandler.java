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
            if(args[0].equalsIgnoreCase("reload"))
            {
                if(sender.hasPermission("m0onlinegui.command.reload"))
                {
                    this.plugin.reloadConfig();
                    this.plugin.saveConfig();
                
                    this.plugin.renewConfig();
    
                    sender.sendMessage(this.config.CONFIG_RELOAD_MSG());
                }
                else sender.sendMessage(this.config.NO_PERMISSION_MSG());
            
                return true;
            }
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
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd,
                                      String label, String[] args)
    {
        List<String> completes = new ArrayList<>();
        
        if(args.length == 1)
        {
            completes.add("reload");
        }
        
        return completes;
    }
}
