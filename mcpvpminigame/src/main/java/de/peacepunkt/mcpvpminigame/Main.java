package de.peacepunkt.mcpvpminigame;

import de.peacepunkt.mcpvpminigame.postiontracker.PositionCommands;
import de.peacepunkt.mcpvpminigame.rounds.RoundHandler;
import de.peacepunkt.mcpvpminigame.teams.Team;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class Main extends JavaPlugin implements Listener {
        static ChatColor serverChatColor = ChatColor.GREEN;
        World lobby;
        RoundHandler handler;
        Map<UUID, PermissionAttachment> permissions;

        @Override
        public void onEnable() {
                lobby = checkLobby();
                System.out.println(lobby);
                permissions = new HashMap<>();

                getServer().getPluginManager().registerEvents(this, this);
                handler = new RoundHandler(this);

                //register all command helper classes here
                new OpCommands(this);
                new PositionCommands(this);
        }

        public RoundHandler getHandler() {
                return handler;
        }
        @Override
        public void onDisable() {

        }
        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {
                Location l = new Location(lobby, lobby.getSpawnLocation().getX(), lobby.getSpawnLocation().getY(), lobby.getSpawnLocation().getZ());
                event.getPlayer().teleport(l);
                if(permissions.containsKey(event.getPlayer().getUniqueId())) {
                        addLeaderPermission(event.getPlayer());
                }
                Team t = handler.getTeamOfPlayer(event.getPlayer());
                if (t != null) {
                        event.getPlayer().sendMessage(serverChatColor + "You're still member of " + t.getDescription());
                } else {
                        event.getPlayer().sendMessage(serverChatColor + "You're in no team yet. Wait for a team leader to invite you or ask an admin to create a team for you.");
                }
        }
        public void addLeaderPermission(Player p) {
                UUID id = p.getUniqueId();
                if(p != null) {
                        PermissionAttachment a = p.addAttachment(this, "leader", true);
                        Team t = handler.getTeamOfPlayer(p);
                        p.sendMessage(ChatColor.GREEN + "You're the team leader of team " + t.getDescription());
                        permissions.put(id, a);
                        //savePermissions();
                } else {
                        permissions.put(id, null);
                }
        }

        private World checkLobby() {
                System.out.println("");
                for(World w : Bukkit.getWorlds()) {
                        System.out.println(w);
                }
                System.out.println("");

                System.out.println("loading lobby");
                World ret = Bukkit.getWorld("lobby");
                if(ret == null) {
                        System.out.println("starting world creation");
                        WorldCreator creator = new WorldCreator("lobby");
                        //creator.type(WorldType.CUSTOMIZED);
                        creator.environment(World.Environment.NORMAL);
                        creator.generateStructures(false);
                        creator.generator(new SpawnWorldGenerator());
                        World w = Bukkit.createWorld(creator);
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
                        System.out.println("World creation successful.");
                        ret = w;
                        System.out.println(ret);

                }
                return ret;
        }
}
