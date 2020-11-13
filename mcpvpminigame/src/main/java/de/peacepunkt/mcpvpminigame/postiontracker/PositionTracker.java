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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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

        // Initialize list
        for(int i = 0; i < lag; i++) {
            playerlocationsByTime.add(new HashMap<UUID, Location>());
        }


        pl.getServer().getPluginManager().registerEvents(this, pl);
        this.positionLag = lag;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogout(PlayerQuitEvent event) {
        Player leaver_player = event.getPlayer();
        UUID leaver  = leaver_player.getUniqueId();
        selectedTargets.forEach((player, target) -> {
            if(target == leaver) {
                Player toMsg = Bukkit.getPlayer(player);
                if(toMsg != null) {
                    toMsg.sendMessage(ChatColor.RED + "Dein Kompass zeigt jetzt auf die letzte bekannte Position von "+leaver_player.getDisplayName());
                }
            }
        });
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {

        Action action = event.getAction();
        // check for left right click with a compass
        if(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            if(event.getItem() != null && event.getItem().getType().equals(Material.COMPASS)) {
                event.setCancelled(true);
                Player p = event.getPlayer();
                Player target = getNextTarget(p);
                if(target == null) {
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Es gibt aktuell keinen Spieler, auf den gezeigt werden kann. Vielleicht sind die Teams leer?"));
                }else{

                    // Try to set target, only works when the lag time has passed once
                    if(setTargetOf(p, target)) {
                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Dein Kompass zeigt jetzt auf "+target.getDisplayName()));
                    }else{
                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Position von Spieler "+target.getDisplayName()+ChatColor.RESET+" ist noch unbekannt."));
                    }
                }
            }
        }
    }

    /**
     * Sets target of a player, this will also influence the players compass.
     * @param p
     * @param target
     * @return
     */
    public boolean setTargetOf(Player p, Player target) {
        if(target == null || p == null) {
            return false;
        }

        selectedTargets.put(p.getUniqueId(), target.getUniqueId());
        if(p.isOnline()){
            Location loc = getLastLocation(target);
            if(loc != null) {
                p.setCompassTarget(loc);
                return true;
            }
        }
        return false;
     }

    /**
     * Updates compass of player
     * @param p to update
     * @return true if successfull
     */
    public boolean updateCompass(Player p) {
        if(p.isOnline()) {
            UUID target = selectedTargets.get(p.getUniqueId());
            Player target_player = Bukkit.getPlayer(target);
            if(target_player != null) {
                Location loc = getLastLocation(target_player);
                if(loc != null) {
                    p.setCompassTarget(loc);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines the next compass target of a player, determined by their current selection.
     * @param p
     * @return
     */
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

    /**
     * Loads location of player, dependant if he is a dragon killer.
     * @param p
     * @return
     */
    public Location getLastLocation(Player p) {
        RoundHandler handler = this.main.getHandler();
        Location loc = null;

        // Live tracking
        if(handler.getTeamOfPlayer(p) == handler.enderTeam) {
            loc = playerlocationsByTime.get(getCurrentTimePoint()).get(p.getUniqueId());
        }else { // Shifted tracking
            loc = playerlocationsByTime.get(getLastTimePoint()).get(p.getUniqueId());
        }
        return loc;
    }

    // called every 60 seconds.
    private void updateTimePoint() {
        timerIndex = (timerIndex + 1) % positionLag;
    }

    // timerIndex -1 is last updated element
    private int getCurrentTimePoint() {
        return (timerIndex - 1) % positionLag;
    }

    // timerIndex always has the value of the latest update made.
    private int getLastTimePoint() {
        return timerIndex;
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

                    // Save at point in time: timerIndex
                    playerlocationsByTime.set(timerIndex, newLocations);

                    // Increment timer once, this timer is only changed here
                    updateTimePoint();

                    // we need to update all the compasses
                    for(Player player : players) {
                        updateCompass(player);
                    }
                }
    
            }, 0L, 2*20L); // 60 seconds
            isRunning = true;
            return true;
        }
        return false;        
    }

    public void stop() {
        if(isRunning) {
            Bukkit.getScheduler().cancelTask(taskId);
            isRunning = false;
        }
    }
}
