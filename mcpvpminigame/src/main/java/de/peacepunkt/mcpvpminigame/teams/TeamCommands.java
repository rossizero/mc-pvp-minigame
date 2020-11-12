package de.peacepunkt.mcpvpminigame.teams;

import de.peacepunkt.mcpvpminigame.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamCommands {
    public TeamCommands(Main main) {
        main.getCommand("addplayer").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                if (commandSender instanceof Player) {
                    if (strings.length == 1) {
                        Player sender = (Player) commandSender;
                        String other = strings[0];
                        Player target = Bukkit.getPlayer(other);
                        //stuff
                    }
                }
                return true;
            }
        });

    }
}
