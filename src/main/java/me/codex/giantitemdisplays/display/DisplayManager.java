package me.codex.giantitemdisplays.display;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import me.codex.giantitemdisplays.GiantItemDisplaysPlugin;
import me.codex.giantitemdisplays.storage.DisplayStorage;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.joml.Matrix4f;

public final class DisplayManager {
    private static final String ROLE_ITEM = "item";
    private static final String ROLE_INTERACTION = "interaction";

    private final GiantItemDisplaysPlugin plugin;
    private final DisplayStorage storage;
    private final CollisionManager collisionManager;
    private final Map<String, DisplayData> displays = new LinkedHashMap<>();
    private final Map<String, DisplayRuntime> runtimes = new HashMap<>();
    private final Map<String, Long> clickCooldowns = new HashMap<>();
    private long animationStep;

    public DisplayManager(GiantItemDisplaysPlugin plugin, DisplayStorage storage) {
        this.plugin = plugin;
        this.storage = storage;
        this.collisionManager = new CollisionManager(plugin);
    }

    public void loadAll() {
        displays.clear();
        displays.putAll(storage.load());
        spawnLoadedDisplays();
    }

    public void reloadAll() {
        removeAllRuntimeEntities(true);
        removeLoadedPluginEntities();
        runtimes.clear();
        clickCooldowns.clear();
        loadAll();
        saveAll();
    }

    public void saveAll() {
        storage.save(displays);
    }

    public Collection<DisplayData> displays() {
        return displays.values();
    }

    public DisplayData display(String id) {
        return displays.get(id);
    }

    public boolean exists(String id) {
        return displays.containsKey(id);
    }

    public void create(String id, Player player) {
        ItemStack item = player.getInventory().getItemInMainHand().clone();
        item.setAmount(1);

        DisplayData data = new DisplayData(id);
        data.setLocation(player.getLocation());
        data.setItem(item);

        double defaultScale = plugin.getConfig().getDouble("settings.default-scale", 3.0);
        data.setScaleX(defaultScale);
        data.setScaleY(defaultScale);
        data.setScaleZ(defaultScale);
        data.setHitboxWidth(plugin.getConfig().getDouble("settings.default-hitbox-width", 2.0));
        data.setHitboxHeight(plugin.getConfig().getDouble("settings.default-hitbox-height", 2.0));
        data.setSpin(plugin.getConfig().getBoolean("settings.default-spin", true));
        data.setSpinSpeed(plugin.getConfig().getDouble("settings.default-spin-speed", 2.0));
        data.setBob(plugin.getConfig().getBoolean("settings.default-bob", true));
        data.setBobHeight(plugin.getConfig().getDouble("settings.default-bob-height", 0.15));
        data.setBobSpeed(plugin.getConfig().getDouble("settings.default-bob-speed", 0.05));
        data.setCollisionMode(CollisionMode.fromString(plugin.getConfig().getString("settings.default-collision", "interaction")));
        data.setHitboxEnabled(data.collisionMode() != CollisionMode.NONE);
        data.setGlow(plugin.getConfig().getBoolean("display-defaults.glow", false));
        data.setPermission(plugin.getConfig().getString("display-defaults.permission", "none"));
        data.setCommandExecutor(plugin.getConfig().getString("display-defaults.command-executor", "console"));
        data.setCommandValue("");

        displays.put(id, data);
        spawnOrUpdate(data);
        saveAll();
    }

    public void remove(String id) {
        DisplayData data = displays.remove(id);
        if (data != null) {
            removeRuntimeEntities(data, true);
            saveAll();
        }
    }

    public void moveHere(DisplayData data, Player player) {
        removeRuntimeEntities(data, true);
        data.setLocation(player.getLocation());
        spawnOrUpdate(data);
        saveAll();
    }

    public void setItem(DisplayData data, ItemStack item) {
        ItemStack copy = item.clone();
        copy.setAmount(1);
        data.setItem(copy);
        spawnOrUpdate(data);
        saveAll();
    }

    public void setScale(DisplayData data, double scale) {
        setScale3d(data, scale, scale, scale);
    }

    public void setScale3d(DisplayData data, double x, double y, double z) {
        data.setScaleX(x);
        data.setScaleY(y);
        data.setScaleZ(z);
        updateTransformation(data);
        saveAll();
    }

    public void setRotation(DisplayData data, float yaw, float pitch) {
        data.setYaw(yaw);
        data.setPitch(pitch);
        updateTransformation(data);
        saveAll();
    }

    public void setHitbox(DisplayData data, double width, double height) {
        data.setHitboxWidth(width);
        data.setHitboxHeight(height);
        data.setHitboxEnabled(data.collisionMode() != CollisionMode.NONE);
        spawnOrUpdate(data);
        saveAll();
    }

    public void setCollision(DisplayData data, CollisionMode mode) {
        removeRuntimeEntities(data, true);
        data.setCollisionMode(mode);
        data.setHitboxEnabled(mode != CollisionMode.NONE);
        spawnOrUpdate(data);
        saveAll();
    }

    public void setGlow(DisplayData data, boolean glow) {
        data.setGlow(glow);
        DisplayRuntime runtime = runtimes.get(data.id());
        if (runtime != null && runtime.hasValidItemDisplay()) {
            runtime.itemDisplay().setGlowing(glow);
        }
        saveAll();
    }

    public void tickAnimations() {
        animationStep++;
        for (DisplayData data : displays.values()) {
            DisplayRuntime runtime = runtimes.get(data.id());
            if (runtime == null || !runtime.hasValidItemDisplay()) {
                spawnOrUpdate(data);
                continue;
            }
            updateTransformation(data);
        }
    }

    public boolean handleClick(Player player, Entity entity) {
        if (!isPluginEntity(entity)) {
            return false;
        }
        String role = entity.getPersistentDataContainer().get(plugin.roleKey(), PersistentDataType.STRING);
        if (!ROLE_INTERACTION.equals(role)) {
            return false;
        }
        String id = entity.getPersistentDataContainer().get(plugin.displayIdKey(), PersistentDataType.STRING);
        DisplayData data = id == null ? null : displays.get(id);
        if (data == null) {
            entity.remove();
            return true;
        }

        if (!player.hasPermission("giantitemdisplays.interact") && !player.hasPermission("giantitemdisplays.admin")) {
            plugin.lang().send(player, "click-no-permission");
            return true;
        }
        if (data.hasClickPermission() && !player.hasPermission(data.permission()) && !player.hasPermission("giantitemdisplays.admin")) {
            plugin.lang().send(player, "click-no-permission");
            return true;
        }
        if (isCoolingDown(player, data.id())) {
            plugin.lang().send(player, "click-cooldown");
            return true;
        }

        runDisplayCommand(player, data);
        plugin.lang().send(player, "click-success", Map.of("id", data.id()));
        return true;
    }

    public void handleChunkLoad(Chunk chunk) {
        for (Entity entity : chunk.getEntities()) {
            if (!isPluginEntity(entity)) {
                continue;
            }
            String id = entity.getPersistentDataContainer().get(plugin.displayIdKey(), PersistentDataType.STRING);
            DisplayData data = id == null ? null : displays.get(id);
            if (data == null) {
                entity.remove();
                continue;
            }
            String role = entity.getPersistentDataContainer().get(plugin.roleKey(), PersistentDataType.STRING);
            DisplayRuntime runtime = runtimes.computeIfAbsent(id, ignored -> new DisplayRuntime());
            if (ROLE_ITEM.equals(role) && entity instanceof ItemDisplay itemDisplay) {
                runtime.itemDisplay(itemDisplay);
            } else if (ROLE_INTERACTION.equals(role) && entity instanceof Interaction interaction) {
                runtime.interaction(interaction);
            } else {
                entity.remove();
            }
        }

        for (DisplayData data : displays.values()) {
            if (sameChunk(data, chunk)) {
                spawnOrUpdate(data);
            }
        }
    }

    public void handleChunkUnload(Chunk chunk) {
        for (DisplayData data : displays.values()) {
            if (sameChunk(data, chunk)) {
                runtimes.remove(data.id());
            }
        }
    }

    public void removeLoadedPluginEntities() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (isPluginEntity(entity)) {
                    entity.remove();
                }
            }
        }
    }

    public void removeAllRuntimeEntities(boolean clearBarriers) {
        for (DisplayData data : displays.values()) {
            removeRuntimeEntities(data, clearBarriers);
        }
        runtimes.clear();
    }

    public void spawnLoadedDisplays() {
        for (DisplayData data : displays.values()) {
            spawnOrUpdate(data);
        }
    }

    private void spawnOrUpdate(DisplayData data) {
        Location location = data.toLocation();
        if (location == null) {
            return;
        }
        World world = location.getWorld();
        if (!world.isChunkLoaded(data.chunkX(), data.chunkZ())) {
            return;
        }

        DisplayRuntime runtime = runtimes.computeIfAbsent(data.id(), ignored -> new DisplayRuntime());
        if (!runtime.hasValidItemDisplay()) {
            ItemDisplay itemDisplay = world.spawn(location, ItemDisplay.class, spawned -> {
                mark(spawned, data.id(), ROLE_ITEM);
                spawned.setPersistent(false);
                spawned.setGravity(false);
                spawned.setInvulnerable(true);
                spawned.setSilent(true);
            });
            runtime.itemDisplay(itemDisplay);
        }
        applyItemDisplay(runtime.itemDisplay(), data);

        if (data.usesInteraction()) {
            if (!runtime.hasValidInteraction()) {
                Interaction interaction = world.spawn(location, Interaction.class, spawned -> {
                    mark(spawned, data.id(), ROLE_INTERACTION);
                    spawned.setPersistent(false);
                    spawned.setGravity(false);
                    spawned.setInvulnerable(true);
                    spawned.setSilent(true);
                });
                runtime.interaction(interaction);
            }
            applyInteraction(runtime.interaction(), data, location);
        } else if (runtime.hasValidInteraction()) {
            runtime.interaction().remove();
            runtime.interaction(null);
        }

        if (data.collisionMode() == CollisionMode.BARRIER) {
            collisionManager.applyBarriers(data);
        } else {
            collisionManager.clearBarriers(data, false);
        }
    }

    private void applyItemDisplay(ItemDisplay itemDisplay, DisplayData data) {
        itemDisplay.teleport(data.toLocation());
        itemDisplay.setItemStack(data.item().clone());
        itemDisplay.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.GROUND);
        itemDisplay.setGlowing(data.glow());
        updateTransformation(data);
    }

    private void applyInteraction(Interaction interaction, DisplayData data, Location location) {
        interaction.teleport(location);
        interaction.setInteractionWidth((float) Math.max(0.1D, data.hitboxWidth()));
        interaction.setInteractionHeight((float) Math.max(0.1D, data.hitboxHeight()));
        interaction.setResponsive(true);
    }

    private void updateTransformation(DisplayData data) {
        DisplayRuntime runtime = runtimes.get(data.id());
        if (runtime == null || !runtime.hasValidItemDisplay()) {
            return;
        }
        float yaw = (float) Math.toRadians(data.yaw() + (data.spin() ? animationStep * data.spinSpeed() : 0.0D));
        float pitch = (float) Math.toRadians(data.pitch());
        float bob = data.bob() ? (float) (Math.sin(animationStep * data.bobSpeed()) * data.bobHeight()) : 0.0F;
        Matrix4f matrix = new Matrix4f()
                .translation(0.0F, bob, 0.0F)
                .rotateY(yaw)
                .rotateX(pitch)
                .scale((float) data.scaleX(), (float) data.scaleY(), (float) data.scaleZ());
        runtime.itemDisplay().setTransformationMatrix(matrix);
    }

    private void runDisplayCommand(Player player, DisplayData data) {
        String command = data.commandValue();
        if (command == null || command.isBlank()) {
            return;
        }
        command = applyPlaceholders(command, player, data);
        if (command.startsWith("/")) {
            command = command.substring(1);
        }

        try {
            if ("player".equalsIgnoreCase(data.commandExecutor())) {
                Bukkit.dispatchCommand(player, command);
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        } catch (RuntimeException exception) {
            plugin.getLogger().warning("Erro ao executar comando do display " + data.id() + ": " + exception.getMessage());
            plugin.lang().send(player, "command-error");
        }
    }

    private String applyPlaceholders(String command, Player player, DisplayData data) {
        return command
                .replace("%player%", player.getName())
                .replace("%uuid%", player.getUniqueId().toString())
                .replace("%world%", data.worldName())
                .replace("%x%", Double.toString(data.x()))
                .replace("%y%", Double.toString(data.y()))
                .replace("%z%", Double.toString(data.z()))
                .replace("%id%", data.id());
    }

    private boolean isCoolingDown(Player player, String id) {
        long cooldown = Math.max(0L, plugin.getConfig().getLong("settings.click-cooldown-ms", 1000L));
        if (cooldown == 0L) {
            return false;
        }
        String key = player.getUniqueId() + ":" + id.toLowerCase(Locale.ROOT);
        long now = System.currentTimeMillis();
        long last = clickCooldowns.getOrDefault(key, 0L);
        if (now - last < cooldown) {
            return true;
        }
        clickCooldowns.put(key, now);
        return false;
    }

    private void removeRuntimeEntities(DisplayData data, boolean clearBarriers) {
        DisplayRuntime runtime = runtimes.remove(data.id());
        if (runtime != null) {
            runtime.removeEntities();
        }
        if (clearBarriers) {
            collisionManager.clearBarriers(data, true);
        }
    }

    private void mark(Entity entity, String id, String role) {
        PersistentDataContainer data = entity.getPersistentDataContainer();
        data.set(plugin.markerKey(), PersistentDataType.BYTE, (byte) 1);
        data.set(plugin.displayIdKey(), PersistentDataType.STRING, id);
        data.set(plugin.roleKey(), PersistentDataType.STRING, role);
    }

    private boolean isPluginEntity(Entity entity) {
        PersistentDataContainer data = entity.getPersistentDataContainer();
        return data.has(plugin.markerKey(), PersistentDataType.BYTE);
    }

    private boolean sameChunk(DisplayData data, Chunk chunk) {
        return chunk.getWorld().getName().equals(data.worldName())
                && chunk.getX() == data.chunkX()
                && chunk.getZ() == data.chunkZ();
    }

    public static boolean isUsableItem(ItemStack item) {
        return item != null && item.getType() != Material.AIR && !item.getType().isAir();
    }

    public static boolean isValidId(String id) {
        return id != null && id.matches("[A-Za-z0-9_-]+");
    }

    public static String cooldownKey(UUID uuid, String id) {
        return uuid + ":" + id.toLowerCase(Locale.ROOT);
    }
}
