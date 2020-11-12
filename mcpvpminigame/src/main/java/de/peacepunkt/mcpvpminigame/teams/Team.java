package de.peacepunkt.mcpvpminigame.teams;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class Team {
    Player leader;
    List<OfflinePlayer> mates;
    String name;
    ChatColor color;

    public void Team(String name, Player leader, ChatColor color) {
        this.name = name;
        this.color = color;
        this.leader = leader;
    }

    public void addPlayer(OfflinePlayer player) {

    }


    //returns whether player is in this team or not
    public boolean containsPlayer(Player player) {
        if (player.equals(leader)) {
            return true;
        }

        for (OfflinePlayer p : mates) {
            if(p.getPlayer().equals(player)) {
                return true;
            }
        }
        return false;
    }
}
