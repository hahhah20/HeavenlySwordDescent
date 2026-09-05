package com.hahhah20.heavenlysworddescent.model;

import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/** Controls only the visual ItemDisplay model; gameplay state stays elsewhere. */
public final class SwordModelController {
    private final ItemDisplay display;
    private final Quaternionf fixedRotation = new Quaternionf()
            .rotateZ((float) Math.toRadians(135.0));
    private float scale = 1f;

    public SwordModelController(ItemDisplay display) {
        this.display = display;
    }

    public void configure(ItemStack item) {
        display.setItemStack(item.clone());
        display.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.NONE);
        display.setBillboard(Display.Billboard.FIXED);
        display.setBrightness(new Display.Brightness(15, 15));
        display.setShadowRadius(0);
        display.setShadowStrength(0);
        display.setInterpolationDuration(0);
        display.setTeleportDuration(0);
        display.setPersistent(true);
        display.setInvulnerable(true);
        apply();
    }

    public void setScale(float scale) {
        this.scale = scale;
        apply();
    }

    public float scale() {
        return scale;
    }

    public void face(Location from, Location playerLocation) {
        if (display.isDead() || playerLocation == null) return;
        var direction = playerLocation.toVector().subtract(from.toVector());
        direction.setY(0.0);
        if (direction.lengthSquared() < 1.0E-6) return;
        float yaw = (float) Math.toDegrees(Math.atan2(-direction.getX(), direction.getZ()));
        display.setRotation(yaw, 0f);
    }

    public void teleport(Location location) {
        if (!display.isDead()) display.teleport(location);
    }

    public boolean exists() {
        return !display.isDead();
    }

    public void remove() {
        if (!display.isDead()) display.remove();
    }

    private void apply() {
        if (display.isDead()) return;
        display.setTransformation(new Transformation(
                new Vector3f(0f, 0f, 0f),
                new Quaternionf(fixedRotation),
                new Vector3f(scale, scale, scale),
                new Quaternionf().identity()
        ));
    }
}
