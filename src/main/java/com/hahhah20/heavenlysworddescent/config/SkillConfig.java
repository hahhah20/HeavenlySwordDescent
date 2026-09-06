package com.hahhah20.heavenlysworddescent.config;

import com.hahhah20.heavenlysworddescent.HeavenlySwordDescentPlugin;

/** Centralized access to V2.2.0 split configuration with config.yml fallback. */
public final class SkillConfig {
    private final HeavenlySwordDescentPlugin plugin;
    private final ModuleConfig modules;

    public SkillConfig(HeavenlySwordDescentPlugin plugin) {
        this.plugin = plugin;
        this.modules = plugin.getModuleConfig();
    }

    public int chargeTicks() {
        int fallback = plugin.getConfig().getInt("skill.charge-ticks", 40);
        return Math.max(1, modules.skills().getInt("skill.charge-ticks", fallback));
    }

    public double swordHeight() {
        return modules.sword().getDouble("sword.height", plugin.getConfig().getDouble("skill.sword-height", 18.0));
    }

    public double fallSpeed() {
        return modules.sword().getDouble("sword.fall-speed", plugin.getConfig().getDouble("skill.fall-speed", 0.65));
    }

    public double fallAcceleration() {
        return modules.sword().getDouble("sword.fall-acceleration", plugin.getConfig().getDouble("skill.fall-acceleration", 0.12));
    }

    public double maxFallSpeed() {
        return modules.sword().getDouble("sword.max-fall-speed", plugin.getConfig().getDouble("skill.max-fall-speed", 4.8));
    }

    public float chargeStartScale() {
        return (float) modules.sword().getDouble("sword.model.charge-start-scale", 1.8);
    }

    public float chargeEndScale() {
        return (float) modules.sword().getDouble("sword.model.charge-end-scale", 4.0);
    }

    public int targetRange() {
        int fallback = plugin.getConfig().getInt("skill.target-range", 40);
        return Math.max(1, modules.skills().getInt("skill.target-range", fallback));
    }

    public double lingerSeconds() {
        double fallback = plugin.getConfig().getDouble("skill.linger.seconds", 4.0);
        return Math.max(4.0, modules.skills().getDouble("skill.linger.seconds", fallback));
    }

    public int lingerDamageInterval() {
        int fallback = plugin.getConfig().getInt("skill.linger.damage-interval-ticks", 10);
        return Math.max(1, modules.skills().getInt("skill.linger.damage-interval-ticks", fallback));
    }

    public double lingerDamageMultiplier() {
        double fallback = plugin.getConfig().getDouble("skill.linger.damage-multiplier", 0.10);
        return modules.skills().getDouble("skill.linger.damage-multiplier", fallback);
    }

    public double cooldownSeconds() {
        double fallback = plugin.getConfig().getDouble("skill.cooldown-seconds", 20.0);
        return Math.max(0.0, modules.skills().getDouble("skill.cooldown-seconds", fallback));
    }

    public boolean actionbar() {
        boolean fallback = plugin.getConfig().getBoolean("skill.display.actionbar", true);
        return modules.skills().getBoolean("skill.display.actionbar", fallback);
    }

    public boolean chatMessage() {
        boolean fallback = plugin.getConfig().getBoolean("skill.display.chat-message", false);
        return modules.skills().getBoolean("skill.display.chat-message", fallback);
    }

    public int warningPoints() {
        int fallback = plugin.getConfig().getInt("visual.warning-points", 64);
        return Math.max(4, modules.effects().getInt("visual.warning-points", fallback));
    }

    public int energyOrbits() {
        int fallback = plugin.getConfig().getInt("visual.energy-orbits", 3);
        return Math.max(1, modules.effects().getInt("visual.energy-orbits", fallback));
    }

    public int trailParticles() {
        int fallback = plugin.getConfig().getInt("visual.trail-particles", 10);
        return Math.max(1, modules.effects().getInt("visual.trail-particles", fallback));
    }

    public int crackRings() {
        int fallback = plugin.getConfig().getInt("visual.crack-rings", 4);
        return Math.max(1, modules.effects().getInt("visual.crack-rings", fallback));
    }

    public double coreRadius() { return plugin.getConfig().getDouble("skill.radius.core", 2.0); }
    public double middleRadius() { return plugin.getConfig().getDouble("skill.radius.middle", 3.5); }
    public double outerRadius() { return plugin.getConfig().getDouble("skill.radius.outer", 5.0); }
    public double coreDamage() { return plugin.getConfig().getDouble("skill.damage.core", 80.0); }
    public double middleDamage() { return plugin.getConfig().getDouble("skill.damage.middle", 45.0); }
    public double outerDamage() { return plugin.getConfig().getDouble("skill.damage.outer", 20.0); }
    public boolean damageCaster() { return plugin.getConfig().getBoolean("skill.damage-caster", false); }
    public double knockback() { return plugin.getConfig().getDouble("skill.impact.knockback", 2.2); }
    public double verticalKnockback() { return plugin.getConfig().getDouble("skill.impact.vertical-knockback", 0.55); }
}
