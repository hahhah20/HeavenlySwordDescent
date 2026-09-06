package com.hahhah20.heavenlysworddescent.effect;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

/**
 * High-contrast ground fracture VFX for the sword impact.
 *
 * The fracture is deliberately drawn as thick, jagged, connected line work on the real terrain
 * surface. It is a visual effect only; the terrain itself is never modified.
 */
public final class GroundCrackEffect {
    private GroundCrackEffect() { }

    public static void create(Location center, int rings) {
        createStage(center, rings, 7.5);
    }

    /** Draw the complete fracture network up to the requested radius. */
    public static void createStage(Location center, int rings, double maxRadius) {
        World world = center.getWorld();
        if (world == null) return;

        GroundSurface ground = groundSurface(world, center);
        int rayCount = Math.max(12, Math.min(18, Math.max(2, rings) * 4));
        double limit = Math.max(0.45, Math.min(7.5, maxRadius));

        // Broken impact plate at the sword contact point.
        for (int i = 0; i < 42; i++) {
            double angle = Math.PI * 2.0 * i / 42.0;
            double radius = 0.12 + (i % 9) * 0.095;
            drawCrackSegment(world, ground, radius, angle, 0.16, true);
        }

        for (int ray = 0; ray < rayCount; ray++) {
            double angle = Math.PI * 2.0 * ray / rayCount
                    + Math.sin(ray * 1.73) * 0.15;
            double length = Math.min(limit, 4.4 + (ray % 5) * 0.70);

            // Connected, dense main fissure. Tiny gaps occur only at a few break points.
            drawCrack(world, ground, angle, 0.26, length, true, ray);

            // Large asymmetric branches turn the radial pattern into a shattered surface.
            if (ray % 2 == 0 && limit > 1.4) {
                double branchAngle = angle + (ray % 4 == 0 ? 0.50 : -0.50);
                drawCrack(world, ground, branchAngle,
                        1.00 + (ray % 3) * 0.20,
                        Math.min(limit, 3.25 + (ray % 3) * 0.42), false, ray + 20);
            }
            if (ray % 3 == 0 && limit > 2.3) {
                drawCrack(world, ground, angle - 0.31,
                        2.0, Math.min(limit, 4.7), false, ray + 40);
            }
        }
    }

    /** Keep the fracture network glowing while the sword remains embedded. */
    public static void tick(Location center, int tick, int rings) {
        World world = center.getWorld();
        if (world == null) return;

        GroundSurface ground = groundSurface(world, center);
        int rayCount = Math.max(12, Math.min(18, Math.max(2, rings) * 4));
        boolean flare = tick % 18 == 0 || tick % 18 == 1 || tick % 18 == 2;
        double rotation = tick * 0.0018;

        for (int ray = 0; ray < rayCount; ray++) {
            double angle = Math.PI * 2.0 * ray / rayCount + rotation
                    + Math.sin(ray * 2.4) * 0.075;
            double length = 4.15 + (ray % 4) * 0.62;

            for (double d = 0.25; d <= length; d += 0.105) {
                int segment = (int) Math.floor(d / 0.105);
                // During normal linger only a few pieces dim; on flare all pieces light.
                if (!flare && (segment + ray + tick / 5) % 13 == 0) continue;

                double wobble = Math.sin(d * 4.7 + ray * 1.7) * 0.16;
                drawCrackSegment(world, ground, d, angle + wobble, 0.115, flare);

                // Small side fractures create detail along the main fault line.
                if (segment % 5 == 0 && d > 0.7) {
                    double branchAngle = angle + (segment % 2 == 0 ? 0.72 : -0.72);
                    double branchLength = Math.min(0.85, 0.34 + (segment % 4) * 0.10);
                    for (double b = 0.08; b <= branchLength; b += 0.11) {
                        double branchWobble = Math.sin(b * 9.0 + ray) * 0.09;
                        drawCrackSegment(world, ground, d + b,
                                branchAngle + branchWobble, 0.07, flare);
                    }
                }
            }
        }
    }

    private static void drawCrack(World world, GroundSurface ground,
                                  double angle, double start, double end,
                                  boolean major, int seed) {
        double step = major ? 0.105 : 0.125;
        int segment = 0;
        for (double d = start; d <= end; d += step) {
            // Break only at deterministic fracture points; never enough to make the line disappear.
            if (segment > 3 && (segment + seed) % (major ? 23 : 19) == 0) {
                segment++;
                continue;
            }

            double wobble = Math.sin(d * 4.15 + seed * 0.71) * (major ? 0.19 : 0.16);
            drawCrackSegment(world, ground, d, angle + wobble,
                    major ? 0.14 : 0.105, true);

            // Fork from the crack edge every few segments.
            if (major && segment > 5 && segment % 8 == 0) {
                double branchAngle = angle + (segment % 16 == 0 ? 0.70 : -0.70);
                double branchStart = Math.max(start, d - 0.08);
                double branchEnd = Math.min(end, d + 0.55 + (seed % 3) * 0.12);
                for (double b = branchStart; b <= branchEnd; b += 0.12) {
                    double branchWobble = Math.sin(b * 7.0 + seed) * 0.12;
                    drawCrackSegment(world, ground, b,
                            branchAngle + branchWobble, 0.085, true);
                }
            }
            segment++;
        }
    }

    /** Render a thick dark fissure with a narrow molten core and bright edge. */
    private static void drawCrackSegment(World world, GroundSurface ground,
                                         double radius, double angle,
                                         double width, boolean hot) {
        Location center = ground.point(radius, angle, 0.045);
        double side = angle + Math.PI * 0.5;

        // Broad dark body: the part that makes the crack read against grass/stone.
        world.spawnParticle(Particle.DUST, center, 2, 0, 0, 0, 0,
                new Particle.DustOptions(Color.fromRGB(8, 3, 1), hot ? 3.45f : 3.05f));

        // Offset molten core, intentionally much narrower than the dark body.
        Location core = center.clone().add(
                Math.cos(side) * width * 0.28, 0.018,
                Math.sin(side) * width * 0.28);
        world.spawnParticle(Particle.DUST, core, 1, 0, 0, 0, 0,
                new Particle.DustOptions(Color.fromRGB(255, 91, 8), hot ? 1.55f : 1.20f));

        // Fine golden edge gives the fracture depth rather than a flat orange particle line.
        Location edge = center.clone().add(
                -Math.cos(side) * width * 0.58, 0.026,
                -Math.sin(side) * width * 0.58);
        world.spawnParticle(Particle.DUST, edge, 1, 0, 0, 0, 0,
                new Particle.DustOptions(Color.fromRGB(255, 190, 52), hot ? 0.92f : 0.70f));

        if (hot && radius < 2.8) {
            world.spawnParticle(Particle.END_ROD, center, 1, 0, 0, 0, 0);
        }

        // A tiny crumble fragment sits beside selected crack joints.
        if (((int) Math.floor(radius * 10.0)) % 5 == 0) {
            world.spawnParticle(Particle.BLOCK_CRUMBLE, edge, 1,
                    0.035, 0.012, 0.035, 0.0, ground.data());
        }
    }

    private static GroundSurface groundSurface(World world, Location center) {
        int x = center.getBlockX();
        int z = center.getBlockZ();
        Block block = world.getHighestBlockAt(x, z);
        BlockData data = block.getType().isSolid()
                ? block.getBlockData()
                : Material.STONE.createBlockData();
        return new GroundSurface(world, center.getX(), center.getZ(), block.getY() + 1.035, data);
    }

    private record GroundSurface(World world, double x, double z, double y, BlockData data) {
        private Location point(double radius, double angle, double offsetY) {
            return new Location(world,
                    x + Math.cos(angle) * radius,
                    y + offsetY,
                    z + Math.sin(angle) * radius);
        }
    }
}
