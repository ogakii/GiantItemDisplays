package me.codex.giantitemdisplays.display;

import me.codex.giantitemdisplays.GiantItemDisplaysPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public final class CollisionManager {
    private final GiantItemDisplaysPlugin plugin;

    public CollisionManager(GiantItemDisplaysPlugin plugin) {
        this.plugin = plugin;
    }

    public void applyBarriers(DisplayData data) {
        clearBarriers(data, true);
        if (data.collisionMode() != CollisionMode.BARRIER) {
            return;
        }

        Location location = data.toLocation();
        if (location == null) {
            return;
        }
        World world = location.getWorld();
        int maxBlocks = Math.max(1, plugin.getConfig().getInt("settings.barrier-max-blocks", 512));
        int minX = (int) Math.floor(location.getX() - data.hitboxWidth() / 2.0D);
        int maxX = (int) Math.floor(location.getX() + data.hitboxWidth() / 2.0D);
        int minZ = (int) Math.floor(location.getZ() - data.hitboxWidth() / 2.0D);
        int maxZ = (int) Math.floor(location.getZ() + data.hitboxWidth() / 2.0D);
        int minY = (int) Math.floor(location.getY());
        int maxY = minY + Math.max(1, (int) Math.ceil(data.hitboxHeight())) - 1;
        int created = 0;

        // ItemDisplay nao possui colisao fisica real. Este modo cria uma colisao fake
        // usando apenas blocos de ar, registrando cada posicao para limpeza futura.
        for (int y = minY; y <= maxY && created < maxBlocks; y++) {
            for (int x = minX; x <= maxX && created < maxBlocks; x++) {
                for (int z = minZ; z <= maxZ && created < maxBlocks; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (!block.getType().isAir()) {
                        continue;
                    }
                    block.setType(Material.BARRIER, false);
                    data.barrierBlocks().add(new BlockLocation(world.getName(), x, y, z));
                    created++;
                }
            }
        }
    }

    public void clearBarriers(DisplayData data, boolean loadChunks) {
        for (BlockLocation blockLocation : data.barrierBlocks()) {
            Location location = blockLocation.toLocation();
            if (location == null) {
                continue;
            }
            World world = location.getWorld();
            if (loadChunks && !world.isChunkLoaded(blockLocation.chunkX(), blockLocation.chunkZ())) {
                world.loadChunk(blockLocation.chunkX(), blockLocation.chunkZ());
            }
            if (!world.isChunkLoaded(blockLocation.chunkX(), blockLocation.chunkZ())) {
                continue;
            }
            Block block = world.getBlockAt(blockLocation.x(), blockLocation.y(), blockLocation.z());
            if (block.getType() == Material.BARRIER) {
                block.setType(Material.AIR, false);
            }
        }
        data.barrierBlocks().clear();
    }
}
