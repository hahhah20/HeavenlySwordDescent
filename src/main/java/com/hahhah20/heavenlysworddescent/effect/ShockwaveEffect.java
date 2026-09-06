package com.hahhah20.heavenlysworddescent.effect;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

/** Layered ground energy shockwave that keeps the sword readable at the center. */
public final class ShockwaveEffect {
    private ShockwaveEffect() { }

    public static void create(Location center) {
        World world = center.getWorld();
        if (world == null) return;

        // Strong but separated launch rings; reduced density leaves room for ground fractures.
        drawRing(world, center, 1.0, 0.0, 40, Particle.END_ROD, 0.014);
        drawRing(world, center, 2.35, 0.10, 58, Particle.END_ROD, 0.011);
        drawRing(world, center, 4.75, -0.08, 82, Particle.SOUL_FIRE_FLAME, 0.007);
    }

    /** Persistent three-layer pulse while the sword remains embedded. */
    public static void tick(Location center, int tick) {
        World world = center.getWorld();
        if (world == null) return;

        double phase = tick * 0.080;
        double pulse = 0.16 + 0.14 * (0.5 + 0.5 * Math.sin(tick * 0.18));

        // Inner ring hugs the impact point and frames the sword base.
        drawRing(world, center, 1.55 + pulse, phase, 44, Particle.END_ROD, 0.007);
        // Middle ring is the main shockwave silhouette.
        drawRing(world, center, 3.10 + pulse * 1.3, -phase * 0.8, 62, Particle.END_ROD, 0.008);
        // Outer ring is deliberately sparse and blue-white in character via soul flame.
        drawRing(world, center, 5.00 + pulse * 1.7, phase * 0.55, 82,
                Particle.SOUL_FIRE_FLAME, 0.005);

        // One travelling pulse every 12 ticks; it expands instead of stacking into a white disk.
        if (tick % 12 == 0) {
            double expanding = 0.95 + ((tick / 12) % 6) * 0.82;
            drawRing(world, center, expanding, phase, 38, Particle.END_ROD, 0.010);
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
