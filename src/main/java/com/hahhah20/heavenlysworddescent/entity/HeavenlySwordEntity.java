package com.hahhah20.heavenlysworddescent.entity;

import com.hahhah20.heavenlysworddescent.HeavenlySwordDescentPlugin;
import com.hahhah20.heavenlysworddescent.model.SwordModelController;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

/** Owns sword gameplay position/physics while delegating visual state to the model controller. */
public final class HeavenlySwordEntity {
    private final HeavenlySwordDescentPlugin plugin;
    private final Location target;
    private final Player facingPlayer;
    private final SwordModelController model;
    private double y;
    private double velocity;
    private Location landedLocation;
    private float currentScale = 1f;
    private boolean landed;

    public HeavenlySwordEntity(HeavenlySwordDescentPlugin plugin, Location target, Player facingPlayer) {
        this.plugin = plugin;
        this.target = target.clone();
        this.target.setYaw(0f);
        this.target.setPitch(0f);
        this.facingPlayer = facingPlayer;
        this.y = target.getY() + plugin.getConfig().getDouble("skill.sword-height");
        this.model = createModel(plugin, target, facingPlayer);
    }

    private SwordModelController createModel(HeavenlySwordDescentPlugin plugin, Location target, Player facingPlayer) {
        World world = target.getWorld();
        if (world == null) return null;
        ItemDisplay display = (ItemDisplay) world.spawnEntity(positionAtY(this.y), EntityType.ITEM_DISPLAY);
        ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = sword.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§f§l天剑降临");
            sword.setItemMeta(meta);
        }
        SwordModelController controller = new SwordModelController(display);
        controller.configure(sword);
        return controller;
    }

    private Location positionAtY(double height) {
        Location location = target.clone();
        location.setY(height);
        location.setYaw(0f);
        location.setPitch(0f);
        return location;
    }

    private void facePlayer() {
        if (model == null || facingPlayer == null || !facingPlayer.isOnline()) return;
        model.face(location(), facingPlayer.getLocation());
    }

    public void spawn() {
        if (model != null) {
            model.setScale(1f);
            facePlayer();
        }
    }

    public void charge(float progress) {
        currentScale = 1.8f + 2.2f * progress;
        if (!landed && model != null) model.setScale(currentScale);
        facePlayer();
    }

    public void beginFall() {
        velocity = plugin.getConfig().getDouble("skill.fall-speed");
        landed = false;
        landedLocation = null;
        if (model != null) model.setScale(currentScale);
        facePlayer();
    }

    public boolean tickFall() {
        if (!exists() || landed) return false;
        velocity = Math.min(plugin.getConfig().getDouble("skill.max-fall-speed"),
                velocity + plugin.getConfig().getDouble("skill.fall-acceleration"));
        y -= velocity;
        if (y <= target.getY() + 0.8) {
            land();
            return false;
        }
        if (model != null) model.teleport(positionAtY(y));
        facePlayer();
        return true;
    }

    public void land() {
        if (!exists()) return;
        velocity = 0.0;
        landed = true;
        double visibleGroundOffset = Math.max(1.5, currentScale * 0.5);
        y = target.getY() + visibleGroundOffset;
        landedLocation = positionAtY(y);
        if (model != null) model.setScale(currentScale);
        if (model != null) model.teleport(landedLocation);
        facePlayer();
    }

    public void keepLanded() {
        if (!landed || !exists() || landedLocation == null) return;
        if (model != null) model.teleport(landedLocation);
        facePlayer();
    }

    public Location location() {
        if (landedLocation != null) return landedLocation.clone();
        return positionAtY(y);
    }

    public double velocity() { return velocity; }
    public boolean exists() { return model != null && model.exists(); }
    public boolean isLanded() { return landed; }

    public void remove() {
        if (model != null) model.remove();
        landed = false;
        landedLocation = null;
    }
}
