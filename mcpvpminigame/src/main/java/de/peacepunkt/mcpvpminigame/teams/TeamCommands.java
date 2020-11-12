package de.peacepunkt.mcpvpminigame.teams;

import de.peacepunkt.mcpvpminigame.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class TeamCommands {
    private Map<Player, Player> pendingInviteRequests;

    public TeamCommands(Main main) {
        pendingInviteRequests = new HashMap<>();

        main.getCommand("invite").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                if (commandSender instanceof Player) {
                    Player sender = (Player) commandSender;
                    Team t = main.getHandler().getTeamOfLeader(sender);
                    if (t == null) {
                        sender.sendMessage(Main.serverChatColor + "You can't do that");
                    } else {
                        if (strings.length == 1) {
                            String targetName = strings[0];
                            Player target = Bukkit.getPlayer(targetName);

                            if (target != null) {
                                if (!target.equals(sender)) {
                                    if (pendingInviteRequests.containsValue(target)) {
                                        sender.sendMessage(Main.serverChatColor + "This player has already been invited by someone else. Tell him to deny the invite and accept yours!.");
                                        target.sendMessage(Main.serverChatColor + target.getDisplayName() + " also wants to recruit you. Type /deny");
                                        return true;
                                    } else {
                                        sender.sendMessage(Main.serverChatColor + "Invitation successfully send to  " + targetName);
                                        target.sendMessage(Main.serverChatColor + sender.getDisplayName() + Main.serverChatColor + " wants to invite you to his team. If this is  also what you want type /accept otherwise just ignore me...");
                                        pendingInviteRequests.put(sender, target);
                                        return true;
                                    }
                                } else {
                                    sender.sendMessage(Main.serverChatColor + "I am not mad at you, I am just disappointed...");
                                    return true;
                                }
                            } else {
                                sender.sendMessage(Main.serverChatColor + "This could be a typo or your recruit is offline");
                                return true;
                            }

                        } else {
                            return false;
                        }
                    }
                    return false;
                }
                return false;
            }
        });

        main.getCommand("accept").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                if(commandSender instanceof Player) {
                    Player target = (Player) commandSender;
                    for(Player p : pendingInviteRequests.keySet()) {
                        if(pendingInviteRequests.get(p).equals(target)) {
                            Team t = main.getHandler().getTeamOfPlayer(target);
                            if(t == null) {
                                pendingInviteRequests.remove(p);
                                target.sendMessage(Main.serverChatColor + "accepted invitation by " + p.getDisplayName());
                                Team in = main.getHandler().getTeamOfLeader(p);
                                in.addPlayer(target, false);
                                if(main.getHandler().getRound().isRunning()) {
                                    main.getHandler().tpPlayerIntoGame(target);
                                }
                                p.sendMessage(Main.serverChatColor + target.getDisplayName() + " is now in your team!");

                            } else {
                                target.sendMessage(Main.serverChatColor + "What the * are you doing? You already chose a team!");
                            }
                        }
                        return true;
                    }
                    target.sendMessage(Main.serverChatColor + "There was no pending invitation for you...");
                }
                return false;
            }
        });

        main.getCommand("deny").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                if(commandSender instanceof Player) {
                    Player target = (Player) commandSender;
                    for(Player p : pendingInviteRequests.keySet()) {
                        if(pendingInviteRequests.get(p).equals(target)) {
                            pendingInviteRequests.remove(p);
                            target.sendMessage(Main.serverChatColor + "denied recruitment...");
                            p.sendMessage(Main.serverChatColor + target.getDisplayName() +" denied your recruitment. Shame on you!");
                            return true;
                        }
                    }
                    target.sendMessage(Main.serverChatColor + "There was no pending invitation for you...");
                }
                return true;
            }
        });

        main.getCommand("a").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                if(commandSender instanceof Player) {
                    Player sender = (Player) commandSender;
                    StringBuilder msg = new StringBuilder();
                    for(String str : strings) {
                        msg.append(str).append(" ");
                    }
                    Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE+"[GLOBAL] " + ChatColor.RESET +"<"+sender.getDisplayName() +"> " + msg.toString());
                }
                return true;
            }
        });
    }
}
