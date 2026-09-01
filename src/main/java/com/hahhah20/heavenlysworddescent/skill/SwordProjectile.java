package com.hahhah20.heavenlysworddescent.skill;

import com.hahhah20.heavenlysworddescent.HeavenlySwordDescentPlugin;
import org.bukkit.*;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemDisplayContext;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class SwordProjectile {
    private final HeavenlySwordDescentPlugin plugin;
    private final Location target;
    private ItemDisplay display;
    private ItemStack swordItem;
    private double y;
    private double velocity;
    private Location landedLocation;
    private float currentScale = 1f;
    private boolean landed;

    public SwordProjectile(HeavenlySwordDescentPlugin plugin, Location target) {
        this.plugin = plugin;
        this.target = target.clone();
        this.y = target.getY() + plugin.getConfig().getDouble("skill.sword-height");
    }

    private Location positionAtY(double height) {
        Location location = target.clone();
        location.setY(height);
        return location;
    }

    private void configureDisplay(ItemDisplay d, ItemStack item, float scale) {
        d.setItemStack(item.clone());
        d.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.NONE);
        d.setBillboard(Display.Billboard.FIXED);
        d.setBrightness(new Display.Brightness(15, 15));
        d.setShadowRadius(0);
        d.setShadowStrength(0);
        d.setInterpolationDuration(0);
        d.setTeleportDuration(0);
        setTransform(d, scale);
    }

    private void setTransform(ItemDisplay d, float scale) {
        Transformation transformation = new Transformation(
                new Vector3f(0f, 0f, 0f),
                new Quaternionf().identity(),
                new Vector3f(scale, scale, scale),
                new Quaternionf().identity()
        );
        // Keep the tested V2.1.3/V2.1.7 sword orientation.
        transformation.getLeftRotation().rotateZ((float) Math.toRadians(135.0));
        d.setTransformation(transformation);
    }

    public void spawn() {
        World world = target.getWorld();
        if (world == null) return;
        swordItem = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = swordItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§f§l天剑降临");
            swordItem.setItemMeta(meta);
        }
        display = (ItemDisplay) world.spawnEntity(positionAtY(y), EntityType.ITEM_DISPLAY);
        configureDisplay(display, swordItem, 1f);
    }

    private void transform(float scale) {
        currentScale = scale;
        if (display == null || display.isDead() || landed) return;
        setTransform(display, scale);
    }

    public void charge(float progress) {
        transform(1.8f + 2.2f * progress);
    }

    public void beginFall() {
        velocity = plugin.getConfig().getDouble("skill.fall-speed");
        landed = false;
        landedLocation = null;
    }

    public boolean tickFall() {
        if (display == null || display.isDead()) return false;
        if (landed) return false;
        velocity = Math.min(plugin.getConfig().getDouble("skill.max-fall-speed"),
                velocity + plugin.getConfig().getDouble("skill.fall-acceleration"));
        y -= velocity;
        if (y <= target.getY() + 0.8) {
            land();
            return false;
        }
        display.teleport(positionAtY(y));
        return true;
    }

    /**
     * Impact is deliberately non-destructive: the SAME ItemDisplay becomes the
     * persistent sword body. No second display is spawned and no display is
     * removed here.
     */
    public void land() {
        if (display == null || display.isDead()) return;
        y = target.getY() + 0.8;
        landedLocation = positionAtY(y);
        landed = true;
        velocity = 0.0;

        display.setInterpolationDuration(0);
        display.setTeleportDuration(0);
        setTransform(display, currentScale);
        display.teleport(landedLocation);
    }

    /** Keep the ORIGINAL ItemDisplay alive and exactly at the impact point. */
    public void keepLanded() {
        if (!landed || display == null || display.isDead() || landedLocation == null) return;
        display.teleport(landedLocation);
    }

    public Location location() {
        if (landedLocation != null) return landedLocation.clone();
        if (display != null && !display.isDead()) return display.getLocation();
        return positionAtY(y);
    }

    public double velocity() { return velocity; }

    public boolean exists() {
        return display != null && !display.isDead();
    }

    public boolean isLanded() { return landed; }

    public void remove() {
        if (display != null && !display.isDead()) display.remove();
        display = null;
        landedLocation = null;
        landed = false;
    }
}
