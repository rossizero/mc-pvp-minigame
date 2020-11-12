package de.peacepunkt.mcpvpminigame;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpCommands {
    public OpCommands(Main main) {
        main.getCommand("warp").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
                if (arg0 instanceof Player) {
                    if(arg3.length == 1) {
                        Player p = (Player) arg0;
                        String worldName = arg3[0];
                        boolean done = false;
                        World target = Bukkit.getWorld(worldName);
                        if(target != null) {
                            Location loc = new Location(target, target.getSpawnLocation().getX(),target.getSpawnLocation().getY(), target.getSpawnLocation().getZ());
                            p.teleport(loc);
                            done = true;
                        }
                        if(!done) {
                            p.sendMessage("World " + arg3[0] + " could be unloaded.");
                        }
                        return true;
                    } else {
                        Player p = (Player) arg0;
                        p.sendMessage("/warp <worldName>");
                    }
                }
                return false;
            }
        });

        main.getCommand("start").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                main.getHandler().startRound();
                return true;
            }
        });
    }
}
