package com.hahhah20.heavenlysworddescent.effect;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

/** Staged, high-contrast radial fissures at the sword impact point. */
public final class GroundCrackEffect {
    private GroundCrackEffect() { }

    public static void create(Location center, int rings) {
        createStage(center, rings, 4.2);
    }

    /** Draw only the portion of the fracture network that has reached this radius. */
    public static void createStage(Location center, int rings, double maxRadius) {
        World world = center.getWorld();
        if (world == null) return;
        BlockData data = groundData(world, center);
        int rayCount = Math.max(12, Math.min(18, Math.max(2, rings) * 4));
        double limit = Math.max(0.45, Math.min(5.0, maxRadius));

        for (int ray = 0; ray < rayCount; ray++) {
            double angle = Math.PI * 2.0 * ray / rayCount + Math.sin(ray * 1.91) * 0.09;
            double length = Math.min(limit, 3.0 + (ray % 4) * 0.55);
            if (length <= 0.4) continue;
            drawCrack(world, center, data, angle, 0.28, length, 0.070, true);

            if (ray % 2 == 0 && limit > 1.25) {
                double branchAngle = angle + (ray % 4 == 0 ? 0.52 : -0.52);
                drawCrack(world, center, data, branchAngle,
                        1.15 + (ray % 3) * 0.25,
                        Math.min(limit, 2.05 + (ray % 3) * 0.30),
                        0.062, false);
            }
        }
    }

    /** Refreshes only the strongest crack segments during the landed phase. */
    public static void tick(Location center, int tick, int rings) {
        World world = center.getWorld();
        if (world == null) return;
        BlockData data = groundData(world, center);
        int rayCount = Math.max(10, Math.min(16, Math.max(2, rings) * 4));
        double rotation = tick * 0.006;

        for (int ray = 0; ray < rayCount; ray++) {
            double angle = Math.PI * 2.0 * ray / rayCount + rotation
                    + Math.sin(ray * 2.4) * 0.06;
            double length = 2.35 + (ray % 4) * 0.48;
            for (double d = 0.42; d <= length; d += 0.30) {
                int segment = (int) Math.floor(d / 0.30);
                if ((segment + ray + tick / 5) % 6 == 0) continue;
                double wobble = Math.sin(d * 4.0 + ray * 1.7) * 0.13;
                Location p = surface(center, d, angle + wobble, 0.055);

                world.spawnParticle(Particle.DUST, p, 3,
                        0.026, 0.006, 0.026, 0.0,
                        new Particle.DustOptions(Color.fromRGB(18, 11, 7), 1.55f));
                if ((segment + ray + tick) % 4 == 0) {
                    world.spawnParticle(Particle.BLOCK_CRUMBLE, p, 1,
                            0.035, 0.016, 0.035, 0.010, data);
                }
                if ((segment + ray + tick) % 9 == 0) {
                    world.spawnParticle(Particle.DUST, p, 1,
                            0.010, 0.003, 0.010, 0.0,
                            new Particle.DustOptions(Color.fromRGB(255, 184, 42), 0.82f));
                }
            }
        }
    }

    private static void drawCrack(World world, Location center, BlockData data,
                                  double angle, double start, double end,
                                  double yOffset, boolean major) {
        int segment = 0;
        for (double d = start; d <= end; d += major ? 0.25 : 0.28) {
            if ((segment + (int) Math.round(angle * 10.0)) % (major ? 7 : 5) == 0) {
                segment++;
                continue;
            }

            double wobble = Math.sin(d * 3.6 + angle * 5.0) * (major ? 0.12 : 0.10);
            Location p = surface(center, d, angle + wobble, yOffset);

            world.spawnParticle(Particle.DUST, p, major ? 4 : 3,
                    0.025, 0.006, 0.025, 0.0,
                    new Particle.DustOptions(Color.fromRGB(18, 11, 7), major ? 1.65f : 1.40f));
            world.spawnParticle(Particle.BLOCK_CRUMBLE, p, major ? 2 : 1,
                    0.045, 0.016, 0.045, 0.012, data);
            if (segment % (major ? 5 : 4) == 0) {
                world.spawnParticle(Particle.DUST, p, 1,
                        0.010, 0.003, 0.010, 0.0,
                        new Particle.DustOptions(Color.fromRGB(255, 198, 48), major ? 0.95f : 0.76f));
            }
            segment++;
        }
    }

    private static Location surface(Location center, double radius, double angle, double yOffset) {
        return center.clone().add(
                Math.cos(angle) * radius,
                -0.86 + yOffset,
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
