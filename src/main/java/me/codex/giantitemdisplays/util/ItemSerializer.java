package me.codex.giantitemdisplays.util;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public final class ItemSerializer {
    private ItemSerializer() {
    }

    public static void write(ConfigurationSection section, String path, ItemStack item) {
        section.set(path, item == null ? new ItemStack(Material.STONE) : item.clone());
    }

    public static ItemStack read(ConfigurationSection section, String path) {
        ItemStack item = section.getItemStack(path);
        if (item == null || item.getType().isAir()) {
            return new ItemStack(Material.STONE);
        }
        return item.clone();
    }
}
