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
    private ItemStack swordItem;
    private double y;
    private double velocity;
    private Location landedLocation;
    private float currentScale = 1f;
    private boolean landed;

    // The vanilla handheld sword model is diagonal in the raw ItemDisplay plane.
    // 135 degrees made the blade vertical but left the tip pointing upward.
    // Rotate the same axis by another 180 degrees: -45 degrees (315 degrees)
    // keeps the blade vertical and flips it so the sword tip points downward.
    // Entity yaw/pitch remains zero so the caster's camera never affects it.
    private final Quaternionf fixedSwordRotation = new Quaternionf()
            .rotateZ((float) Math.toRadians(-45.0));

    public SwordProjectile(HeavenlySwordDescentPlugin plugin, Location target) {
        this.plugin = plugin;
        this.target = target.clone();
        this.target.setYaw(0f);
        this.target.setPitch(0f);
        this.y = target.getY() + plugin.getConfig().getDouble("skill.sword-height");
    }

    private Location positionAtY(double height) {
        Location location = target.clone();
        location.setY(height);
        location.setYaw(0f);
        location.setPitch(0f);
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
        d.setPersistent(true);
        d.setInvulnerable(true);
        d.setRotation(0f, 0f);
        setTransform(d, scale);
    }

    private void setTransform(ItemDisplay d, float scale) {
        Transformation transformation = new Transformation(
                new Vector3f(0f, 0f, 0f),
                new Quaternionf(fixedSwordRotation),
                new Vector3f(scale, scale, scale),
                new Quaternionf().identity()
        );
        d.setTransformation(transformation);
        d.setRotation(0f, 0f);
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
        if (display != null && !display.isDead()) setTransform(display, currentScale);
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
        display.setRotation(0f, 0f);
        return true;
    }

    public void land() {
        if (display == null || display.isDead()) return;
        velocity = 0.0;
        landed = true;
        double visibleGroundOffset = Math.max(1.5, currentScale * 0.5);
        y = target.getY() + visibleGroundOffset;
        landedLocation = positionAtY(y);
        display.setInterpolationDuration(0);
        display.setTeleportDuration(0);
        display.setPersistent(true);
        display.setInvulnerable(true);
        setTransform(display, currentScale);
        display.teleport(landedLocation);
        display.setRotation(0f, 0f);
    }

    public void keepLanded() {
        if (!landed || display == null || display.isDead() || landedLocation == null) return;
        display.teleport(landedLocation);
        display.setRotation(0f, 0f);
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
