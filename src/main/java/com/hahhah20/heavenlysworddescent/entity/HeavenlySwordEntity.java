package com.hahhah20.heavenlysworddescent.entity;

import com.hahhah20.heavenlysworddescent.HeavenlySwordDescentPlugin;
import com.hahhah20.heavenlysworddescent.skill.SwordProjectile;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/** Gameplay-facing entity boundary for the V2.2.0 sword. */
public final class HeavenlySwordEntity {
    private final SwordProjectile projectile;

    public HeavenlySwordEntity(HeavenlySwordDescentPlugin plugin, Location target, Player facingPlayer) {
        this.projectile = new SwordProjectile(plugin, target, facingPlayer);
    }

    public void spawn() { projectile.spawn(); }
    public void charge(float progress) { projectile.charge(progress); }
    public void beginFall() { projectile.beginFall(); }
    public boolean tickFall() { return projectile.tickFall(); }
    public Location location() { return projectile.location(); }
    public double velocity() { return projectile.velocity(); }
    public boolean exists() { return projectile.exists(); }
    public boolean isLanded() { return projectile.isLanded(); }
    public void keepLanded() { projectile.keepLanded(); }
    public void remove() { projectile.remove(); }
}
