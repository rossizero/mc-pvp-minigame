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

        //TODO make team <name> <short_name> <leader: Player> <color?>
        main.getCommand("maketeam").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                if(strings.length == 4) {
                    try {
                        String name = strings[0];
                        String short_name = strings[1];
                        String player_name = strings[2];
                        Player leader = Bukkit.getPlayer(player_name);
                        if (leader == null)
                            return false;
                        int color = Integer.parseInt(strings[3]);
                        main.getHandler().createTeam(name, short_name, leader, color);
                        leader.addAttachment(main, "leader", true);
                    } catch (Exception e){
                        return false;
                    }
                }
                return true;
            }
        });

        //TODO delete team <leader_name>
        main.getCommand("deleteTeam").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                if(strings.length == 1) {
                    try {
                        String player_name = strings[2];
                        Player leader = Bukkit.getPlayer(player_name);
                        if (leader == null)
                            return false;
                        int color = Integer.parseInt(strings[3]);
                        leader.removeAttachment(main.permissions.get(leader.getUniqueId()));
                    } catch (Exception e){
                        return false;
                    }
                }
                return false;
            }
        });
    }
}
