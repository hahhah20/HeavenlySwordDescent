package com.hahhah20.heavenlysworddescent.effect;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

/** High-contrast radial fissures that make the impact surface read as cracked ground. */
public final class GroundCrackEffect {
    private GroundCrackEffect() { }

    public static void create(Location center, int rings) {
        createStage(center, rings, 4.8);
    }

    /** Draw the fracture network up to the requested radius, so the cracks visibly grow outward. */
    public static void createStage(Location center, int rings, double maxRadius) {
        World world = center.getWorld();
        if (world == null) return;
        BlockData data = groundData(world, center);
        int rayCount = Math.max(12, Math.min(18, Math.max(2, rings) * 4));
        double limit = Math.max(0.45, Math.min(6.8, maxRadius));

        // Dense fracture origin: the first frame must visibly read as broken ground, not a ring.
        for (int i = 0; i < 30; i++) {
            double angle = Math.PI * 2.0 * i / 30.0;
            double radius = 0.20 + (i % 6) * 0.115;
            crackMark(world, surface(center, radius, angle, 0.115), data, 5, 1.15f, 0.28);
        }

        for (int ray = 0; ray < rayCount; ray++) {
            double angle = Math.PI * 2.0 * ray / rayCount
                    + Math.sin(ray * 1.91) * 0.11;
            double length = Math.min(limit, 3.9 + (ray % 5) * 0.66);
            if (length <= 0.4) continue;

            drawCrack(world, center, data, angle, 0.28, length, true);

            // Split branches make the pattern look like a real fractured surface instead of spokes.
            if (ray % 2 == 0 && limit > 1.35) {
                double branchAngle = angle + (ray % 4 == 0 ? 0.48 : -0.48);
                drawCrack(world, center, data, branchAngle,
                        1.00 + (ray % 3) * 0.22,
                        Math.min(limit, 2.65 + (ray % 3) * 0.38), false);
            }
            if (ray % 3 == 0 && limit > 2.2) {
                drawCrack(world, center, data, angle - 0.30,
                        1.95, Math.min(limit, 3.75), false);
            }
        }
    }

    /** Refreshes the strongest fracture segments while the sword is embedded. */
    public static void tick(Location center, int tick, int rings) {
        World world = center.getWorld();
        if (world == null) return;
        BlockData data = groundData(world, center);
        int rayCount = Math.max(12, Math.min(18, Math.max(2, rings) * 4));
        boolean flare = tick % 18 == 0 || tick % 18 == 1 || tick % 18 == 2;
        double rotation = tick * 0.003;

        for (int ray = 0; ray < rayCount; ray++) {
            double angle = Math.PI * 2.0 * ray / rayCount + rotation
                    + Math.sin(ray * 2.4) * 0.065;
            double length = 3.55 + (ray % 4) * 0.58;

            for (double d = 0.28; d <= length; d += 0.16) {
                int segment = (int) Math.floor(d / 0.16);
                if (!flare && (segment + ray + tick / 5) % 10 == 0) continue;

                double wobble = Math.sin(d * 4.5 + ray * 1.7) * 0.17;
                Location p = surface(center, d, angle + wobble, flare ? 0.120 : 0.105);
                crackMark(world, p, data, flare ? 5 : 4,
                        flare ? 1.18f : 1.02f, flare ? 0.25 : 0.21);

                // Secondary jagged edge makes the fissure visibly wider than a particle dotted line.
                if (segment % 3 == 0) {
                    double side = angle + Math.PI * 0.5;
                    Location edge = p.clone().add(
                            Math.cos(side) * (flare ? 0.075 : 0.060), 0,
                            Math.sin(side) * (flare ? 0.075 : 0.060));
                    world.spawnParticle(Particle.DUST, edge, flare ? 3 : 2,
                            0.018, 0.004, 0.018, 0.0,
                            new Particle.DustOptions(Color.fromRGB(255, 118, 18),
                                    flare ? 0.92f : 0.76f));
                }
            }
        }
    }

    private static void drawCrack(World world, Location center, BlockData data,
                                  double angle, double start, double end, boolean major) {
        int segment = 0;
        double step = major ? 0.16 : 0.18;
        for (double d = start; d <= end; d += step) {
            // Short gaps keep the fracture organic while the dense points still read as one crack.
            if ((segment + (int) Math.round(angle * 10.0)) % (major ? 11 : 9) == 0) {
                segment++;
                continue;
            }

            double wobble = Math.sin(d * 4.0 + angle * 5.0) * (major ? 0.17 : 0.15);
            Location p = surface(center, d, angle + wobble, major ? 0.115 : 0.105);
            crackMark(world, p, data, major ? 5 : 4,
                    major ? 1.15f : 1.00f, major ? 0.25 : 0.21);

            if (segment % (major ? 3 : 4) == 0) {
                double side = angle + Math.PI * 0.5;
                Location edge = p.clone().add(
                        Math.cos(side) * (major ? 0.075 : 0.060), 0,
                        Math.sin(side) * (major ? 0.075 : 0.060));
                world.spawnParticle(Particle.DUST, edge, 2,
                        0.018, 0.004, 0.018, 0.0,
                        new Particle.DustOptions(Color.fromRGB(255, 122, 18), 0.80f));
            }
            segment++;
        }
    }

    /** Render a thick dark fissure, bright molten edge and actual fragments from the sampled ground. */
    private static void crackMark(World world, Location point, BlockData data,
                                  int count, float goldSize, double debrisSpread) {
        // Large dark core is deliberately oversized so the crack remains visible against grass and stone.
        world.spawnParticle(Particle.DUST, point, count + 4,
                0.034, 0.008, 0.034, 0.0,
                new Particle.DustOptions(Color.fromRGB(12, 5, 2), 2.75f));
        world.spawnParticle(Particle.DUST, point.clone().add(0, 0.024, 0), count + 1,
                0.022, 0.005, 0.022, 0.0,
                new Particle.DustOptions(Color.fromRGB(255, 155, 22), goldSize));
        world.spawnParticle(Particle.BLOCK_CRUMBLE, point, Math.max(2, count / 2),
                debrisSpread, 0.018, debrisSpread, 0.010, data);
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
