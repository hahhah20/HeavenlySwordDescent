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
        createStage(center, rings, 4.8);
    }

    /** Draw only the portion of the fracture network that has reached this radius. */
    public static void createStage(Location center, int rings, double maxRadius) {
        World world = center.getWorld();
        if (world == null) return;
        BlockData data = groundData(world, center);
        int rayCount = Math.max(12, Math.min(18, Math.max(2, rings) * 4));
        double limit = Math.max(0.45, Math.min(6.2, maxRadius));

        // A broken impact plate establishes the fracture origin without drawing a circular ring.
        for (int i = 0; i < 20; i++) {
            double angle = Math.PI * 2.0 * i / 20.0;
            double radius = 0.42 + (i % 4) * 0.11;
            Location p = surface(center, radius, angle, 0.075);
            world.spawnParticle(Particle.BLOCK_CRUMBLE, p, 3,
                    0.055, 0.018, 0.055, 0.025, data);
        }

        for (int ray = 0; ray < rayCount; ray++) {
            double angle = Math.PI * 2.0 * ray / rayCount + Math.sin(ray * 1.91) * 0.11;
            double length = Math.min(limit, 3.6 + (ray % 5) * 0.62);
            if (length <= 0.4) continue;
            drawCrack(world, center, data, angle, 0.34, length, 0.078, true);

            if (ray % 2 == 0 && limit > 1.45) {
                double branchAngle = angle + (ray % 4 == 0 ? 0.50 : -0.50);
                drawCrack(world, center, data, branchAngle,
                        1.25 + (ray % 3) * 0.25,
                        Math.min(limit, 2.35 + (ray % 3) * 0.34),
                        0.070, false);
            }
        }
    }

    /** Refreshes the strongest fracture segments during the landed phase with intermittent flicker. */
    public static void tick(Location center, int tick, int rings) {
        World world = center.getWorld();
        if (world == null) return;
        BlockData data = groundData(world, center);
        int rayCount = Math.max(10, Math.min(16, Math.max(2, rings) * 4));
        boolean flare = tick % 18 == 0 || tick % 18 == 1;
        double rotation = tick * 0.003;

        for (int ray = 0; ray < rayCount; ray++) {
            double angle = Math.PI * 2.0 * ray / rayCount + rotation
                    + Math.sin(ray * 2.4) * 0.065;
            double length = 3.0 + (ray % 4) * 0.52;
            for (double d = 0.38; d <= length; d += 0.26) {
                int segment = (int) Math.floor(d / 0.26);
                // Most frames are restrained; periodic flare frames make the cracks visibly pulse.
                if (!flare && (segment + ray + tick / 5) % 7 == 0) continue;
                double wobble = Math.sin(d * 4.2 + ray * 1.7) * 0.14;
                Location p = surface(center, d, angle + wobble, flare ? 0.085 : 0.060);

                world.spawnParticle(Particle.DUST, p, flare ? 5 : 2,
                        0.028, 0.007, 0.028, 0.0,
                        new Particle.DustOptions(Color.fromRGB(10, 7, 5), flare ? 1.95f : 1.72f));
                if ((segment + ray + tick) % (flare ? 2 : 5) == 0) {
                    world.spawnParticle(Particle.BLOCK_CRUMBLE, p, flare ? 2 : 1,
                            0.038, 0.018, 0.038, 0.012, data);
                }
                if ((segment + ray + tick) % (flare ? 3 : 11) == 0) {
                    world.spawnParticle(Particle.DUST, p, 1,
                            0.012, 0.004, 0.012, 0.0,
                            new Particle.DustOptions(Color.fromRGB(255, 190, 48), flare ? 1.10f : 0.72f));
                }
            }
        }
    }

    private static void drawCrack(World world, Location center, BlockData data,
                                  double angle, double start, double end,
                                  double yOffset, boolean major) {
        int segment = 0;
        for (double d = start; d <= end; d += major ? 0.23 : 0.26) {
            // Keep small gaps, but no longer enough to make the crack read as random particles.
            if ((segment + (int) Math.round(angle * 10.0)) % (major ? 9 : 7) == 0) {
                segment++;
                continue;
            }

            double wobble = Math.sin(d * 3.8 + angle * 5.0) * (major ? 0.14 : 0.12);
            Location p = surface(center, d, angle + wobble, yOffset);

            world.spawnParticle(Particle.DUST, p, major ? 5 : 4,
                    0.022, 0.006, 0.022, 0.0,
                    new Particle.DustOptions(Color.fromRGB(8, 6, 4), major ? 1.95f : 1.65f));
            world.spawnParticle(Particle.BLOCK_CRUMBLE, p, major ? 2 : 1,
                    0.040, 0.014, 0.040, 0.010, data);
            if (segment % (major ? 4 : 3) == 0) {
                world.spawnParticle(Particle.DUST, p, 1,
                        0.010, 0.003, 0.010, 0.0,
                        new Particle.DustOptions(Color.fromRGB(255, 202, 64), major ? 1.05f : 0.82f));
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
