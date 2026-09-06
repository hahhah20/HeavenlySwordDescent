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

        // Broken center plate: several short, thick fracture tongues radiate from the blade.
        for (int i = 0; i < 24; i++) {
            double angle = Math.PI * 2.0 * i / 24.0;
            double radius = 0.28 + (i % 5) * 0.105;
            Location p = surface(center, radius, angle, 0.075);
            crackMark(world, p, data, 3, 0.82f, 0.28);
        }

        for (int ray = 0; ray < rayCount; ray++) {
            double angle = Math.PI * 2.0 * ray / rayCount
                    + Math.sin(ray * 1.91) * 0.11;
            double length = Math.min(limit, 3.8 + (ray % 5) * 0.64);
            if (length <= 0.4) continue;

            drawCrack(world, center, data, angle, 0.30, length, true);

            // Split branches make the pattern look like a real fractured surface instead of spokes.
            if (ray % 2 == 0 && limit > 1.45) {
                double branchAngle = angle + (ray % 4 == 0 ? 0.48 : -0.48);
                drawCrack(world, center, data, branchAngle,
                        1.05 + (ray % 3) * 0.22,
                        Math.min(limit, 2.55 + (ray % 3) * 0.38), false);
            }
            if (ray % 3 == 0 && limit > 2.2) {
                double branchAngle = angle - 0.30;
                drawCrack(world, center, data, branchAngle,
                        2.0, Math.min(limit, 3.65), false);
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
            double length = 3.35 + (ray % 4) * 0.56;

            for (double d = 0.30; d <= length; d += 0.18) {
                int segment = (int) Math.floor(d / 0.18);
                // Leave deliberate gaps so the line looks broken, while keeping it readable as a crack.
                if (!flare && (segment + ray + tick / 5) % 9 == 0) continue;

                double wobble = Math.sin(d * 4.5 + ray * 1.7) * 0.16;
                Location p = surface(center, d, angle + wobble, flare ? 0.090 : 0.078);
                crackMark(world, p, data, flare ? 4 : 3, flare ? 1.05f : 0.90f, flare ? 0.24 : 0.20);

                // A short secondary edge gives each fissure a jagged width rather than a dotted centerline.
                if (segment % 3 == 0) {
                    double side = angle + Math.PI * 0.5;
                    Location edge = surface(center, d,
                            angle + wobble + Math.sin(segment * 1.9) * 0.025,
                            flare ? 0.065 : 0.055);
                    edge.add(Math.cos(side) * 0.055, 0, Math.sin(side) * 0.055);
                    world.spawnParticle(Particle.DUST, edge, flare ? 2 : 1,
                            0.018, 0.005, 0.018, 0.0,
                            new Particle.DustOptions(Color.fromRGB(255, 146, 30),
                                    flare ? 0.78f : 0.60f));
                }
            }
        }
    }

    private static void drawCrack(World world, Location center, BlockData data,
                                  double angle, double start, double end, boolean major) {
        int segment = 0;
        double step = major ? 0.18 : 0.20;
        for (double d = start; d <= end; d += step) {
            // Keep gaps short enough that the player still sees one continuous fracture.
            if ((segment + (int) Math.round(angle * 10.0)) % (major ? 10 : 8) == 0) {
                segment++;
                continue;
            }

            double wobble = Math.sin(d * 4.0 + angle * 5.0) * (major ? 0.16 : 0.14);
            Location p = surface(center, d, angle + wobble, major ? 0.088 : 0.080);
            crackMark(world, p, data, major ? 4 : 3, major ? 1.02f : 0.88f, major ? 0.24 : 0.20);

            if (segment % (major ? 3 : 4) == 0) {
                double side = angle + Math.PI * 0.5;
                Location edge = surface(center, d, angle + wobble, major ? 0.068 : 0.062);
                edge.add(Math.cos(side) * (major ? 0.065 : 0.05), 0,
                        Math.sin(side) * (major ? 0.065 : 0.05));
                world.spawnParticle(Particle.DUST, edge, 1,
                        0.015, 0.004, 0.015, 0.0,
                        new Particle.DustOptions(Color.fromRGB(255, 145, 26), 0.68f));
            }
            segment++;
        }
    }

    /** Render a visible dark fissure with a hot-gold inner edge and a little broken terrain. */
    private static void crackMark(World world, Location point, BlockData data,
                                  int count, float goldSize, double debrisSpread) {
        world.spawnParticle(Particle.DUST, point, count + 2,
                0.030, 0.006, 0.030, 0.0,
                new Particle.DustOptions(Color.fromRGB(18, 9, 4), 1.95f));
        world.spawnParticle(Particle.DUST, point.clone().add(0, 0.018, 0), count,
                0.020, 0.004, 0.020, 0.0,
                new Particle.DustOptions(Color.fromRGB(255, 172, 36), goldSize));
        world.spawnParticle(Particle.BLOCK_CRUMBLE, point, Math.max(1, count / 2),
                debrisSpread, 0.014, debrisSpread, 0.008, data);
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
