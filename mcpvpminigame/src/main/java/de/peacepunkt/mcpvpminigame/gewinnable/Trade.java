package de.peacepunkt.mcpvpminigame.gewinnable;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Trade {
    ItemStack what;
    ItemStack price;
    String key;

    public Trade(ItemStack what, ItemStack price, String key) {
        this.what = what;
        this.price = price;
        this.key = key;
        ItemMeta meta = what.getItemMeta();
        List<String> list = new ArrayList<>();
        String pri = "for: " + price.getAmount() + " " + price.getType().toString();
        if(price.getAmount() > 2) {
            pri +="s";
        }
        list.add(pri);
        meta.setLore(list);
        what.setItemMeta(meta);
        price.getAmount();
    }
}
