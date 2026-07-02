package me.codex.giantitemdisplays.display;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public record BlockLocation(String world, int x, int y, int z) {
    public Location toLocation() {
        World bukkitWorld = Bukkit.getWorld(world);
        if (bukkitWorld == null) {
            return null;
        }
        return new Location(bukkitWorld, x, y, z);
    }

    public int chunkX() {
        return x >> 4;
    }

    public int chunkZ() {
        return z >> 4;
    }
}
