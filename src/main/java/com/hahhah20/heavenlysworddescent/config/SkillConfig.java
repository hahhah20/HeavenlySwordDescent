package com.hahhah20.heavenlysworddescent.config;

import com.hahhah20.heavenlysworddescent.HeavenlySwordDescentPlugin;

/** Centralized access to skill configuration for V2.2.0. */
public final class SkillConfig {
    private final HeavenlySwordDescentPlugin plugin;

    public SkillConfig(HeavenlySwordDescentPlugin plugin) {
        this.plugin = plugin;
    }

    public int chargeTicks() { return Math.max(1, plugin.getConfig().getInt("skill.charge-ticks", 40)); }
    public double swordHeight() { return plugin.getConfig().getDouble("skill.sword-height", 18.0); }
    public double fallSpeed() { return plugin.getConfig().getDouble("skill.fall-speed", 0.65); }
    public double fallAcceleration() { return plugin.getConfig().getDouble("skill.fall-acceleration", 0.12); }
    public double maxFallSpeed() { return plugin.getConfig().getDouble("skill.max-fall-speed", 4.8); }
    public int targetRange() { return Math.max(1, plugin.getConfig().getInt("skill.target-range", 40)); }
    public double lingerSeconds() { return Math.max(4.0, plugin.getConfig().getDouble("skill.linger.seconds", 4.0)); }
    public int lingerDamageInterval() { return Math.max(1, plugin.getConfig().getInt("skill.linger.damage-interval-ticks", 10)); }
    public double lingerDamageMultiplier() { return plugin.getConfig().getDouble("skill.linger.damage-multiplier", 0.10); }
    public double cooldownSeconds() { return Math.max(0.0, plugin.getConfig().getDouble("skill.cooldown-seconds", 20.0)); }
    public boolean actionbar() { return plugin.getConfig().getBoolean("skill.display.actionbar", true); }
    public boolean chatMessage() { return plugin.getConfig().getBoolean("skill.display.chat-message", false); }
    public int warningPoints() { return Math.max(4, plugin.getConfig().getInt("visual.warning-points", 64)); }
    public int energyOrbits() { return Math.max(1, plugin.getConfig().getInt("visual.energy-orbits", 3)); }
    public int trailParticles() { return Math.max(1, plugin.getConfig().getInt("visual.trail-particles", 10)); }
    public int crackRings() { return Math.max(1, plugin.getConfig().getInt("visual.crack-rings", 4)); }
}
