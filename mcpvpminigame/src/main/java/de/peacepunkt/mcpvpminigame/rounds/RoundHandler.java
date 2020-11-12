package de.peacepunkt.mcpvpminigame.rounds;

import de.peacepunkt.mcpvpminigame.Main;
import de.peacepunkt.mcpvpminigame.teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RoundHandler {
    private Round round;
    private Main main;
    private List<Team> teams;

    public RoundHandler(Main main) {
        teams = new ArrayList<Team>();
        round = new Round(this);
        main.getServer().getPluginManager().registerEvents(round, main);
        this.main = main;
    }

    public void startRound() {
        round.start();
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<Player>();
        for(Team t : teams) {
            players.addAll(t.getPlayers());
        }
        return players;
    }

    public Team createTeam(String name, String short_name, Player leader, int color) {
        Team t = new Team(name, short_name, leader, color);
        teams.add(t);
        return t;
    }

    public void removeTeam(Team t) {
        t.clear();
        teams.remove(t);
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

        teams = new ArrayList<Team>();
        round = new Round(this);

    }
}
