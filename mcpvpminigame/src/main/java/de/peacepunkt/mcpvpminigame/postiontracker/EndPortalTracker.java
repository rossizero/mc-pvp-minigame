package de.peacepunkt.mcpvpminigame.postiontracker;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerPortalEvent;

import de.peacepunkt.mcpvpminigame.Main;

public class EndPortalTracker implements Listener {

    private HashMap<UUID, Location> sources = new HashMap<UUID, Location>();
    private Random random = new Random();
    public EndPortalTracker(Main plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onChangedWorld(PlayerChangedWorldEvent event) {
        if(event.getFrom().getEnvironment().equals(Environment.THE_END)){
            Location saved = sources.get(event.getPlayer().getUniqueId());

            // add randomness, so one cannot be trapped inside the portal
            saved = saved.add(random.nextInt(30)-15, 0, random.nextInt(31)-15);
            if(saved != null) {
                event.getPlayer().teleport(event.getPlayer().getWorld().getHighestBlockAt(saved).getLocation().clone().add(0.5d, 1, 0.5d));
            }
        }
    }

    @EventHandler
    public void onEnterEndWorld(PlayerPortalEvent event) {
        World from = event.getFrom().getWorld();
        World to = event.getTo().getWorld();
        
        if(from.getEnvironment() == Environment.NORMAL) {
            if(to.getEnvironment() == Environment.THE_END) {
                sources.put(event.getPlayer().getUniqueId(), event.getPlayer().getLocation());

                Bukkit.broadcastMessage(event.getPlayer().getDisplayName()+" entered the end!");
            }
        } 
            
        
    }
}
