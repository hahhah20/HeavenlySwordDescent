package com.hahhah20.heavenlysworddescent.effect;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

/** Complete ground-level energy shockwave with an expanding impact pulse and persistent rings. */
public final class ShockwaveEffect {
    private ShockwaveEffect() { }

    public static void create(Location center) {
        World world = center.getWorld();
        if (world == null) return;
        // One-time impact wave: dense and closed so no visible gaps appear in the ring.
        for (double radius = 0.7; radius <= 7.0; radius += 0.22) {
            int points = Math.max(32, (int) Math.round(radius * 16.0));
            drawRing(world, center, radius, 0.0, points, Particle.END_ROD, 0.018);
        }
    }

    /** Persistent complete rings emitted every server tick while the sword is landed. */
    public static void tick(Location center, int tick) {
        World world = center.getWorld();
        if (world == null) return;

        double phase = tick * 0.11;
        double pulse = 0.18 + 0.16 * (0.5 + 0.5 * Math.sin(tick * 0.17));

        drawRing(world, center, 2.25 + pulse, phase, 72, Particle.END_ROD, 0.010);
        drawRing(world, center, 4.55 + pulse * 1.8, -phase * 0.7, 104, Particle.SOUL_FIRE_FLAME, 0.008);

        // Every half second, send a thin closed pulse from the sword outward.
        if (tick % 10 == 0) {
            double expanding = 0.9 + ((tick / 10) % 7) * 0.9;
            drawRing(world, center, expanding, phase, 56, Particle.END_ROD, 0.014);
        }
    }

    private static void drawRing(World world, Location center, double radius, double phase,
                                 int points, Particle particle, double spread) {
        for (int i = 0; i < points; i++) {
            double angle = Math.PI * 2.0 * i / points + phase;
            Location point = center.clone().add(
                    Math.cos(angle) * radius, -0.86, Math.sin(angle) * radius);
            world.spawnParticle(particle, point, 1,
                    spread, spread * 0.55, spread, 0.0);
        }
    }
}
