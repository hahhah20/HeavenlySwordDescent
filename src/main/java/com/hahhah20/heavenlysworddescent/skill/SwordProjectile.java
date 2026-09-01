package com.hahhah20.heavenlysworddescent.skill;

import com.hahhah20.heavenlysworddescent.HeavenlySwordDescentPlugin;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;

public final class SwordProjectile {
    private final HeavenlySwordDescentPlugin plugin;
    private final Location target;
    private ItemDisplay display;
    private double y;
    private double velocity;

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

    public void spawn() {
        World world = target.getWorld();
        if (world == null) return;

        display = (ItemDisplay) world.spawnEntity(positionAtY(y), EntityType.ITEM_DISPLAY);
        ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = sword.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§f§l天剑降临");
            sword.setItemMeta(meta);
        }
        display.setItemStack(sword);
        display.setBillboard(org.bukkit.entity.Display.Billboard.FIXED);
        display.setBrightness(new org.bukkit.entity.Display.Brightness(15, 15));
        display.setShadowRadius(0);
        display.setShadowStrength(0);
        display.setInterpolationDuration(1);
        display.setTeleportDuration(1);
        transform(1f);
    }

    private void transform(float scale) {
        if (display == null) return;
        Transformation transformation = display.getTransformation();
        transformation.getScale().set(scale, scale, scale * 1.25f);
        transformation.getLeftRotation().set(new Quaternionf().rotateX((float) Math.PI));
        display.setTransformation(transformation);
    }

    public void charge(float progress) {
        transform(1.8f + 2.2f * progress);
    }

    public void beginFall() {
        velocity = plugin.getConfig().getDouble("skill.fall-speed");
    }

    public boolean tickFall() {
        velocity = Math.min(
                plugin.getConfig().getDouble("skill.max-fall-speed"),
                velocity + plugin.getConfig().getDouble("skill.fall-acceleration")
        );
        y -= velocity;
        if (display != null) display.teleport(positionAtY(y));
        return y > target.getY() + 0.8;
    }

    public Location location() {
        return display != null ? display.getLocation() : positionAtY(y);
    }

    public double velocity() {
        return velocity;
    }

    public void remove() {
        if (display != null && !display.isDead()) display.remove();
        display = null;
    }
}
