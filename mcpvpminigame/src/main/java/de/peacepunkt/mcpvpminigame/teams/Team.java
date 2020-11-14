package de.peacepunkt.mcpvpminigame.teams;

import java.util.ArrayList;
import java.util.List;

import de.peacepunkt.mcpvpminigame.Main;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class Team {
    OfflinePlayer leader;
    List<OfflinePlayer> mates;
    String name;
    String short_name;
    ChatColor color;

    public Team(String name, String short_name, Player leader, int color) {
        this.name = name;
        this.color = Team.colorById(color);
        this.leader = leader;
        this.short_name = short_name;
        mates = new ArrayList<>();
        changeNickName(leader);
        joinMsg(leader, true);
    }

    private void joinMsg(Player player, boolean leader) {
        player.sendMessage(Main.serverChatColor + "You joined team " + this.getDescription());
        if (leader)
            player.sendMessage(Main.serverChatColor + "oh and you are this teams leader btw!");
    }

    private void changeNickName(Player player) {
        player.setDisplayName(color+player.getName()+ChatColor.RESET);
        player.setPlayerListName(color+player.getName()+ChatColor.RESET);
    }

    private void resetNickName(Player player) {
        player.setDisplayName(ChatColor.RESET + player.getName() + ChatColor.RESET);
        player.setPlayerListName(ChatColor.RESET + player.getName() + ChatColor.RESET);
    }

    public void addPlayer(OfflinePlayer player, boolean leader) {
        if(player.getPlayer() != null) {
            joinMsg(player.getPlayer(), false);
            changeNickName(player.getPlayer());
        }
        if(leader) {
            this.leader = player;
        } else {
            boolean found = false;
            OfflinePlayer remove = null;
            for(OfflinePlayer p: mates) {
                if(p.getUniqueId().equals(player.getUniqueId())) {
                    found = true;
                    remove = p;
                }
            }
            if(found) {
                mates.remove(remove);
            }
            mates.add(player);
        }
    }

    public List<Player> getPlayers() {
        List<Player> online = new ArrayList<Player>();
        for(OfflinePlayer p : mates) {
            if(p.isOnline()) {
                online.add(p.getPlayer());
            }
        }
        if(leader.isOnline()) {
            online.add(leader.getPlayer());
        }
        return online;
    }

    public static ChatColor colorById(int id) {
        switch (id) {
            default:
                return ChatColor.AQUA;
            case 1:
                return ChatColor.GREEN;
            case 2:
                return ChatColor.YELLOW;
            case 3:
                return ChatColor.RED;
            case 4:
                return ChatColor.LIGHT_PURPLE;
            case 5:
                return ChatColor.DARK_AQUA;
            case 6:
                return ChatColor.GRAY;
            case 7:
                return ChatColor.GOLD;
            case 8:
                return ChatColor.DARK_RED;
            case 9:
                return ChatColor.BLUE;

        }
    }

    //returns whether player is in this team or not
    public boolean containsPlayer(Player player) {
        if (player.getUniqueId().equals(leader.getUniqueId())) {
            return true;
        }

        for (OfflinePlayer p : mates) {
            if(p.getUniqueId().equals(player.getUniqueId())) {
                return true;
            }
        }
        return false;
    }

    public ChatColor getColor() {
        return color;
    }

    public String getDescription() {
        return color + name;
    }

    public OfflinePlayer getLeader() {
        return leader;
    }

    public void clear() {
        for (OfflinePlayer p : mates) {
            if(p.getPlayer() != null) {
                resetNickName(p.getPlayer());
            }
        }

        if(leader.getPlayer() != null) {
            resetNickName(leader.getPlayer());
        }
    }

    //sends a message to all team members
    public void sendMessage(Player player, String message) {
        for(OfflinePlayer p : mates) {
            if(p.getPlayer() != null)
                p.getPlayer().sendMessage(Main.serverChatColor+"[TEAM] " + ChatColor.RESET +"<"+player.getDisplayName() +"> " + ChatColor.GRAY + message);
        }
        if(leader.getPlayer() != null) {
            leader.getPlayer().sendMessage(Main.serverChatColor+"[TEAM] " + ChatColor.RESET +"<"+player.getDisplayName() +"> " + ChatColor.GRAY + message);
        }
    }
}
