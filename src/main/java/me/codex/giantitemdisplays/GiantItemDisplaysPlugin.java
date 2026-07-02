package me.codex.giantitemdisplays;

import me.codex.giantitemdisplays.command.DisplayCommand;
import me.codex.giantitemdisplays.display.DisplayManager;
import me.codex.giantitemdisplays.listener.DisplayListener;
import me.codex.giantitemdisplays.storage.DisplayStorage;
import me.codex.giantitemdisplays.task.AnimationTask;
import me.codex.giantitemdisplays.util.LangManager;
import org.bukkit.NamespacedKey;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class GiantItemDisplaysPlugin extends JavaPlugin {
    private NamespacedKey markerKey;
    private NamespacedKey displayIdKey;
    private NamespacedKey roleKey;
    private LangManager langManager;
    private DisplayStorage displayStorage;
    private DisplayManager displayManager;
    private AnimationTask animationTask;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResourceIfMissing("lang.yml");
        saveResourceIfMissing("displays.yml");

        markerKey = new NamespacedKey(this, "plugin_entity");
        displayIdKey = new NamespacedKey(this, "display_id");
        roleKey = new NamespacedKey(this, "entity_role");

        langManager = new LangManager(this);
        langManager.reload();

        displayStorage = new DisplayStorage(this);
        displayManager = new DisplayManager(this, displayStorage);

        if (getConfig().getBoolean("settings.remove-orphan-plugin-entities-on-startup", true)) {
            displayManager.removeLoadedPluginEntities();
        }

        displayManager.loadAll();
        getLogger().info("GiantItemDisplays carregado. Plugin enviado por Ogaki.");

        DisplayCommand displayCommand = new DisplayCommand(this, displayManager, langManager);
        PluginCommand command = getCommand("gid");
        if (command != null) {
            command.setExecutor(displayCommand);
            command.setTabCompleter(displayCommand);
        }

        getServer().getPluginManager().registerEvents(new DisplayListener(displayManager), this);

        animationTask = new AnimationTask(displayManager);
        long rate = Math.max(1L, getConfig().getLong("settings.animation-tick-rate", 2L));
        animationTask.runTaskTimer(this, rate, rate);
    }

    @Override
    public void onDisable() {
        if (animationTask != null) {
            animationTask.cancel();
        }
        if (displayManager != null) {
            displayManager.removeAllRuntimeEntities(true);
            displayManager.saveAll();
        }
    }

    public void reloadPlugin() {
        reloadConfig();
        langManager.reload();
        displayManager.reloadAll();

        if (animationTask != null) {
            animationTask.cancel();
        }
        animationTask = new AnimationTask(displayManager);
        long rate = Math.max(1L, getConfig().getLong("settings.animation-tick-rate", 2L));
        animationTask.runTaskTimer(this, rate, rate);
    }

    private void saveResourceIfMissing(String resource) {
        if (!getDataFolder().toPath().resolve(resource).toFile().exists()) {
            saveResource(resource, false);
        }
    }

    public NamespacedKey markerKey() {
        return markerKey;
    }

    public NamespacedKey displayIdKey() {
        return displayIdKey;
    }

    public NamespacedKey roleKey() {
        return roleKey;
    }

    public LangManager lang() {
        return langManager;
    }
}
