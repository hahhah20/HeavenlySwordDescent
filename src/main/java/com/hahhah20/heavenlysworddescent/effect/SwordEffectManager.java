package com.hahhah20.heavenlysworddescent.effect;

import com.hahhah20.heavenlysworddescent.HeavenlySwordDescentPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/** Single entry point for the sword's visual effect phases. */
public final class SwordEffectManager {
    private final HeavenlySwordDescentPlugin plugin;

    public SwordEffectManager(HeavenlySwordDescentPlugin plugin) {
        this.plugin = plugin;
    }

    public void charge(Location target, int tick) {
        WarningEffect.tick(plugin, target, tick);
        EnergyEffect.tick(plugin, target, tick);
    }

    public void falling(Location swordLocation, double velocity) {
        SwordTrail.tick(plugin, swordLocation, velocity);
    }

    public void impact(Location target, Player caster) {
        ImpactEffect.execute(plugin, caster, target);
    }

    /** Plays one frame of the staged piercing/settling animation. */
    public void embed(Location target, int tick, int duration) {
        SwordEmbedEffect.tick(plugin, target, tick, duration);
    }

    /** Keeps the grounded sword surrounded by rotating energy rings during its linger window. */
    public void lingering(Location groundCenter, Location swordLocation, int tick, int duration) {
        SwordLingerEffect.tick(plugin, groundCenter, swordLocation, tick, duration);
    }
}
