package me.codex.giantitemdisplays.storage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import me.codex.giantitemdisplays.GiantItemDisplaysPlugin;
import me.codex.giantitemdisplays.display.BlockLocation;
import me.codex.giantitemdisplays.display.CollisionMode;
import me.codex.giantitemdisplays.display.DisplayData;
import me.codex.giantitemdisplays.util.ItemSerializer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public final class DisplayStorage {
    private final GiantItemDisplaysPlugin plugin;
    private final File file;

    public DisplayStorage(GiantItemDisplaysPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "displays.yml");
    }

    public Map<String, DisplayData> load() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        Map<String, DisplayData> displays = new LinkedHashMap<>();
        ConfigurationSection root = config.getConfigurationSection("displays");
        if (root == null) {
            return displays;
        }

        for (String id : root.getKeys(false)) {
            ConfigurationSection section = root.getConfigurationSection(id);
            if (section == null) {
                continue;
            }
            DisplayData data = new DisplayData(id);
            data.setWorldName(section.getString("world", "world"));
            data.setX(section.getDouble("x"));
            data.setY(section.getDouble("y"));
            data.setZ(section.getDouble("z"));
            data.setYaw((float) section.getDouble("yaw"));
            data.setPitch((float) section.getDouble("pitch"));
            data.setItem(ItemSerializer.read(section, "item"));
            data.setScaleX(section.getDouble("scale.x", plugin.getConfig().getDouble("settings.default-scale", 3.0)));
            data.setScaleY(section.getDouble("scale.y", plugin.getConfig().getDouble("settings.default-scale", 3.0)));
            data.setScaleZ(section.getDouble("scale.z", plugin.getConfig().getDouble("settings.default-scale", 3.0)));
            data.setHitboxEnabled(section.getBoolean("hitbox.enabled", true));
            data.setHitboxWidth(section.getDouble("hitbox.width", plugin.getConfig().getDouble("settings.default-hitbox-width", 2.0)));
            data.setHitboxHeight(section.getDouble("hitbox.height", plugin.getConfig().getDouble("settings.default-hitbox-height", 2.0)));
            data.setSpin(section.getBoolean("animation.spin", plugin.getConfig().getBoolean("settings.default-spin", true)));
            data.setSpinSpeed(section.getDouble("animation.spin-speed", plugin.getConfig().getDouble("settings.default-spin-speed", 2.0)));
            data.setBob(section.getBoolean("animation.bob", plugin.getConfig().getBoolean("settings.default-bob", true)));
            data.setBobHeight(section.getDouble("animation.bob-height", plugin.getConfig().getDouble("settings.default-bob-height", 0.15)));
            data.setBobSpeed(section.getDouble("animation.bob-speed", plugin.getConfig().getDouble("settings.default-bob-speed", 0.05)));
            data.setCollisionMode(CollisionMode.fromString(section.getString("collision", plugin.getConfig().getString("settings.default-collision", "interaction"))));
            data.setGlow(section.getBoolean("glow", plugin.getConfig().getBoolean("display-defaults.glow", false)));
            data.setPermission(section.getString("permission", plugin.getConfig().getString("display-defaults.permission", "none")));
            data.setCommandExecutor(section.getString("command.executor", plugin.getConfig().getString("display-defaults.command-executor", "console")));
            data.setCommandValue(section.getString("command.value", ""));
            loadBarriers(section, data);
            displays.put(id, data);
        }
        return displays;
    }

    public void save(Map<String, DisplayData> displays) {
        FileConfiguration config = new YamlConfiguration();
        ConfigurationSection root = config.createSection("displays");
        for (DisplayData data : displays.values()) {
            ConfigurationSection section = root.createSection(data.id());
            section.set("world", data.worldName());
            section.set("x", data.x());
            section.set("y", data.y());
            section.set("z", data.z());
            section.set("yaw", data.yaw());
            section.set("pitch", data.pitch());
            ItemSerializer.write(section, "item", data.item());
            section.set("scale.x", data.scaleX());
            section.set("scale.y", data.scaleY());
            section.set("scale.z", data.scaleZ());
            section.set("hitbox.enabled", data.hitboxEnabled());
            section.set("hitbox.width", data.hitboxWidth());
            section.set("hitbox.height", data.hitboxHeight());
            section.set("animation.spin", data.spin());
            section.set("animation.spin-speed", data.spinSpeed());
            section.set("animation.bob", data.bob());
            section.set("animation.bob-height", data.bobHeight());
            section.set("animation.bob-speed", data.bobSpeed());
            section.set("collision", data.collisionMode().configName());
            section.set("glow", data.glow());
            section.set("permission", data.permission());
            section.set("command.executor", data.commandExecutor());
            section.set("command.value", data.commandValue());
            saveBarriers(section, data);
        }

        try {
            config.save(file);
        } catch (IOException exception) {
            plugin.getLogger().severe("Nao foi possivel salvar displays.yml: " + exception.getMessage());
        }
    }

    private void loadBarriers(ConfigurationSection section, DisplayData data) {
        List<Map<?, ?>> maps = section.getMapList("barriers");
        for (Map<?, ?> map : maps) {
            Object world = map.get("world");
            Object x = map.get("x");
            Object y = map.get("y");
            Object z = map.get("z");
            if (world instanceof String worldName && x instanceof Number bx && y instanceof Number by && z instanceof Number bz) {
                data.barrierBlocks().add(new BlockLocation(worldName, bx.intValue(), by.intValue(), bz.intValue()));
            }
        }
    }

    private void saveBarriers(ConfigurationSection section, DisplayData data) {
        List<Map<String, Object>> serialized = new ArrayList<>();
        for (BlockLocation block : data.barrierBlocks()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("world", block.world());
            map.put("x", block.x());
            map.put("y", block.y());
            map.put("z", block.z());
            serialized.add(map);
        }
        section.set("barriers", serialized);
    }
}
