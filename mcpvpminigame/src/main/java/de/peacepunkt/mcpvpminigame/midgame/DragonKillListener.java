package de.peacepunkt.mcpvpminigame.midgame;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import de.peacepunkt.mcpvpminigame.Main;
import de.peacepunkt.mcpvpminigame.teams.Team;

public class DragonKillListener implements Listener {
    
    private Main plugin = null;
    private Player lastSlayer = null;
    public DragonKillListener(Main plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void onDragonHit(EntityDamageByEntityEvent event) {
        if(event.getEntity().getType().equals(EntityType.ENDER_DRAGON)) {
            if(event.getDamager() instanceof Player) {
                lastSlayer = (Player) event.getDamager();
            }
        }

    }

    @EventHandler
    public void onDragonKill(EntityDeathEvent event) {
        if(event.getEntity().getType().equals(EntityType.ENDER_DRAGON)) {
            Player player = null;

            // This should not happen usually
            if(event.getEntity().getKiller() == null) {
                player = this.lastSlayer;
            } else {
                player = event.getEntity().getKiller();
            }

            Team t = this.plugin.getHandler().getTeamOfPlayer(player);
            if(t == null) {
                // Some random guy killed the dragon
                player.sendMessage("Hey, you don't even have a team, what are you doing????");
                // do nothing
            }else {
                this.plugin.getHandler().makeEnderTeam(t);
                String time_formatted = getFormattedTime(this.plugin.getHandler().getRunningTime());
                Bukkit.broadcastMessage("The dragon was slayn by Team "+t.getDescription()+ChatColor.RESET+" after "+time_formatted+". The killers are:");
                for(Player p : t.getPlayers()) {
                    Bukkit.broadcastMessage(p.getDisplayName());
                }
                Bukkit.broadcastMessage("Now bring the egg back to spawn!");
            }
            
        }
    }

    private String getFormattedTime(long runningTime) {
        // returns HH:MM:SS
        long seconds = runningTime / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

}
