package de.peacepunkt.mcpvpminigame.teams;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class Team {
    OfflinePlayer leader;
    List<Player> mates = new ArrayList<Player>();
    String name;
    String short_name;
    ChatColor color;

    public Team(String name, String short_name, Player leader, ChatColor color) {
        this.name = name;
        this.color = color;
        this.leader = leader;
        this.short_name = short_name;
        changeNickName(leader);
    }
    public Team(String name, String short_name, Player leader, int color) {
        this.name = name;
        this.color = Team.colorById(color);
        this.leader = leader;
        this.short_name = short_name;
        changeNickName(leader);
    }

    private void changeNickName(Player player) {
        player.setDisplayName(color+player.getName());
    }

    public void addPlayer(OfflinePlayer player) {
        if(player.getPlayer() != null)
            player.getPlayer().setDisplayName(color+player.getName());
        mates.add(player);
    }
    public List<Player> getPlayers() {
        List<Player> online = new ArrayList<Player>();
        for(Player p : mates) {
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
                return ChatColor.BLUE;
            case 1:
                return ChatColor.GREEN;
            case 2:
                return ChatColor.YELLOW;
            case 3:
                return ChatColor.RED;
            case 4:
                return ChatColor.LIGHT_PURPLE;
            case 5:
                return ChatColor.BLACK;
            case 6:
                return ChatColor.GRAY;
            case 7:
                return ChatColor.GOLD;
            case 8:
                return ChatColor.DARK_RED;
            case 9:
                return ChatColor.AQUA;
        }
    }

    //returns whether player is in this team or not
    public boolean containsPlayer(Player player) {
        if (player.equals(leader.getPlayer())) {
            return true;
        }

        for (OfflinePlayer p : mates) {
            if(p.getPlayer().equals(player)) {
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
}
