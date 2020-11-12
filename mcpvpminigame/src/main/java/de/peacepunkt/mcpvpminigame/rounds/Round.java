package de.peacepunkt.mcpvpminigame.rounds;


import de.peacepunkt.mcpvpminigame.Main;
import de.peacepunkt.mcpvpminigame.teams.Team;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;


public class Round implements Listener {
    RoundHandler handler;
    private BukkitRunnable noPvpCooldown;
    private int secondsFromPvpCooldown = 0;
    private boolean ispvp = false;
    private boolean running = false;

    public Round(RoundHandler handler) {
        this.handler = handler;
    }

    public void start() {
        tpPlayersIntoWorld("world");
        startSound();
        running = true;
        Bukkit.broadcastMessage(Main.serverChatColor + "pvp disabled for 5 mins from now");
        noPvpCooldown = new BukkitRunnable() {
            @Override
            public void run() {
                noPvpCooldown();
            }
        };
        //runs synchronously!
        noPvpCooldown.runTaskTimer(handler.getMain(), 0, 20);
        //5 mins no pvp
    }
    private void noPvpCooldown() {
        secondsFromPvpCooldown++;
        if (Main.nopvp - secondsFromPvpCooldown <= 10 && Main.nopvp - secondsFromPvpCooldown > 0) {
            Bukkit.broadcastMessage(Main.serverChatColor + "pvp active in " + ChatColor.DARK_RED + (Main.nopvp - secondsFromPvpCooldown) + Main.serverChatColor + " seconds!");
        }
        if (Main.nopvp - secondsFromPvpCooldown <= 0) {
            Bukkit.broadcastMessage(Main.serverChatColor + "pvp enabled (including friendly fire)");
            noPvpCooldown.cancel();
            ispvp = true;
        }
    }
    private void startSound() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendTitle("lets fetz", "", 10, 70, 10);
            p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 7, 0);
        }
    }

    private void tpPlayersIntoWorld(String worldname) {
        World world = Bukkit.getWorld(worldname);
        for(Player p: handler.getPlayers()) {
            p.teleport(new Location(world, world.getSpawnLocation().getX(), world.getSpawnLocation().getY(), world.getSpawnLocation().getZ()));
        }
    }

    @EventHandler
    public void  onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            if (!ispvp)
                e.setCancelled(true);
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void stop() {
        tpPlayersIntoWorld("lobby");
    }
}
