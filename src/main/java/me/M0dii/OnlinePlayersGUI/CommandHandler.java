package me.M0dii.OnlinePlayersGUI;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CommandHandler implements CommandExecutor, TabCompleter
{
    private final Main plugin;
    
    public CommandHandler(Main plugin)
    {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd,
                             @Nonnull String alias, @Nonnull String[] args)
    {
        if(sender instanceof Player)
        {
            Player p = (Player)sender;
            
            if(args.length == 1)
            {
                if(args[0].equalsIgnoreCase("reload"))
                {
                    if(p.hasPermission("m0onlinegui.reload"))
                    {
                        this.plugin.reloadConfig();
                        this.plugin.saveConfig();
    
                        Config.load(plugin);
    
                        p.sendMessage(Config.CONFIG_RELOADED);
                    }
                    else
                    {
                        p.sendMessage(Config.NO_PERMISSION);
                    }
                    
                    return true;
                }
            }
            
            if(p.hasPermission("m0onlinegui.see"))
            {
                PlayerListGUI.showPlayers(p);
            }
            else
            {
                p.sendMessage(Config.NO_PERMISSION);
            }
            
            return true;
        }

        return true;
    }
    
    @Override
    public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull Command cmd,
                                      @Nonnull String label, @Nonnull String[] args)
    {
        List<String> completes = new ArrayList<>();
        
        if(args.length == 1)
        {
            completes.add("reload");
        }
        
        return completes;
    }
}
