package com.hahhah20.heavenlysworddescent.effect;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

/** Ground-level circular energy shockwave: impact burst plus a complete persistent ring. */
public final class ShockwaveEffect {
    private ShockwaveEffect() { }

    public static void create(Location center) {
        World world = center.getWorld();
        if (world == null) return;

        // Initial expanding burst: dense enough to read as a complete ring.
        for (double radius = 0.8; radius <= 7.0; radius += 0.28) {
            int points = Math.max(24, (int) Math.round(radius * 12.0));
            for (int i = 0; i < points; i++) {
                double angle = Math.PI * 2.0 * i / points;
                Location point = ground(center, radius, angle, 0.12);
                world.spawnParticle(Particle.END_ROD, point, 1,
                        0.018, 0.012, 0.018, 0.0);
            }
        }
    }

    /** One complete ring frame, emitted every server tick during the landed phase. */
    public static void tick(Location center, int tick) {
        World world = center.getWorld();
        if (world == null) return;

        double phase = tick * 0.16;
        double pulse = 0.35 + 0.18 * (0.5 + 0.5 * Math.sin(tick * 0.20));

        // Two full concentric rings create the persistent shockwave silhouette.
        drawRing(world, center, 2.2 + pulse, phase, 56, Particle.END_ROD);
        drawRing(world, center, 4.8 + pulse * 1.5, -phase * 0.65, 88, Particle.SOUL_FIRE_FLAME);

        // Every 10 ticks a thin expanding pulse runs through the whole impact field.
        if (tick % 10 == 0) {
            double expanding = 1.4;
            drawRing(world, center, expanding, phase, 36, Particle.END_ROD);
        }
    }

    private static void drawRing(World world, Location center, double radius, double phase,
                                 int points, Particle particle) {
        for (int i = 0; i < points; i++) {
            double angle = Math.PI * 2.0 * i / points + phase;
            Location point = ground(center, radius, angle, 0.10);
            world.spawnParticle(particle, point, 1,
                    0.012, 0.008, 0.012, 0.0);
        }
    }

    private static Location ground(Location center, double radius, double angle, double yOffset) {
        return center.clone().add(
                Math.cos(angle) * radius,
                -0.96 + yOffset,
                Math.sin(angle) * radius
        );
    }
}
