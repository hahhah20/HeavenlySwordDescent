package com.hahhah20.heavenlysworddescent.effect;

import com.hahhah20.heavenlysworddescent.HeavenlySwordDescentPlugin;
import com.hahhah20.heavenlysworddescent.config.SkillConfig;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

public final class WarningEffect {
    private WarningEffect() { }

    public static void tick(HeavenlySwordDescentPlugin plugin, Location center, int tick) {
        World world = center.getWorld();
        if (world == null) return;

        SkillConfig config = new SkillConfig(plugin);
        int points = config.warningPoints();
        double maxRadius = config.outerRadius();
        double progress = Math.min(1.0, tick / (double) config.chargeTicks());
        double radius = 1.0 + progress * (maxRadius - 1.0);

        if (tick % 2 != 0) return;
        for (int i = 0; i < points; i++) {
            double angle = Math.PI * 2 * i / points;
            world.spawnParticle(
                    Particle.SOUL_FIRE_FLAME,
                    center.clone().add(Math.cos(angle) * radius, .05, Math.sin(angle) * radius),
                    1
            );
        }

        if (tick % 4 == 0) {
            for (int i = -3; i <= 3; i++) {
                world.spawnParticle(Particle.END_ROD, center.clone().add(i * .6, .08, 0), 1);
                world.spawnParticle(Particle.END_ROD, center.clone().add(0, .08, i * .6), 1);
            }
        }
    }
}
