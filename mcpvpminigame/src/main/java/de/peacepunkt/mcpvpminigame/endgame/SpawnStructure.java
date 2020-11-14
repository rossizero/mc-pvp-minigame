package de.peacepunkt.mcpvpminigame.endgame;

import de.peacepunkt.mcpvpminigame.Main;
import de.peacepunkt.mcpvpminigame.teams.Team;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;


public class SpawnStructure implements Listener {
    Main main;
    Block target; //block on which the egg has to be placed
    private BukkitRunnable timer;
    int counter = 0;

    private Team old_team = null;
    private Team current_team = null;

    BossBar bossBar;
    int final_countdown = 7 * 60;

    boolean first = true;
    boolean abgebaut = false;

    public SpawnStructure(Main main) {
        this.main = main;
        this.target = createSpawnStructure();
        timer = new BukkitRunnable() {
            @Override
            public void run() {
                update_function();
            }
        };
    }

    @EventHandler
    public void onEntityCombustEvent(EntityCombustEvent event) {
        if(event.getEntityType().equals(EntityType.DROPPED_ITEM)) {
            Item i = (Item) event.getEntity();
            if(i.getItemStack().getType().equals(Material.DRAGON_EGG)) {
                event.setCancelled(true); //TODO not working
            }
        }
    }
    @EventHandler
    public void onBlockPlacedEvent(BlockPlaceEvent event) {
        if (event.getBlock().getLocation().equals(target.getLocation())) {
            System.out.println(event.getBlock());
            if (event.getBlock().getType().equals(Material.DRAGON_EGG)) {
                for(Player p: Bukkit.getOnlinePlayers()) {
                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 5, 0);
                }
                Material old = event.getBlock().getType();
                event.getBlock().setType(Material.END_GATEWAY);
                Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> event.getBlock().setType(old), 100);
                Team t = main.getHandler().getTeamOfPlayer(event.getPlayer());
                if(t != null) {
                    Bukkit.broadcastMessage(t.getDescription() + Main.serverChatColor + " placed the egg!");
                    abgebaut = false;
                    changeCountdownTeam(t);
                } else {
                    Bukkit.broadcastMessage(Main.serverChatColor + "A rando placed the egg. This should never happen. Wtf guys.");
                }
                System.out.println("PLACED ON TARGET");
                //TODO prevent everything else block types
            } else {
                event.setCancelled(true);
            }
        }
    }
    private void changeCountdownTeam(Team t) {
        if(first) {
            current_team = t;
            bossBar = Bukkit.createBossBar( "", BarColor.PINK, BarStyle.SOLID);
            for(Player p: Bukkit.getOnlinePlayers()) {
                bossBar.addPlayer(p);
            }
            timer.runTaskTimer(main, 0, 20);
            first = false;
            reseted = true;
            World w = Bukkit.getWorld("world");
            w.setThundering(true);
            w.setStorm(true);
            w.setWeatherDuration(99999999); 


        } else {
            if(current_team.equals(t)) {
                reseted = true;
            } else {
                if(old_team == null) {
                    old_team = current_team;
                    current_team = t;
                    reseted = false;
                } else {
                    if (old_team.equals(t)) {
                        reseted = true;
                    } else {
                        reseted = false;
                        old_team = current_team;
                    }
                    current_team = t;
                }
            }
        }
    }
    boolean reseted = false;
    private void update_function() {
        if(!abgebaut) {
            if(!reseted) {
                counter--;
                if(counter <= 0) {
                    counter = 0;
                    old_team = null;
                    reseted = true;
                }
            } else {
                counter++;
            }
        } else {
            counter--;
            if(counter <= 0) {
                counter = 0;
                old_team = null;
                reseted = true;
            }
        }
        int percentage = (int) (((float)counter / (float)final_countdown) * 100);
        bossBar.setProgress((float)counter/(float)final_countdown);

        if(!reseted) {
            bossBar.setTitle("Progress: of Team " + old_team.getDescription() + " " + ChatColor.RESET + percentage + "%");
        } else {
            bossBar.setTitle("Progress: of Team " + current_team.getDescription() + " " + ChatColor.RESET + percentage + "%");
        }
        if(current_team == null) {
            bossBar.setTitle("Progress: " + percentage + "%");
        }

        if(counter >= final_countdown) {
            end();
            timer.cancel();
        }

    }

    private void end() {
        main.waldtraud(current_team);

        // remove bossbar
        bossBar.removeAll();
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if(event.getClickedBlock() != null) {
            if (event.getClickedBlock().getType().equals(Material.DRAGON_EGG)) {
                if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                    event.setCancelled(true);
                } else if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                    event.setCancelled(true);
                    event.getClickedBlock().setType(Material.AIR);
                    ItemStack item = new ItemStack(Material.DRAGON_EGG);
                    event.getPlayer().getWorld().dropItem(event.getClickedBlock().getLocation(), item);
                    if(event.getClickedBlock().getLocation().equals(target.getLocation())) {
                        for(Player p: Bukkit.getOnlinePlayers()) {
                            p.playSound(p.getLocation(), Sound.ITEM_SHIELD_BREAK, 5, 0);
                        }
                        abgebaut = true;
                    }
                }
            }
        }
    }

    private Block createSpawnStructure() {
        World w = Bukkit.getWorld("world");
        if (w != null) {
            Block b = null;
            int i = 0;
            //get first point
            while (b == null) {
                try {
                    Block bb = w.getHighestBlockAt(w.getSpawnLocation().getBlockX() + i, w.getSpawnLocation().getBlockZ());
                    boolean leaves = (bb.getType().equals(Material.ACACIA_LEAVES) || bb.getType().equals(Material.BIRCH_LEAVES) || bb.getType().equals(Material.DARK_OAK_LEAVES) ||
                            bb.getType().equals(Material.JUNGLE_LEAVES) || bb.getType().equals(Material.OAK_LEAVES) || bb.getType().equals(Material.SPRUCE_LEAVES));

                    if(!leaves) {
                        b = bb;
                        break;
                    }
                    i++;

                } catch (NullPointerException ignored) {
                    i++;
                }
            }
            //check in a radius of 10 * 10 to cancel out trees. If bedrock the spawn platform most possibly has already been generated
            if(!b.getType().equals(Material.BEDROCK)) {
                int count = 1;
                int sum_of_ys = b.getY();
                for (int j = -10; j < 10; j++) {
                    for (int k = -10; k < 10; k++) {
                        try {
                            Block bb = w.getHighestBlockAt(w.getSpawnLocation().getBlockX() + i, w.getSpawnLocation().getBlockZ());
                            boolean leaves = (bb.getType().equals(Material.ACACIA_LEAVES) || bb.getType().equals(Material.BIRCH_LEAVES) || bb.getType().equals(Material.DARK_OAK_LEAVES) ||
                                    bb.getType().equals(Material.JUNGLE_LEAVES) || bb.getType().equals(Material.OAK_LEAVES) || bb.getType().equals(Material.SPRUCE_LEAVES));

                            if (!leaves) {
                                sum_of_ys += bb.getY();
                                count++;
                            }
                        } catch (NullPointerException ignored) {

                        }
                    }
                }
                b = w.getBlockAt(w.getSpawnLocation().getBlockX() + i, (sum_of_ys / count), w.getSpawnLocation().getBlockZ());
                w.setSpawnLocation(b.getLocation().add(0, 0, 0));
            } else {
                //b = spawnlocation + 7 = old b -7
                b = b.getLocation().subtract(0, 7, 0).getBlock();
            }

            int rSquared = Main.radius * Main.radius;
            for (int x = b.getX()-Main.radius; x <= b.getX()+ Main.radius; x++) {
                for (int z = b.getZ()-Main.radius; z <= b.getZ() + Main.radius; z++) {
                    if ((b.getX()-x) * (b.getX()-x) + (b.getZ()-z) * (b.getZ()-z) <= rSquared) {
                        for(int y = b.getY()-2; y < b.getY()+1; y ++) {
                            w.getBlockAt(x, y, z).setType(Material.BEDROCK);
                        }
                        for(int y = b.getY()+1; y < 255; y ++) {
                            w.getBlockAt(x, y, z).setType(Material.AIR);
                        }
                    }
                }
            }
            Block ret = b;
            for (int y = b.getY(); y <= b.getY() + 7; y++) {
                ret = w.getBlockAt(b.getX(), y, b.getZ());
                ret.setType(Material.BEDROCK);
            }
            ret = ret.getLocation().add(0, 1, 0).getBlock();

            for(Entity current : w.getEntities()) {
                if (current instanceof Item) {
                    current.remove();
                }
            }
            return ret;
        } else {
            System.out.println("there is no world named world and that is pretty shitty");
            return null;
        }
    }

    public void addPlayerToBar(Player p) {
        if(bossBar != null) {
            bossBar.addPlayer(p);
        }
    }
}
