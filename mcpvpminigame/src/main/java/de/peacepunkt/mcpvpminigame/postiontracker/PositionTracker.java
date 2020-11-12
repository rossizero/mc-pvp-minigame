package de.peacepunkt.mcpvpminigame.postiontracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import de.peacepunkt.mcpvpminigame.Main;
import de.peacepunkt.mcpvpminigame.rounds.RoundHandler;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class PositionTracker implements Listener {

    private boolean isRunning = false;
    private Main main = null;
    private int secondTimer = 0;
    private int positionLag = 1;
    private int timerIndex = 0;
    private int taskId = 0;
    private ArrayList<HashMap<UUID, Location>> playerlocationsByTime = new ArrayList<HashMap<UUID, Location>>();
    private HashMap<UUID, UUID> selectedTargets = new HashMap<UUID, UUID>();

    public PositionTracker(Main pl, int lag){
        this.main = pl;
        for(int i = 0; i < positionLag; i++) {
            playerlocationsByTime.add(new HashMap<UUID, Location>());
        }
        pl.getServer().getPluginManager().registerEvents(this, pl);
        this.positionLag = lag;
    }

    @EventHandler
    public void onLeftClick(PlayerInteractEvent event) {
        if(event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            if(event.getItem().getType().equals(Material.COMPASS)) {
                Player p = event.getPlayer();
                Player target = getNextTarget(p);
                if(target == null) {
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Es gibt aktuell keinen Spieler, auf den gezeigt werden kann. Vielleicht sind die Teams leer?"));
                }else{
                    if(setTargetOf(p, target)) {
                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Dein Kompass zeigt jetzt auf "+target.getDisplayName()));
                    }else{
                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Position von Spieler "+target.getDisplayName()+" ist unbekannt."));
                        
                    }
                }
            }
        }
    }

    public boolean setTargetOf(Player p, Player target) {
        if(target == null || p == null) {
            return false;
        }
        selectedTargets.put(p.getUniqueId(), target.getUniqueId());
        if(p.isOnline()){
            Location loc = getLastLocation(target);
            if(loc != null) {
                p.setCompassTarget(target.getLocation());
                return true;
            }
        }   
        return false;
     }

    public Player getNextTarget(Player p) {
        UUID uuid = selectedTargets.get(p.getUniqueId());
        List<Player> players = this.main.getHandler().getPlayers(); // Sorted players
        players.remove(p);

        if(players.size() == 0) {
            return null;
        }

        if(uuid == null) {
            return players.get(0);
        }
        
        // Player currently has a target
        List<UUID> uuids = players.stream().map(Player::getUniqueId).collect(Collectors.toList());
        int index = uuids.indexOf(uuid);
        if(++index >= players.size()){
            index = 0;
        }

        return players.get(index);
    }

    public Location getLastLocation(Player p) {
        RoundHandler handler = this.main.getHandler();
        Location loc = null;

        // Live tracking
        if(handler.getTeamOfPlayer(p) == handler.enderTeam) {
            loc = playerlocationsByTime.get(timerIndex).get(p.getUniqueId());
        }else { // Shifted tracking
            loc = playerlocationsByTime.get((timerIndex + 1) % positionLag).get(p.getUniqueId());
        }
        return loc;
    }

    /**
     * Starts the position tracker.
     * @return
     */
    public boolean start() {

        if(!isRunning) {
            // set start time
            timerIndex = 0;
            
            taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.main, new Runnable() {
            
                @Override
                public void run() {
                    List<Player> players = Bukkit.getWorld("world").getPlayers();

                    // now create list
                    HashMap<UUID, Location> newLocations = new HashMap<UUID, Location>();
                    for(Player player : players) {
                        newLocations.put(player.getUniqueId(), player.getLocation());
                    }

                    playerlocationsByTime.set(timerIndex, newLocations);
                    timerIndex = (timerIndex + 1) % positionLag;
                    
                }
    
            }, 0L, 60*20L); // 60 seconds
            isRunning = true;
            return true;
        }
        return false;        
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(taskId);
        isRunning = false;
    }
}
