package de.peacepunkt.mcpvpminigame.midgame;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import de.peacepunkt.mcpvpminigame.Main;

public class PlayerLeaveListener implements Listener {

    public PlayerLeaveListener(Main plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        if(p.getInventory().contains(Material.DRAGON_EGG)) {
            // Remove egg & place egg
            p.getInventory().remove(Material.DRAGON_EGG);
            p.getWorld().getBlockAt(p.getLocation()).setType(Material.DRAGON_EGG);
            
            Location loc = p.getLocation();
            Bukkit.broadcastMessage("Egg was dropped at "+loc.getBlockX()+" "+loc.getBlockY()+" "+loc.getBlockZ());
        }
    }
}
