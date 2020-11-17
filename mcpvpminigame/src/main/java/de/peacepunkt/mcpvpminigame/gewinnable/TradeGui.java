package de.peacepunkt.mcpvpminigame.gewinnable;

import de.peacepunkt.mcpvpminigame.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class TradeGui implements Listener {
    private Inventory inventory;
    Main main;
    Map<Integer, Trade> trades;
    Player player;

    public TradeGui(Main main, Player player) {
        this.main = main;
        main.getServer().getPluginManager().registerEvents(this, main);
        trades = new HashMap<>();
        inventory = Bukkit.createInventory(null, 18, "Your trades: ");
        createTradeInventory(player);
        this.player = player;
    }

    private void createTradeInventory(Player player) {
        int count = 0;
        for(Trade trade : TradeList.getAllTrades()) {
            if(main.hasLevel(player, trade.key)) {
                trades.put(count, trade);
                count++;
            }
        }

        for(Integer i : trades.keySet()) {
            Trade trade = trades.get(i);
            inventory.setItem(i, trade.what);
        }
    }

    public void openInventory() {
        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (!e.getInventory().equals(inventory)) return;
        ItemStack clickedItem = e.getCurrentItem();
        int index = e.getSlot();

        // verify current item is not null
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        Trade trade = trades.get(index);
        if(e.getWhoClicked() instanceof Player) {
            Player player = (Player) e.getWhoClicked();
            if (player.getInventory().contains(trade.price.getType())) {
                player.getInventory().removeItem(trade.price);
                player.getInventory().addItem(trade.what);
            }
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        if(event.getInventory().equals(inventory)) {
            System.out.println("unregistered");
            HandlerList.unregisterAll(this);
        }
    }
}
