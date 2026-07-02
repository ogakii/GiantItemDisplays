package me.codex.giantitemdisplays.display;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

public final class DisplayData {
    private final String id;
    private String worldName;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private ItemStack item;
    private double scaleX;
    private double scaleY;
    private double scaleZ;
    private boolean hitboxEnabled;
    private double hitboxWidth;
    private double hitboxHeight;
    private boolean spin;
    private double spinSpeed;
    private boolean bob;
    private double bobHeight;
    private double bobSpeed;
    private CollisionMode collisionMode;
    private boolean glow;
    private String permission;
    private String commandExecutor;
    private String commandValue;
    private final List<BlockLocation> barrierBlocks = new ArrayList<>();

    public DisplayData(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public Location toLocation() {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return null;
        }
        return new Location(world, x, y, z, yaw, pitch);
    }

    public void setLocation(Location location) {
        this.worldName = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }

    public boolean usesInteraction() {
        return hitboxEnabled && collisionMode != CollisionMode.NONE;
    }

    public boolean hasClickPermission() {
        return permission != null && !permission.isBlank() && !permission.equalsIgnoreCase("none");
    }

    public int chunkX() {
        return ((int) Math.floor(x)) >> 4;
    }

    public int chunkZ() {
        return ((int) Math.floor(z)) >> 4;
    }

    public String worldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public double x() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double y() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double z() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public float yaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float pitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public ItemStack item() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public double scaleX() {
        return scaleX;
    }

    public void setScaleX(double scaleX) {
        this.scaleX = scaleX;
    }

    public double scaleY() {
        return scaleY;
    }

    public void setScaleY(double scaleY) {
        this.scaleY = scaleY;
    }

    public double scaleZ() {
        return scaleZ;
    }

    public void setScaleZ(double scaleZ) {
        this.scaleZ = scaleZ;
    }

    public boolean hitboxEnabled() {
        return hitboxEnabled;
    }

    public void setHitboxEnabled(boolean hitboxEnabled) {
        this.hitboxEnabled = hitboxEnabled;
    }

    public double hitboxWidth() {
        return hitboxWidth;
    }

    public void setHitboxWidth(double hitboxWidth) {
        this.hitboxWidth = hitboxWidth;
    }

    public double hitboxHeight() {
        return hitboxHeight;
    }

    public void setHitboxHeight(double hitboxHeight) {
        this.hitboxHeight = hitboxHeight;
    }

    public boolean spin() {
        return spin;
    }

    public void setSpin(boolean spin) {
        this.spin = spin;
    }

    public double spinSpeed() {
        return spinSpeed;
    }

    public void setSpinSpeed(double spinSpeed) {
        this.spinSpeed = spinSpeed;
    }

    public boolean bob() {
        return bob;
    }

    public void setBob(boolean bob) {
        this.bob = bob;
    }

    public double bobHeight() {
        return bobHeight;
    }

    public void setBobHeight(double bobHeight) {
        this.bobHeight = bobHeight;
    }

    public double bobSpeed() {
        return bobSpeed;
    }

    public void setBobSpeed(double bobSpeed) {
        this.bobSpeed = bobSpeed;
    }

    public CollisionMode collisionMode() {
        return collisionMode;
    }

    public void setCollisionMode(CollisionMode collisionMode) {
        this.collisionMode = collisionMode;
    }

    public boolean glow() {
        return glow;
    }

    public void setGlow(boolean glow) {
        this.glow = glow;
    }

    public String permission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String commandExecutor() {
        return commandExecutor;
    }

    public void setCommandExecutor(String commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    public String commandValue() {
        return commandValue;
    }

    public void setCommandValue(String commandValue) {
        this.commandValue = commandValue;
    }

    public List<BlockLocation> barrierBlocks() {
        return barrierBlocks;
    }
}
