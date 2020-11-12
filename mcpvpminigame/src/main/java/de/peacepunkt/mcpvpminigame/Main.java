package de.peacepunkt.mcpvpminigame;

import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class Main extends JavaPlugin implements Listener {
        World lobby;

        @Override
        public void onEnable() {
            lobby = checkLobby();
            System.out.println(lobby);
            getServer().getPluginManager().registerEvents(this, this);
            new OpCommands(this);
        }

        @Override
        public void onDisable() {

        }
        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {
                event.getPlayer().sendMessage("welcome");
                Location l = new Location(lobby, lobby.getSpawnLocation().getX(), lobby.getSpawnLocation().getY(), lobby.getSpawnLocation().getZ());
                System.out.println(l);
                event.getPlayer().teleport(l);
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
