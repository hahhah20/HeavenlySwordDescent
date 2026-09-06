package com.hahhah20.heavenlysworddescent.effect;

import com.hahhah20.heavenlysworddescent.HeavenlySwordDescentPlugin;
import com.hahhah20.heavenlysworddescent.config.SkillConfig;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

public final class SwordTrail {
    private SwordTrail() { }

    public static void tick(HeavenlySwordDescentPlugin plugin, Location location, double speed) {
        World world = location.getWorld();
        if (world == null) return;

        int configured = new SkillConfig(plugin).trailParticles();
        int particles = Math.min(40, configured + (int) (speed * 4));
        world.spawnParticle(Particle.END_ROD, location, particles, .25, .8, .25, .02);
        world.spawnParticle(Particle.CRIT, location, Math.max(1, particles / 2), .2, .6, .2, .05);
    }
}
