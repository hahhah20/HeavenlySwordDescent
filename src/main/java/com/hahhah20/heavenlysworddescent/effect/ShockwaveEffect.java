package com.hahhah20.heavenlysworddescent.effect;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

/** Staged expanding ground shockwave for the sword impact. */
public final class ShockwaveEffect {
    private ShockwaveEffect() { }

    public static void create(Location center) {
        createStage(center, 3);
    }

    /** Render one impact stage: small launch ring -> main ring -> outer ring. */
    public static void createStage(Location center, int stage) {
        World world = center.getWorld();
        if (world == null) return;
        int safeStage = Math.max(0, Math.min(3, stage));

        if (safeStage >= 0) {
            double radius = 0.72 + safeStage * 0.42;
            drawRing(world, center, radius, safeStage * 0.13, 34 + safeStage * 8,
                    Particle.END_ROD, 0.012);
        }
        if (safeStage >= 1) {
            double radius = 1.65 + safeStage * 0.72;
            drawRing(world, center, radius, -safeStage * 0.10, 48 + safeStage * 10,
                    Particle.END_ROD, 0.010);
        }
        if (safeStage >= 2) {
            double radius = 3.15 + safeStage * 0.55;
            drawRing(world, center, radius, safeStage * 0.07, 62 + safeStage * 10,
                    Particle.SOUL_FIRE_FLAME, 0.006);
        }
        if (safeStage >= 3) {
            // A thin outer rim gives the final shockwave a visible edge instead of a white disk.
            drawRing(world, center, 4.95, -0.12, 78, Particle.SOUL_FIRE_FLAME, 0.004);
        }
    }

    /** Persistent three-layer pulse while the sword remains embedded. */
    public static void tick(Location center, int tick) {
        World world = center.getWorld();
        if (world == null) return;

        double phase = tick * 0.080;
        double pulse = 0.16 + 0.14 * (0.5 + 0.5 * Math.sin(tick * 0.18));
        drawRing(world, center, 1.55 + pulse, phase, 44, Particle.END_ROD, 0.007);
        drawRing(world, center, 3.10 + pulse * 1.3, -phase * 0.8, 62, Particle.END_ROD, 0.008);
        drawRing(world, center, 5.00 + pulse * 1.7, phase * 0.55, 82,
                Particle.SOUL_FIRE_FLAME, 0.005);

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
