package de.peacepunkt.mcpvpminigame;

import de.peacepunkt.mcpvpminigame.teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

class OpCommands {
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
                if(!main.getHandler().getRound().isRunning()) {
                    main.getHandler().startRound();
                } else {
                    Player p = (Player) commandSender;
                    p.sendMessage(Main.serverChatColor + "Already running....");
                }
                return true;
            }
        });

        main.getCommand("stop").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                if(main.getHandler().getRound().isRunning()) {
                    main.getHandler().stopRound();
                } else {
                    Player p = (Player) commandSender;
                    p.sendMessage(Main.serverChatColor + "Nothing running....");
                }
                return true;
            }
        });

        main.getCommand("maketeam").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                if (commandSender instanceof Player) {
                    if (strings.length == 4) {
                        try {
                            String name = strings[0];
                            String short_name = strings[1];
                            String player_name = strings[2];
                            Player leader = Bukkit.getPlayer(player_name);
                            Player sender = (Player) commandSender;
                            if (leader == null)
                                return false;
                            Team t = main.getHandler().getTeamOfPlayer(leader);
                            if (t == null) {
                                int color = Integer.parseInt(strings[3]);
                                main.getHandler().createTeam(name, short_name, leader, color);
                                PermissionAttachment a = leader.addAttachment(main, "leader", true);
                                main.permissions.put(leader.getUniqueId(), a);
                            } else {
                                sender.sendMessage(Main.serverChatColor + "this player already has a team");
                            }
                        } catch (Exception e) {
                            return false;
                        }
                    }
                    return true;
                }
                return false;
            }
        });

        main.getCommand("deleteTeam").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                if(strings.length == 1) {
                    try {
                        String player_name = strings[0];
                        Player leader = Bukkit.getPlayer(player_name);
                        if (leader == null) {
                            if (commandSender instanceof Player) {
                                Player p = (Player) commandSender;
                                p.sendMessage(Main.serverChatColor + "This guy is not a team leader");
                            }
                            return false;
                        }
                        Team t = main.getHandler().getTeamOfLeader(leader);
                        if (t != null) {
                            leader.removeAttachment(main.permissions.get(leader.getUniqueId()));
                            leader.sendMessage(Main.serverChatColor + "Your team has been deleted, sorry");
                            main.getHandler().removeTeam(t);
                        }

                    } catch (Exception e){
                        return false;
                    }
                }
                return false;
            }
        });
    }
}
