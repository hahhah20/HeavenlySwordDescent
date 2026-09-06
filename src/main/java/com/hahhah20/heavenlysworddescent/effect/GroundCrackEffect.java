package com.hahhah20.heavenlysworddescent.effect;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

/** Persistent ground-fracture visual centered on the sword impact. */
public final class GroundCrackEffect {
    private GroundCrackEffect() { }

    public static void create(Location center, int rings) {
        World world = center.getWorld();
        if (world == null) return;
        BlockData data = groundData(world, center);
        int rayCount = Math.max(16, Math.max(2, rings) * 8);

        // Initial impact: dense, broken radial fractures with branching tips.
        for (int ray = 0; ray < rayCount; ray++) {
            double angle = Math.PI * 2.0 * ray / rayCount + Math.sin(ray * 1.73) * 0.07;
            double length = 2.8 + (ray % 5) * 0.55;
            drawCrack(world, center, data, angle, 0.25, length, 0.055);
            if (ray % 2 == 0) {
                drawCrack(world, center, data, angle + (ray % 4 == 0 ? 0.46 : -0.46),
                        1.0 + (ray % 3) * 0.28, 1.9 + (ray % 3) * 0.35, 0.045);
            }
        }
    }

    /** Refreshes the crack network every few ticks so it reads as an active fissure. */
    public static void tick(Location center, int tick, int rings) {
        World world = center.getWorld();
        if (world == null) return;
        BlockData data = groundData(world, center);
        int rayCount = Math.max(12, Math.min(28, Math.max(2, rings) * 6));
        double rotation = tick * 0.018;

        for (int ray = 0; ray < rayCount; ray++) {
            double angle = Math.PI * 2.0 * ray / rayCount + rotation
                    + Math.sin(ray * 2.1) * 0.045;
            double length = 2.0 + (ray % 5) * 0.46;
            for (double d = 0.35; d <= length; d += 0.22) {
                if (((int) (d * 10) + ray + tick / 3) % 6 == 0) continue;
                double wobble = Math.sin(d * 4.7 + ray * 1.9) * 0.10;
                Location p = surface(center, d, angle + wobble, 0.025);
                world.spawnParticle(Particle.BLOCK_CRUMBLE, p, 1,
                        0.045, 0.018, 0.045, 0.02, data);
                if ((ray + (int) (d * 10) + tick) % 9 == 0) {
                    world.spawnParticle(Particle.DUST, p, 1,
                            0.008, 0.004, 0.008, 0.0,
                            new Particle.DustOptions(Color.fromRGB(255, 196, 48), 0.55f));
                }
            }
        }

        // Four broken outer fissure arcs establish a large impact footprint.
        for (int ring = 1; ring <= Math.max(2, rings); ring++) {
            double radius = 1.35 + ring * 0.72;
            int points = 44 + ring * 6;
            for (int i = 0; i < points; i++) {
                if ((i + ring + tick / 4) % 5 == 0) continue;
                double a = Math.PI * 2.0 * i / points + rotation * (ring % 2 == 0 ? -1 : 1);
                Location p = surface(center, radius, a, 0.035);
                world.spawnParticle(Particle.END_ROD, p, 1,
                        0.018, 0.008, 0.018, 0.0);
            }
        }
    }

    private static void drawCrack(World world, Location center, BlockData data,
                                  double angle, double start, double end, double yOffset) {
        for (double d = start; d <= end; d += 0.22) {
            double wobble = Math.sin(d * 3.8 + angle * 5.0) * 0.105;
            Location p = surface(center, d, angle + wobble, yOffset);
            world.spawnParticle(Particle.BLOCK_CRUMBLE, p, 2,
                    0.045, 0.018, 0.045, 0.018, data);
            if (((int) (d * 10)) % 7 == 0) {
                world.spawnParticle(Particle.DUST, p, 1,
                        0.008, 0.004, 0.008, 0.0,
                        new Particle.DustOptions(Color.fromRGB(255, 210, 64), 0.7f));
            }
        }
    }

    private static Location surface(Location center, double radius, double angle, double yOffset) {
        return center.clone().add(
                Math.cos(angle) * radius,
                -0.99 + yOffset,
                Math.sin(angle) * radius
        );
    }

    private static BlockData groundData(World world, Location center) {
        Location sample = center.clone().add(0, -1.0, 0);
        var block = world.getBlockAt(sample);
        return block.getType().isSolid()
                ? block.getBlockData()
                : Material.STONE.createBlockData();
    }
}
