package de.peacepunkt.mcpvpminigame.rounds;

import java.util.ArrayList;
import java.util.List;

import de.peacepunkt.mcpvpminigame.postiontracker.PositionTracker;
import org.bukkit.*;
import org.bukkit.entity.Player;

import de.peacepunkt.mcpvpminigame.Main;
import de.peacepunkt.mcpvpminigame.teams.Team;

public class RoundHandler {
    private Round round;
    private Main main;
    private List<Team> teams;
    public Team enderTeam = null;
    private PositionTracker positionTracker;


    public RoundHandler(Main main) {
        teams = new ArrayList<Team>();
        round = new Round(this);
        main.getServer().getPluginManager().registerEvents(round, main);
        this.main = main;
        positionTracker = new PositionTracker(main, 2);
    }

    public void startRound() {
        round.start();
        positionTracker.start();
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<Player>();
        for(Team t : teams) {
            players.addAll(t.getPlayers());
        }
        return players;
    }

    public Team createTeam(String name, String short_name, Player leader) {
        int color = getFreeColor();
        if(color != -1) {
            Team t = new Team(name, short_name, leader, color);
            teams.add(t);
            return t;
        } else {
            return null;
        }
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
            if (t.getLeader().getUniqueId().equals(player.getUniqueId())) {
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

    public Main getMain() {
        return main;
    }
    public Round getRound() {
        return round;
    }

    public void tpPlayerIntoGame(Player p) {
        World world = Bukkit.getWorld("world");
        p.teleport(new Location(world, world.getSpawnLocation().getX(), world.getSpawnLocation().getY(), world.getSpawnLocation().getZ()));
    }

    public void stopRound() {
        round.stop();
        for(Player player: Bukkit.getOnlinePlayers()) {
            main.clearInventory(player);
        }
        for(Team t: teams) {
            removeTeam(t);
        }
        teams = new ArrayList<Team>();
        round = new Round(this);

    }

    private int getFreeColor() {
        for(int i = 0; i < 10; i++) {
            boolean found = false;
            for(Team t : teams) {
                if(t.getColor().equals(Team.colorById(i))) {
                    found = true;
                }
            }
            if(!found) {
                return i;
            }
        }
        return -1;
    }
}
