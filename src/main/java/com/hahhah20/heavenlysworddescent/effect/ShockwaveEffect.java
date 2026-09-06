package com.hahhah20.heavenlysworddescent.effect;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

/** Layered ground energy shockwave with a strong impact burst and readable pulse rings. */
public final class ShockwaveEffect {
    private ShockwaveEffect() { }

    public static void create(Location center) {
        World world = center.getWorld();
        if (world == null) return;

        // Three nested launch rings give the landing a clear radial expansion instead of one white flash.
        drawRing(world, center, 1.0, 0.0, 48, Particle.END_ROD, 0.018);
        drawRing(world, center, 2.4, 0.12, 72, Particle.END_ROD, 0.014);
        drawRing(world, center, 4.8, -0.08, 112, Particle.SOUL_FIRE_FLAME, 0.010);
    }

    /** Persistent layered rings emitted every server tick while the sword is landed. */
    public static void tick(Location center, int tick) {
        World world = center.getWorld();
        if (world == null) return;

        double phase = tick * 0.095;
        double pulse = 0.12 + 0.20 * (0.5 + 0.5 * Math.sin(tick * 0.18));

        // Three stable layers keep the energy field visible without becoming a solid white disk.
        drawRing(world, center, 1.55 + pulse, phase, 52, Particle.END_ROD, 0.010);
        drawRing(world, center, 3.15 + pulse * 1.4, -phase * 0.8, 76, Particle.END_ROD, 0.009);
        drawRing(world, center, 5.05 + pulse * 1.8, phase * 0.55, 112,
                Particle.SOUL_FIRE_FLAME, 0.007);

        // A traveling pulse completes a full outward cycle every 30 ticks.
        double expanding = 0.75 + (tick % 30) * 0.19;
        if (expanding <= 6.45) {
            drawRing(world, center, expanding, -phase, 48, Particle.END_ROD, 0.012);
        }
    }

    private static void drawRing(World world, Location center, double radius, double phase,
                                 int points, Particle particle, double spread) {
        for (int i = 0; i < points; i++) {
            double angle = Math.PI * 2.0 * i / points + phase;
            Location point = center.clone().add(
                    Math.cos(angle) * radius, -0.86, Math.sin(angle) * radius);
            world.spawnParticle(particle, point, 1,
                    spread, spread * 0.45, spread, 0.0);
        }
    }
}
