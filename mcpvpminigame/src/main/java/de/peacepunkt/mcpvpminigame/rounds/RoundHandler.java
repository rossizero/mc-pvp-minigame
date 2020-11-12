package de.peacepunkt.mcpvpminigame.rounds;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import de.peacepunkt.mcpvpminigame.Main;
import de.peacepunkt.mcpvpminigame.teams.Team;

public class RoundHandler {
    private Round round;
    private Main main;
    private List<Team> teams;
    public Team enderTeam = null;

    public RoundHandler(Main main) {
        teams = new ArrayList<Team>();
        round = new Round();
        this.main = main;
        
    }

    public void startRound() {
        // get all people from lobby that are in teams
        // give round instance it the people and their teams
        // and say  and round.start oder so
    }
    public Team createTeam(String name, String short_name, Player leader, int color) {
        Team t = new Team(name, short_name, leader, color);
        teams.add(t);
        return t;
    }
    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<Player>();
        for(Team t : teams) {
            List<Player> n = t.getPlayers();
            players.addAll(n);
        }
        return players;
    }
    public Team getTeamOfPlayer(Player player) {
        for(Team t : teams) {
            if (t.containsPlayer(player)) {
                return t;
            }
        }
        return null;
    }

    // returns the team of player if player is this teams leader
    // null for every other case
    public Team getTeamOfLeader(Player player) {
        for(Team t : teams) {
            if (t.getLeader().getPlayer().equals(player)) {
                return t;
            }
        }
        return null;
    }

    public boolean isColorFree(ChatColor color) {
        for(Team t : teams) {
            if(color.equals(t.getColor())) {
                return false;
            }
        }
        return true;
    }
}
