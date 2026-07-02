package me.codex.giantitemdisplays.task;

import me.codex.giantitemdisplays.display.DisplayManager;
import org.bukkit.scheduler.BukkitRunnable;

public final class AnimationTask extends BukkitRunnable {
    private final DisplayManager displayManager;
    private final long elapsedTicks;

    public AnimationTask(DisplayManager displayManager, long elapsedTicks) {
        this.displayManager = displayManager;
        this.elapsedTicks = elapsedTicks;
    }

    @Override
    public void run() {
        displayManager.tickAnimations(elapsedTicks);
    }
}
