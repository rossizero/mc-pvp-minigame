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
    Map<Player, Player> pendingInviteRequests;
    public TeamCommands(Main main) {
        pendingInviteRequests = new HashMap<>();
        main.getCommand("invite").setExecutor(new CommandExecutor() {
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

        /*main.getCommand("invite").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                if (commandSender instanceof Player) {
                    Player sender = (Player) commandSender;
                    /*main.getHandler().
                    if (strings.length == 1) {
                        String targetName = strings[0];
                        Player target = Bukkit.getPlayer(targetName);

                        if (target != null) {
                            if(!target.equals(sender)) {
                                if (pendingInviteRequests.containsValue(target)) {
                                    sender.sendMessage(ChatColor.GREEN + "This player has already been requested by someone else. Tell him to deny the recruitment request and accept yours!.");
                                    target.sendMessage(ChatColor.GREEN + target.getName() + " also wants to recruit you. Type /denyrecruitment");
                                    return true;
                                } else {
                                    sender.sendMessage(ChatColor.GREEN + "I'll just let " + targetName + " validate this request real quick...");
                                    target.sendMessage(ChatColor.GREEN + sender.getName() + " claims to have recruited you. If this is true type /acceptrecruitment otherwise just ignore me...");
                                    pendingInviteRequests.put(sender, target);
                                    return true;
                                }
                            } else {
                                sender.sendMessage(ChatColor.GREEN + "I am not mad at you, I am just disappointed...");
                                return true;
                            }
                        } else {
                            sender.sendMessage(ChatColor.GREEN +"Your recruit has to be on this server. Just tell them to get online real quick!");
                            return true;
                        }

                    } else {
                        return false;
                    }
                }
                return false;
            }
        });*/

    }
}
