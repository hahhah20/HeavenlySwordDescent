package com.hahhah20.heavenlysworddescent.effect;

import com.hahhah20.heavenlysworddescent.HeavenlySwordDescentPlugin;
import com.hahhah20.heavenlysworddescent.config.SkillConfig;
import org.bukkit.Location;

/** Single entry point for the sword's visual effect phases. */
public final class SwordEffectManager {
    private final HeavenlySwordDescentPlugin plugin;
    private final SkillConfig config;

    public SwordEffectManager(HeavenlySwordDescentPlugin plugin) {
        this.plugin = plugin;
        this.config = new SkillConfig(plugin);
    }

    public void charge(Location target, int tick) {
        WarningEffect.tick(plugin, target, tick);
        EnergyEffect.tick(plugin, target, tick);
    }

    public void falling(Location swordLocation, double velocity) {
        SwordTrail.tick(swordLocation, velocity);
    }

    public void impact(Location target, org.bukkit.entity.Player caster) {
        ImpactEffect.execute(plugin, caster, target);
    }

    public int crackRings() { return config.crackRings(); }
}
