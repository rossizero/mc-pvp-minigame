package de.peacepunkt.mcpvpminigame.gewinnable;

import de.peacepunkt.mcpvpminigame.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;

public class LevelHandler implements Listener {
    List<Level> levels;
    Main main;

    public LevelHandler(Main main) {
        levels = new ArrayList<Level>();
        main.getServer().getPluginManager().registerEvents(this, main);
        this.main = main;
    }

    /**
     * adds a player to LevelHandler if it does not yet exist
     * @param player
     */
    public void addPlayer(Player player) {
        if(getLevel(player) == null) {
            levels.add(new Level(player));
        }
    }
    /**
     * check if a player already has a certain level
     * @param player
     * @param key
     * @return whether the player has that level or false if player does not yet exist
     */
    public boolean hasLevel(Player player, String key) {
        Level level = getLevel(player);
        if(level != null) {
            return level.containsKey(key);
        }
        return false;
    }

    /**
     * add a new "level" to a player
     * @param player
     * @param key
     * @return true if level was added or already existed, false if player does not yet exist
     */
    public boolean addLevel(Player player, String key) {
        Level level = getLevel(player);
        if(level != null) {
            level.addKey(key);
            return true;
        }
        return false;
    }

    /**
     * returns Level of player if uuids match
     * @param player
     * @return
     */
    private Level getLevel(Player player) {
        for(Level level : levels) {
            if(level.getUuid().equals(player.getUniqueId())) {
                return level;
            }
        }
        return null;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        addPlayer(event.getPlayer());
    }
}
