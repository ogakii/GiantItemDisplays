package me.codex.giantitemdisplays.util;

import java.io.File;
import java.util.Map;
import me.codex.giantitemdisplays.GiantItemDisplaysPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public final class LangManager {
    private final GiantItemDisplaysPlugin plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final LegacyComponentSerializer legacy = LegacyComponentSerializer.legacyAmpersand();
    private FileConfiguration lang;

    public LangManager(GiantItemDisplaysPlugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "lang.yml");
        lang = YamlConfiguration.loadConfiguration(file);
    }

    public void send(CommandSender sender, String key) {
        send(sender, key, Map.of());
    }

    public void send(CommandSender sender, String key, Map<String, String> placeholders) {
        String raw = raw(key);
        if (raw.isBlank()) {
            return;
        }
        if (!key.equals("prefix")) {
            raw = raw("prefix") + raw;
        }
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            raw = raw.replace("%" + entry.getKey() + "%", entry.getValue());
        }
        sender.sendMessage(parse(raw));
    }

    public String raw(String key) {
        return lang.getString(key, key);
    }

    public Component parse(String raw) {
        String miniRaw = legacyToMiniMessage(raw);
        if (miniRaw.contains("<") && miniRaw.contains(">")) {
            try {
                return miniMessage.deserialize(miniRaw);
            } catch (RuntimeException ignored) {
                return legacy.deserialize(raw);
            }
        }
        return legacy.deserialize(raw);
    }

    private String legacyToMiniMessage(String raw) {
        String result = raw;
        result = result.replace("&0", "<black>").replace("&1", "<dark_blue>").replace("&2", "<dark_green>");
        result = result.replace("&3", "<dark_aqua>").replace("&4", "<dark_red>").replace("&5", "<dark_purple>");
        result = result.replace("&6", "<gold>").replace("&7", "<gray>").replace("&8", "<dark_gray>");
        result = result.replace("&9", "<blue>").replace("&a", "<green>").replace("&b", "<aqua>");
        result = result.replace("&c", "<red>").replace("&d", "<light_purple>").replace("&e", "<yellow>");
        result = result.replace("&f", "<white>").replace("&l", "<bold>").replace("&m", "<strikethrough>");
        result = result.replace("&n", "<underlined>").replace("&o", "<italic>").replace("&r", "<reset>");
        result = result.replace("&A", "<green>").replace("&B", "<aqua>").replace("&C", "<red>");
        result = result.replace("&D", "<light_purple>").replace("&E", "<yellow>").replace("&F", "<white>");
        result = result.replace("&L", "<bold>").replace("&M", "<strikethrough>").replace("&N", "<underlined>");
        result = result.replace("&O", "<italic>").replace("&R", "<reset>");
        return result;
    }
}
