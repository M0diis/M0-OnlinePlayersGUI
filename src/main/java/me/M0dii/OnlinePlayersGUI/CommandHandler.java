package me.M0dii.OnlinePlayersGUI;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class CommandHandler implements CommandExecutor
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
        PlayerListGUI.showPlayers((Player)sender);
        
        return true;
    }
}
