package de.peacepunkt.mcpvpminigame.gewinnable;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TradeList {
    public static List<Trade> getAllTrades()
    {
        List<Trade> trades = new ArrayList<>();
        trades.add(new Trade(new ItemStack(Material.BLAZE_ROD), new ItemStack(Material.DIAMOND, 2), Level.netherKey));
        return trades;
    }
}
