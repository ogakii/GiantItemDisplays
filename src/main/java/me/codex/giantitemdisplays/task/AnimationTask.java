package me.codex.giantitemdisplays.task;

import me.codex.giantitemdisplays.display.DisplayManager;
import org.bukkit.scheduler.BukkitRunnable;

public final class AnimationTask extends BukkitRunnable {
    private final DisplayManager displayManager;

    public AnimationTask(DisplayManager displayManager) {
        this.displayManager = displayManager;
    }

    @Override
    public void run() {
        displayManager.tickAnimations();
    }
}
