package me.codex.giantitemdisplays.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import me.codex.giantitemdisplays.GiantItemDisplaysPlugin;
import me.codex.giantitemdisplays.display.CollisionMode;
import me.codex.giantitemdisplays.display.DisplayData;
import me.codex.giantitemdisplays.display.DisplayManager;
import me.codex.giantitemdisplays.util.LangManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DisplayCommand implements CommandExecutor, TabCompleter {
    private static final List<String> SUBCOMMANDS = List.of(
            "create",
            "remove",
            "list",
            "teleport",
            "movehere",
            "setitem",
            "setscale",
            "setscale3d",
            "setrotation",
            "setspin",
            "setspeed",
            "setbob",
            "setbobheight",
            "setbobspeed",
            "setcommand",
            "setdeluxemenu",
            "setpermission",
            "sethitbox",
            "setcollision",
            "setglow",
            "credits",
            "reload"
    );

    private final GiantItemDisplaysPlugin plugin;
    private final DisplayManager displayManager;
    private final LangManager lang;

    public DisplayCommand(GiantItemDisplaysPlugin plugin, DisplayManager displayManager, LangManager lang) {
        this.plugin = plugin;
        this.displayManager = displayManager;
        this.lang = lang;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendUsage(sender, "/" + label + " <create|remove|list|teleport|movehere|setitem|setscale|setscale3d|setrotation|setspin|setspeed|setbob|setbobheight|setbobSpeed|setcommand|setdeluxemenu|setpermission|sethitbox|setcollision|setglow|credits|reload>");
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        return switch (sub) {
            case "create" -> create(sender, label, args);
            case "remove" -> remove(sender, label, args);
            case "list" -> list(sender);
            case "teleport" -> teleport(sender, label, args);
            case "movehere" -> moveHere(sender, label, args);
            case "setitem" -> setItem(sender, label, args);
            case "setscale" -> setScale(sender, label, args);
            case "setscale3d" -> setScale3d(sender, label, args);
            case "setrotation" -> setRotation(sender, label, args);
            case "setspin" -> setSpin(sender, label, args);
            case "setspeed" -> setSpeed(sender, label, args);
            case "setbob" -> setBob(sender, label, args);
            case "setbobheight" -> setBobHeight(sender, label, args);
            case "setbobspeed" -> setBobSpeed(sender, label, args);
            case "setcommand" -> setCommand(sender, label, args);
            case "setdeluxemenu" -> setDeluxeMenu(sender, label, args);
            case "setpermission" -> setPermission(sender, label, args);
            case "sethitbox" -> setHitbox(sender, label, args);
            case "setcollision" -> setCollision(sender, label, args);
            case "setglow" -> setGlow(sender, label, args);
            case "credits" -> credits(sender);
            case "reload" -> reload(sender);
            default -> {
                sendUsage(sender, "/" + label + " help");
                yield true;
            }
        };
    }

    private boolean create(CommandSender sender, String label, String[] args) {
        if (!require(sender, "giantitemdisplays.create")) {
            return true;
        }
        if (!(sender instanceof Player player)) {
            lang.send(sender, "player-only");
            return true;
        }
        if (args.length != 2) {
            sendUsage(sender, "/" + label + " create <id>");
            return true;
        }
        String id = args[1];
        if (!validateId(sender, id) || requireMissing(sender, id)) {
            return true;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!DisplayManager.isUsableItem(item)) {
            lang.send(sender, "item-required");
            return true;
        }
        displayManager.create(id, player);
        lang.send(sender, "display-created", Map.of("id", id));
        return true;
    }

    private boolean remove(CommandSender sender, String label, String[] args) {
        if (!require(sender, "giantitemdisplays.remove")) {
            return true;
        }
        if (args.length != 2) {
            sendUsage(sender, "/" + label + " remove <id>");
            return true;
        }
        DisplayData data = requireDisplay(sender, args[1]);
        if (data == null) {
            return true;
        }
        displayManager.remove(data.id());
        lang.send(sender, "display-removed", Map.of("id", data.id()));
        return true;
    }

    private boolean list(CommandSender sender) {
        if (!require(sender, "giantitemdisplays.create")) {
            return true;
        }
        Collection<DisplayData> displays = displayManager.displays();
        if (displays.isEmpty()) {
            lang.send(sender, "display-list-empty");
            return true;
        }
        String ids = String.join(", ", displays.stream().map(DisplayData::id).toList());
        lang.send(sender, "display-list-header", Map.of("ids", ids));
        return true;
    }

    private boolean teleport(CommandSender sender, String label, String[] args) {
        if (!require(sender, "giantitemdisplays.create")) {
            return true;
        }
        if (!(sender instanceof Player player)) {
            lang.send(sender, "player-only");
            return true;
        }
        if (args.length != 2) {
            sendUsage(sender, "/" + label + " teleport <id>");
            return true;
        }
        DisplayData data = requireDisplay(sender, args[1]);
        if (data == null) {
            return true;
        }
        if (data.toLocation() != null) {
            player.teleport(data.toLocation());
            lang.send(sender, "display-teleported", Map.of("id", data.id()));
        }
        return true;
    }

    private boolean moveHere(CommandSender sender, String label, String[] args) {
        if (!require(sender, "giantitemdisplays.create")) {
            return true;
        }
        if (!(sender instanceof Player player)) {
            lang.send(sender, "player-only");
            return true;
        }
        if (args.length != 2) {
            sendUsage(sender, "/" + label + " movehere <id>");
            return true;
        }
        DisplayData data = requireDisplay(sender, args[1]);
        if (data == null) {
            return true;
        }
        displayManager.moveHere(data, player);
        lang.send(sender, "display-moved", Map.of("id", data.id()));
        return true;
    }

    private boolean setItem(CommandSender sender, String label, String[] args) {
        if (!require(sender, "giantitemdisplays.create")) {
            return true;
        }
        if (!(sender instanceof Player player)) {
            lang.send(sender, "player-only");
            return true;
        }
        if (args.length != 2) {
            sendUsage(sender, "/" + label + " setitem <id>");
            return true;
        }
        DisplayData data = requireDisplay(sender, args[1]);
        if (data == null) {
            return true;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!DisplayManager.isUsableItem(item)) {
            lang.send(sender, "item-required");
            return true;
        }
        displayManager.setItem(data, item);
        lang.send(sender, "item-set", Map.of("id", data.id()));
        return true;
    }

    private boolean setScale(CommandSender sender, String label, String[] args) {
        if (!require(sender, "giantitemdisplays.create")) {
            return true;
        }
        if (args.length != 3) {
            sendUsage(sender, "/" + label + " setscale <id> <value>");
            return true;
        }
        DisplayData data = requireDisplay(sender, args[1]);
        Double scale = parsePositiveDouble(sender, args[2]);
        if (data == null || scale == null) {
            return true;
        }
        displayManager.setScale(data, scale);
        lang.send(sender, "scale-set", Map.of("id", data.id(), "scale", format(scale)));
        return true;
    }

    private boolean setScale3d(CommandSender sender, String label, String[] args) {
        if (!require(sender, "giantitemdisplays.create")) {
            return true;
        }
        if (args.length != 5) {
            sendUsage(sender, "/" + label + " setscale3d <id> <x> <y> <z>");
            return true;
        }
        DisplayData data = requireDisplay(sender, args[1]);
        Double x = parsePositiveDouble(sender, args[2]);
        Double y = parsePositiveDouble(sender, args[3]);
        Double z = parsePositiveDouble(sender, args[4]);
        if (data == null || x == null || y == null || z == null) {
            return true;
        }
        displayManager.setScale3d(data, x, y, z);
        lang.send(sender, "scale3d-set", Map.of("id", data.id(), "x", format(x), "y", format(y), "z", format(z)));
        return true;
    }

    private boolean setRotation(CommandSender sender, String label, String[] args) {
        if (!require(sender, "giantitemdisplays.create")) {
            return true;
        }
        if (args.length != 4) {
            sendUsage(sender, "/" + label + " setrotation <id> <yaw> <pitch>");
            return true;
        }
        DisplayData data = requireDisplay(sender, args[1]);
        Double yaw = parseDouble(sender, args[2]);
        Double pitch = parseDouble(sender, args[3]);
        if (data == null || yaw == null || pitch == null) {
            return true;
        }
        displayManager.setRotation(data, yaw.floatValue(), pitch.floatValue());
        lang.send(sender, "rotation-set", Map.of("id", data.id(), "yaw", format(yaw), "pitch", format(pitch)));
        return true;
    }

    private boolean setSpin(CommandSender sender, String label, String[] args) {
        if (!require(sender, "giantitemdisplays.create")) {
            return true;
        }
        if (args.length != 3) {
            sendUsage(sender, "/" + label + " setspin <id> <true/false>");
            return true;
        }
        DisplayData data = requireDisplay(sender, args[1]);
        Boolean value = parseBoolean(sender, args[2]);
        if (data == null || value == null) {
            return true;
        }
        data.setSpin(value);
        displayManager.saveAll();
        lang.send(sender, "spin-set", Map.of("id", data.id(), "value", Boolean.toString(value)));
        return true;
    }

    private boolean setSpeed(CommandSender sender, String label, String[] args) {
        if (!require(sender, "giantitemdisplays.create")) {
            return true;
        }
        if (args.length != 3) {
            sendUsage(sender, "/" + label + " setspeed <id> <value>");
            return true;
        }
        DisplayData data = requireDisplay(sender, args[1]);
        Double value = parseDouble(sender, args[2]);
        if (data == null || value == null) {
            return true;
        }
        data.setSpinSpeed(value);
        displayManager.saveAll();
        lang.send(sender, "speed-set", Map.of("id", data.id(), "value", format(value)));
        return true;
    }

    private boolean setBob(CommandSender sender, String label, String[] args) {
        if (!require(sender, "giantitemdisplays.create")) {
            return true;
        }
        if (args.length != 3) {
            sendUsage(sender, "/" + label + " setbob <id> <true/false>");
            return true;
        }
        DisplayData data = requireDisplay(sender, args[1]);
        Boolean value = parseBoolean(sender, args[2]);
        if (data == null || value == null) {
            return true;
        }
        data.setBob(value);
        displayManager.saveAll();
        lang.send(sender, "bob-set", Map.of("id", data.id(), "value", Boolean.toString(value)));
        return true;
    }

    private boolean setBobHeight(CommandSender sender, String label, String[] args) {
        if (!require(sender, "giantitemdisplays.create")) {
            return true;
        }
        if (args.length != 3) {
            sendUsage(sender, "/" + label + " setbobheight <id> <value>");
            return true;
        }
        DisplayData data = requireDisplay(sender, args[1]);
        Double value = parseDouble(sender, args[2]);
        if (data == null || value == null) {
            return true;
        }
        data.setBobHeight(value);
        displayManager.saveAll();
        lang.send(sender, "bobheight-set", Map.of("id", data.id(), "value", format(value)));
        return true;
    }

    private boolean setBobSpeed(CommandSender sender, String label, String[] args) {
        if (!require(sender, "giantitemdisplays.create")) {
            return true;
        }
        if (args.length != 3) {
            sendUsage(sender, "/" + label + " setbobSpeed <id> <value>");
            return true;
        }
        DisplayData data = requireDisplay(sender, args[1]);
        Double value = parseDouble(sender, args[2]);
        if (data == null || value == null) {
            return true;
        }
        data.setBobSpeed(value);
        displayManager.saveAll();
        lang.send(sender, "bobspeed-set", Map.of("id", data.id(), "value", format(value)));
        return true;
    }

    private boolean setCommand(CommandSender sender, String label, String[] args) {
        if (!require(sender, "giantitemdisplays.create")) {
            return true;
        }
        if (args.length < 4) {
            sendUsage(sender, "/" + label + " setcommand <id> <console/player> <command>");
            return true;
        }
        DisplayData data = requireDisplay(sender, args[1]);
        if (data == null) {
            return true;
        }
        String executor = args[2].toLowerCase(Locale.ROOT);
        if (!executor.equals("console") && !executor.equals("player")) {
            lang.send(sender, "invalid-executor");
            return true;
        }
        String command = join(args, 3);
        data.setCommandExecutor(executor);
        data.setCommandValue(command);
        displayManager.saveAll();
        lang.send(sender, "command-set", Map.of("id", data.id(), "executor", executor, "command", command));
        return true;
    }

    private boolean setDeluxeMenu(CommandSender sender, String label, String[] args) {
        if (!require(sender, "giantitemdisplays.create")) {
            return true;
        }
        if (args.length != 3) {
            sendUsage(sender, "/" + label + " setdeluxemenu <id> <menu>");
            return true;
        }
        DisplayData data = requireDisplay(sender, args[1]);
        if (data == null) {
            return true;
        }
        String menu = args[2];
        data.setCommandExecutor("console");
        data.setCommandValue("dm open " + menu + " %player%");
        displayManager.saveAll();
        lang.send(sender, "deluxemenu-set", Map.of("id", data.id(), "menu", menu));
        return true;
    }

    private boolean setPermission(CommandSender sender, String label, String[] args) {
        if (!require(sender, "giantitemdisplays.create")) {
            return true;
        }
        if (args.length != 3) {
            sendUsage(sender, "/" + label + " setpermission <id> <permission/none>");
            return true;
        }
        DisplayData data = requireDisplay(sender, args[1]);
        if (data == null) {
            return true;
        }
        data.setPermission(args[2]);
        displayManager.saveAll();
        lang.send(sender, "permission-set", Map.of("id", data.id(), "permission", args[2]));
        return true;
    }

    private boolean setHitbox(CommandSender sender, String label, String[] args) {
        if (!require(sender, "giantitemdisplays.create")) {
            return true;
        }
        if (args.length != 4) {
            sendUsage(sender, "/" + label + " sethitbox <id> <width> <height>");
            return true;
        }
        DisplayData data = requireDisplay(sender, args[1]);
        Double width = parsePositiveDouble(sender, args[2]);
        Double height = parsePositiveDouble(sender, args[3]);
        if (data == null || width == null || height == null) {
            return true;
        }
        displayManager.setHitbox(data, width, height);
        lang.send(sender, "hitbox-set", Map.of("id", data.id(), "width", format(width), "height", format(height)));
        return true;
    }

    private boolean setCollision(CommandSender sender, String label, String[] args) {
        if (!require(sender, "giantitemdisplays.create")) {
            return true;
        }
        if (args.length != 3) {
            sendUsage(sender, "/" + label + " setcollision <id> <none/interaction/barrier>");
            return true;
        }
        DisplayData data = requireDisplay(sender, args[1]);
        if (data == null) {
            return true;
        }
        if (!CollisionMode.isValid(args[2])) {
            lang.send(sender, "invalid-collision");
            return true;
        }
        CollisionMode mode = CollisionMode.fromString(args[2]);
        displayManager.setCollision(data, mode);
        lang.send(sender, "collision-set", Map.of("id", data.id(), "collision", mode.configName()));
        return true;
    }

    private boolean setGlow(CommandSender sender, String label, String[] args) {
        if (!require(sender, "giantitemdisplays.create")) {
            return true;
        }
        if (args.length != 3) {
            sendUsage(sender, "/" + label + " setglow <id> <true/false>");
            return true;
        }
        DisplayData data = requireDisplay(sender, args[1]);
        Boolean value = parseBoolean(sender, args[2]);
        if (data == null || value == null) {
            return true;
        }
        displayManager.setGlow(data, value);
        lang.send(sender, "glow-set", Map.of("id", data.id(), "value", Boolean.toString(value)));
        return true;
    }

    private boolean credits(CommandSender sender) {
        lang.send(sender, "credits");
        return true;
    }

    private boolean reload(CommandSender sender) {
        if (!require(sender, "giantitemdisplays.reload")) {
            return true;
        }
        plugin.reloadPlugin();
        lang.send(sender, "reloaded");
        return true;
    }

    private boolean require(CommandSender sender, String permission) {
        if (sender.hasPermission("giantitemdisplays.admin") || sender.hasPermission(permission)) {
            return true;
        }
        lang.send(sender, "no-permission");
        return false;
    }

    private DisplayData requireDisplay(CommandSender sender, String id) {
        DisplayData data = displayManager.display(id);
        if (data == null) {
            lang.send(sender, "display-not-found", Map.of("id", id));
        }
        return data;
    }

    private boolean requireMissing(CommandSender sender, String id) {
        if (!displayManager.exists(id)) {
            return false;
        }
        lang.send(sender, "display-already-exists", Map.of("id", id));
        return true;
    }

    private boolean validateId(CommandSender sender, String id) {
        if (DisplayManager.isValidId(id)) {
            return true;
        }
        lang.send(sender, "invalid-id");
        return false;
    }

    private void sendUsage(CommandSender sender, String usage) {
        lang.send(sender, "usage", Map.of("usage", usage));
    }

    private Double parseDouble(CommandSender sender, String raw) {
        try {
            return Double.parseDouble(raw);
        } catch (NumberFormatException exception) {
            lang.send(sender, "invalid-number", Map.of("value", raw));
            return null;
        }
    }

    private Double parsePositiveDouble(CommandSender sender, String raw) {
        Double value = parseDouble(sender, raw);
        if (value == null) {
            return null;
        }
        if (value <= 0.0D) {
            lang.send(sender, "invalid-number", Map.of("value", raw));
            return null;
        }
        return value;
    }

    private Boolean parseBoolean(CommandSender sender, String raw) {
        if (raw.equalsIgnoreCase("true")) {
            return true;
        }
        if (raw.equalsIgnoreCase("false")) {
            return false;
        }
        lang.send(sender, "invalid-boolean");
        return null;
    }

    private String join(String[] args, int start) {
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            if (i > start) {
                builder.append(' ');
            }
            builder.append(args[i]);
        }
        return builder.toString();
    }

    private String format(double value) {
        if (value == (long) value) {
            return Long.toString((long) value);
        }
        return Double.toString(value);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return filter(SUBCOMMANDS, args[0]);
        }
        if (args.length == 2 && needsId(args[0])) {
            return filter(displayManager.displays().stream().map(DisplayData::id).toList(), args[1]);
        }
        String sub = args[0].toLowerCase(Locale.ROOT);
        if ((sub.equals("setspin") || sub.equals("setbob") || sub.equals("setglow")) && args.length == 3) {
            return filter(List.of("true", "false"), args[2]);
        }
        if (sub.equals("setcollision") && args.length == 3) {
            return filter(List.of("none", "interaction", "barrier"), args[2]);
        }
        if (sub.equals("setcommand") && args.length == 3) {
            return filter(List.of("console", "player"), args[2]);
        }
        if (sub.equals("setpermission") && args.length == 3) {
            return filter(List.of("none", "giantitemdisplays.interact"), args[2]);
        }
        return List.of();
    }

    private boolean needsId(String subcommand) {
        String sub = subcommand.toLowerCase(Locale.ROOT);
        return !sub.equals("list") && !sub.equals("reload") && !sub.equals("credits") && !sub.equals("create") && SUBCOMMANDS.contains(sub);
    }

    private List<String> filter(Collection<String> values, String prefix) {
        String normalized = prefix.toLowerCase(Locale.ROOT);
        List<String> result = new ArrayList<>();
        for (String value : values) {
            if (value.toLowerCase(Locale.ROOT).startsWith(normalized)) {
                result.add(value);
            }
        }
        return result;
    }
}
