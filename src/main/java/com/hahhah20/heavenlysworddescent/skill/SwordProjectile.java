package com.hahhah20.heavenlysworddescent.skill;

import com.hahhah20.heavenlysworddescent.HeavenlySwordDescentPlugin;
import org.bukkit.*;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class SwordProjectile {
    private final HeavenlySwordDescentPlugin plugin;
    private final Location target;
    private ItemDisplay display;
    private ItemDisplay landedDisplay;
    private ItemStack swordItem;
    private double y;
    private double velocity;
    private Location landedLocation;
    private float currentScale = 1f;

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
        Transformation transformation = new Transformation(
                new Vector3f(0f, 0f, 0f),
                new Quaternionf().identity(),
                new Vector3f(scale, scale, scale),
                new Quaternionf().identity()
        );
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
        if (display == null) return;
        Transformation transformation = new Transformation(
                new Vector3f(0f, 0f, 0f), new Quaternionf().identity(),
                new Vector3f(scale, scale, scale), new Quaternionf().identity());
        transformation.getLeftRotation().rotateZ((float) Math.toRadians(135.0));
        display.setTransformation(transformation);
    }

    public void charge(float progress) {
        transform(1.8f + 2.2f * progress);
    }

    public void beginFall() {
        velocity = plugin.getConfig().getDouble("skill.fall-speed");
        landedLocation = null;
    }

    public boolean tickFall() {
        if (display == null || display.isDead()) return false;
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

    /** At impact the falling display is replaced by a dedicated persistent sword display. */
    public void land() {
        y = target.getY() + 0.8;
        landedLocation = positionAtY(y);
        World world = target.getWorld();
        if (world == null) return;

        if (landedDisplay != null && !landedDisplay.isDead()) return;
        landedDisplay = (ItemDisplay) world.spawnEntity(landedLocation, EntityType.ITEM_DISPLAY);
        configureDisplay(landedDisplay, swordItem == null ? new ItemStack(Material.NETHERITE_SWORD) : swordItem, currentScale);

        // Remove only the falling instance. The landed sword is now the persistent sword body.
        if (display != null && !display.isDead()) display.remove();
        display = null;
    }

    public void keepLanded() {
        if (landedDisplay != null && !landedDisplay.isDead() && landedLocation != null) {
            landedDisplay.teleport(landedLocation);
        }
    }

    public Location location() {
        if (landedLocation != null) return landedLocation.clone();
        if (display != null && !display.isDead()) return display.getLocation();
        return positionAtY(y);
    }

    public double velocity() { return velocity; }

    public boolean exists() {
        if (landedDisplay != null && !landedDisplay.isDead()) return true;
        return display != null && !display.isDead();
    }

    public void remove() {
        if (display != null && !display.isDead()) display.remove();
        if (landedDisplay != null && !landedDisplay.isDead()) landedDisplay.remove();
        display = null;
        landedDisplay = null;
        landedLocation = null;
    }
}
