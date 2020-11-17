package de.peacepunkt.mcpvpminigame.gewinnable;


import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Level {
    UUID uuid;
    List<String> keys;
    public static String netherKey = "nether";

    public Level(Player player) {
        this.uuid = player.getUniqueId();
        keys = new ArrayList<String>();
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean containsKey(String key) {
        return keys.contains(key);
    }

    public void addKey(String key) {
        keys.add(key);
    }
}
