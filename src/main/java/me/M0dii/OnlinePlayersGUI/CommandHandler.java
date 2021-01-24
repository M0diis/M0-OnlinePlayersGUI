package me.M0dii.OnlinePlayersGUI;

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
    private final OnlineGUI plugin;
    private final Config config;
    
    public CommandHandler(OnlineGUI plugin)
    {
        this.plugin = plugin;
        this.config = plugin.getCfg();
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
                    if(p.hasPermission("m0onlinegui.command.reload"))
                    {
                        this.plugin.reloadConfig();
                        this.plugin.saveConfig();
    
                        this.plugin.getCfg().load(this.plugin);
    
                        p.sendMessage(this.config.CONFIG_RELOAD_MSG());
                    }
                    else
                    {
                        p.sendMessage(this.config.NO_PERMISSION_MSG());
                    }
                    
                    return true;
                }
            }
            
            if(p.hasPermission("m0onlinegui.command.onlinegui"))
            {
                new PlayerListGUI(this.plugin, 0).showPlayers(p);
            }
            else
            {
                p.sendMessage(this.config.NO_PERMISSION_MSG());
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
