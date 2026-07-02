package me.codex.giantitemdisplays.listener;

import me.codex.giantitemdisplays.display.DisplayManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.entity.Player;

public final class DisplayListener implements Listener {
    private final DisplayManager displayManager;

    public DisplayListener(DisplayManager displayManager) {
        this.displayManager = displayManager;
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent event) {
        if (displayManager.handleClick(event.getPlayer(), event.getRightClicked())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player && displayManager.handleClick(player, event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        displayManager.handleChunkLoad(event.getChunk());
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        displayManager.handleChunkUnload(event.getChunk());
    }
}
