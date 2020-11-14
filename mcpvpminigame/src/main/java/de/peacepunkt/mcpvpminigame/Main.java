package de.peacepunkt.mcpvpminigame;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;

import de.peacepunkt.mcpvpminigame.endgame.SpawnStructure;
import de.peacepunkt.mcpvpminigame.midgame.DragonKillListener;
import de.peacepunkt.mcpvpminigame.postiontracker.EndPortalTracker;
import de.peacepunkt.mcpvpminigame.postiontracker.PositionCommands;
import de.peacepunkt.mcpvpminigame.rounds.RoundHandler;
import de.peacepunkt.mcpvpminigame.teams.Team;
import de.peacepunkt.mcpvpminigame.teams.TeamCommands;


public class Main extends JavaPlugin implements Listener {
        public static ChatColor serverChatColor = ChatColor.GREEN;
        public static int nopvp = 15; //secs
        public static int radius = 15;

        World lobby;
        RoundHandler handler;
        Map<UUID, PermissionAttachment> permissions;
        SpawnStructure spawnStructure;

        @Override
        public void onEnable() {
                permissions = new HashMap<>();

                //loads or creates the lobby world
                lobby = checkLobby();

                //registers own events
                getServer().getPluginManager().registerEvents(this, this);

                //initialising our fancy ass RoundHandler
                handler = new RoundHandler(this);

                //register all command helper classes here
                new TeamCommands(this);
                new OpCommands(this);
                new PositionCommands(this);

                // Event Listeners
                new EndPortalTracker(this);
                new DragonKillListener(this);

                spawnStructure = new SpawnStructure(this);
                getServer().getPluginManager().registerEvents(spawnStructure, this);
        }

        @Override
        public void onDisable() {

        }

        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {
                //check if player had a team and if he was the leader of that team
                Team t = handler.getTeamOfPlayer(event.getPlayer());
                boolean isLeader = permissions.containsKey(event.getPlayer().getUniqueId());
                if (t != null) {
                        if(isLeader) {
                                addLeaderPermission(event.getPlayer());
                        }
                        t.addPlayer(Bukkit.getOfflinePlayer(event.getPlayer().getUniqueId()), isLeader);
                        if(event.getPlayer().getWorld().getName().equals("lobby") && handler.getRound().isRunning()) {
                                handler.tpPlayerIntoGame(event.getPlayer());
                        }
                } else {
                        //tp to lobby
                        clearInventory(event.getPlayer());
                        Location l = new Location(lobby, lobby.getSpawnLocation().getX(), lobby.getSpawnLocation().getY(), lobby.getSpawnLocation().getZ());
                        event.getPlayer().teleport(l);
                        event.getPlayer().sendMessage(serverChatColor + "You're in no team yet. Wait for a team leader to invite you or ask an admin to create a team for you.");
                }
        }

        public void clearInventory(Player player) {
                player.getInventory().clear();
                player.getEnderChest().clear();
                player.setHealth(20);
                player.setExp(0);
        }
        public RoundHandler getHandler() {
                return handler;
        }

        private void addLeaderPermission(Player p) {
                UUID id = p.getUniqueId();
                PermissionAttachment a = p.addAttachment(this, "leader", true);
                Team t = handler.getTeamOfLeader(p);
                if (t != null) {
                        p.sendMessage(ChatColor.GREEN + "You're the team leader of team " + t.getDescription());
                        permissions.put(id, a);
                }
        }

        //loads or creates the lobby world
        private World checkLobby() {
                for(World w : Bukkit.getWorlds()) {
                        System.out.println(w);
                }
                World ret = Bukkit.getWorld("lobby");
                if(ret == null) {
                        WorldCreator creator = new WorldCreator("lobby");
                        //creator.type(WorldType.CUSTOMIZED);
                        creator.environment(World.Environment.NORMAL);
                        creator.generateStructures(false);
                        creator.generator(new SpawnWorldGenerator());
                        World w = Bukkit.createWorld(creator);
                        if (w != null) {
                                w.setGameRule(GameRule.DO_FIRE_TICK, false);
                                w.setGameRule(GameRule.DO_MOB_SPAWNING, false);
                                w.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
                                w.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
                                w.setGameRule(GameRule.MOB_GRIEFING, false);
                                w.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
                                w.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
                                w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                                w.setSpawnLocation(0, 151, 0);
                                w.setTime(1000);
                        }
                        ret = w;

                }
                return ret;
        }

        @EventHandler
        public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event){
                //if player has a team, automatically send the message to team members only
                Team t = handler.getTeamOfPlayer(event.getPlayer());
                if (t != null) {
                        t.sendMessage(event.getPlayer(), event.getMessage());
                        event.setCancelled(true);
                }
        }

        @EventHandler
        public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
                if(event.getPlayer().getWorld().getName().equals("lobby")) {
                        event.setRespawnLocation(new Location(lobby, lobby.getSpawnLocation().getX(), lobby.getSpawnLocation().getY(), lobby.getSpawnLocation().getZ()));
                }
        }


}
