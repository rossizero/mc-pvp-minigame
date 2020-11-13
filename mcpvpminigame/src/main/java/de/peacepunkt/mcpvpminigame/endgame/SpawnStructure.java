package de.peacepunkt.mcpvpminigame.endgame;

import de.peacepunkt.mcpvpminigame.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;


public class SpawnStructure implements Listener {
    Main main;
    Block target; //block on which the egg has to be placed

    public SpawnStructure(Main main) {
        this.main = main;
        this.target = createSpawnStructure();
    }
    @EventHandler
    public void onEntityCombustEvent(EntityCombustEvent event) {
        System.out.println(event.getEntity());
        System.out.println(event.getEntityType());
        if(event.getEntityType().equals(EntityType.DROPPED_ITEM)) {
            Item i = (Item) event.getEntity();
            if(i.getItemStack().getType().equals(Material.DRAGON_EGG)) {
                event.setCancelled(true); //TODO not working
            }
        }
    }
    @EventHandler
    public void onBlockPlacedEvent(BlockPlaceEvent event) {
        if (event.getBlock().getType().equals(Material.DRAGON_EGG)) {
            if(event.getBlock().equals(target)) {
                System.out.println("PLACED ON TARGET");
            }
        }
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
}
