package de.peacepunkt.mcpvpminigame.midgame;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import de.peacepunkt.mcpvpminigame.Main;

public class PlayerTweakListener implements Listener {

    public PlayerTweakListener(Main plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler
    public void onItemMove(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if(item != null && item.getType().equals(Material.DRAGON_EGG)) {
            if(!event.getInventory().getType().equals(InventoryType.CRAFTING)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onHopper(InventoryPickupItemEvent event) {
        if(event.getItem().getItemStack().getType().equals(Material.DRAGON_EGG)) {
            event.setCancelled(true);
        }
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
