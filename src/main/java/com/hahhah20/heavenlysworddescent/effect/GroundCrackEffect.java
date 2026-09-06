package com.hahhah20.heavenlysworddescent.effect;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

/** Persistent, readable radial fissures at the sword impact point. */
public final class GroundCrackEffect {
    private GroundCrackEffect() { }

    public static void create(Location center, int rings) {
        World world = center.getWorld();
        if (world == null) return;
        BlockData data = groundData(world, center);
        int rayCount = Math.max(14, Math.min(22, Math.max(2, rings) * 5));

        for (int ray = 0; ray < rayCount; ray++) {
            double angle = Math.PI * 2.0 * ray / rayCount + Math.sin(ray * 1.91) * 0.08;
            double length = 3.2 + (ray % 5) * 0.45;
            drawCrack(world, center, data, angle, 0.35, length, 0.035);

            if (ray % 2 == 0) {
                double branchAngle = angle + (ray % 4 == 0 ? 0.50 : -0.50);
                drawCrack(world, center, data, branchAngle,
                        1.25 + (ray % 3) * 0.25, 2.25 + (ray % 3) * 0.35, 0.030);
            }
        }
    }

    /** Reinforces the fissures during the landed phase without turning them into a circle. */
    public static void tick(Location center, int tick, int rings) {
        World world = center.getWorld();
        if (world == null) return;
        BlockData data = groundData(world, center);
        int rayCount = Math.max(12, Math.min(20, Math.max(2, rings) * 5));
        double rotation = tick * 0.010;

        for (int ray = 0; ray < rayCount; ray++) {
            double angle = Math.PI * 2.0 * ray / rayCount + rotation
                    + Math.sin(ray * 2.4) * 0.05;
            double length = 2.45 + (ray % 4) * 0.52;
            for (double d = 0.42; d <= length; d += 0.28) {
                // Leave irregular gaps so the line reads as a fracture rather than a beam.
                if (((int) (d * 10) + ray + tick / 4) % 7 == 0) continue;
                double wobble = Math.sin(d * 4.2 + ray * 1.7) * 0.12;
                Location p = surface(center, d, angle + wobble, 0.020);

                world.spawnParticle(Particle.BLOCK_CRUMBLE, p, 1,
                        0.035, 0.010, 0.035, 0.012, data);

                // Dark core + small gold edge makes the fissure readable against grass/stone.
                world.spawnParticle(Particle.DUST, p, 2,
                        0.020, 0.006, 0.020, 0.0,
                        new Particle.DustOptions(Color.fromRGB(38, 24, 16), 1.05f));
                if ((ray + (int) (d * 10) + tick) % 5 == 0) {
                    world.spawnParticle(Particle.DUST, p, 1,
                            0.010, 0.004, 0.010, 0.0,
                            new Particle.DustOptions(Color.fromRGB(255, 194, 46), 0.82f));
                }
            }
        }
    }

    private static void drawCrack(World world, Location center, BlockData data,
                                  double angle, double start, double end, double yOffset) {
        for (double d = start; d <= end; d += 0.24) {
            double wobble = Math.sin(d * 3.7 + angle * 5.0) * 0.11;
            Location p = surface(center, d, angle + wobble, yOffset);

            world.spawnParticle(Particle.BLOCK_CRUMBLE, p, 2,
                    0.045, 0.012, 0.045, 0.018, data);
            world.spawnParticle(Particle.DUST, p, 2,
                    0.022, 0.006, 0.022, 0.0,
                    new Particle.DustOptions(Color.fromRGB(38, 24, 16), 1.12f));
            if (((int) (d * 10)) % 6 == 0) {
                world.spawnParticle(Particle.DUST, p, 1,
                        0.010, 0.004, 0.010, 0.0,
                        new Particle.DustOptions(Color.fromRGB(255, 205, 58), 0.86f));
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
