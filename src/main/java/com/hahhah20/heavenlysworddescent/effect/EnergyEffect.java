package com.hahhah20.heavenlysworddescent.effect;

import com.hahhah20.heavenlysworddescent.HeavenlySwordDescentPlugin;
import com.hahhah20.heavenlysworddescent.config.SkillConfig;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

public final class EnergyEffect {
    private EnergyEffect() { }

    public static void tick(HeavenlySwordDescentPlugin plugin, Location center, int tick) {
        World world = center.getWorld();
        if (world == null) return;

        SkillConfig config = new SkillConfig(plugin);
        int orbits = Math.min(8, config.energyOrbits());

        for (int ring = 0; ring < orbits; ring++) {
            double height = 10 + ring * 2 + Math.sin(tick * .16 + ring) * .7;
            double radius = 1.1 + ring * .65;
            for (int i = 0; i < 4; i++) {
                double angle = tick * .12 + i * Math.PI / 2 + ring * .8;
                world.spawnParticle(
                        Particle.END_ROD,
                        center.clone().add(Math.cos(angle) * radius, height, Math.sin(angle) * radius),
                        1
                );
            }
        }

        if (tick % 12 == 0) {
            world.spawnParticle(Particle.SOUL_FIRE_FLAME, center.clone().add(0, config.swordHeight(), 0), 2, .3, .1, .3, .01);
        }
    }
}
