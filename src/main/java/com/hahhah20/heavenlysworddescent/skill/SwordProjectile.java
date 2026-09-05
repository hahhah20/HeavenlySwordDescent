package com.hahhah20.heavenlysworddescent.skill;

import com.hahhah20.heavenlysworddescent.HeavenlySwordDescentPlugin;
import com.hahhah20.heavenlysworddescent.model.SwordModelController;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

/**
 * Legacy-compatible projectile implementation used by the Alpha1 entity boundary.
 * Gameplay movement is kept here until the full entity migration is complete.
 */
public final class SwordProjectile {
    private final HeavenlySwordDescentPlugin plugin;
    private final Location target;
    private final Player facingPlayer;
    private ItemDisplay display;
    private ItemStack swordItem;
    private SwordModelController model;
    private double y;
    private double velocity;
    private Location landedLocation;
    private float currentScale = 1f;
    private boolean landed;

    public SwordProjectile(HeavenlySwordDescentPlugin plugin, Location target, Player facingPlayer) {
        this.plugin = plugin;
        this.target = target.clone();
        this.target.setYaw(0f);
        this.target.setPitch(0f);
        this.facingPlayer = facingPlayer;
        this.y = target.getY() + plugin.getConfig().getDouble("skill.sword-height");
    }

    private Location positionAtY(double height) {
        Location location = target.clone();
        location.setY(height);
        location.setYaw(0f);
        location.setPitch(0f);
        return location;
    }

    private void facePlayer() {
        if (model != null && model.exists() && facingPlayer != null && facingPlayer.isOnline()) {
            model.face(display.getLocation(), facingPlayer.getLocation());
        }
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
        model = new SwordModelController(display);
        model.configure(swordItem);
        model.setScale(1f);
        facePlayer();
    }

    private void transform(float scale) {
        currentScale = scale;
        if (display == null || display.isDead() || landed || model == null) return;
        model.setScale(scale);
        facePlayer();
    }

    public void charge(float progress) {
        transform(1.8f + 2.2f * progress);
    }

    public void beginFall() {
        velocity = plugin.getConfig().getDouble("skill.fall-speed");
        landed = false;
        landedLocation = null;
        if (model != null) model.setScale(currentScale);
        facePlayer();
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
        facePlayer();
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
        if (model != null) model.setScale(currentScale);
        display.teleport(landedLocation);
        facePlayer();
    }

    public void keepLanded() {
        if (!landed || display == null || display.isDead() || landedLocation == null) return;
        display.teleport(landedLocation);
        facePlayer();
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
        if (model != null) model.remove();
        else if (display != null && !display.isDead()) display.remove();
        display = null;
        model = null;
        landedLocation = null;
        landed = false;
    }
}
