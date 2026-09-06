package com.hahhah20.heavenlysworddescent.effect;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

/**
 * Ground fissure VFX for the sword impact.
 *
 * The important detail is that the fissures are anchored to the actual top surface of the
 * terrain. The previous implementation rendered them below the block surface, which made the
 * particles disappear into grass/stone from the player's camera and left only the shockwave visible.
 */
public final class GroundCrackEffect {
    private GroundCrackEffect() { }

    public static void create(Location center, int rings) {
        createStage(center, rings, 7.5);
    }

    /** Draw a visible radial fracture network on top of the actual terrain surface. */
    public static void createStage(Location center, int rings, double maxRadius) {
        World world = center.getWorld();
        if (world == null) return;

        GroundSurface ground = groundSurface(world, center);
        int rayCount = Math.max(12, Math.min(18, Math.max(2, rings) * 4));
        double limit = Math.max(0.45, Math.min(7.5, maxRadius));

        // Dense impact crater: this is deliberately a broken plate, not a circle.
        for (int i = 0; i < 34; i++) {
            double angle = Math.PI * 2.0 * i / 34.0;
            double radius = 0.16 + (i % 8) * 0.10;
            drawSegment(world, ground, radius, angle, 0.13, true);
        }

        for (int ray = 0; ray < rayCount; ray++) {
            double angle = Math.PI * 2.0 * ray / rayCount
                    + Math.sin(ray * 1.91) * 0.13;
            double length = Math.min(limit, 4.2 + (ray % 5) * 0.72);
            drawCrack(world, ground, angle, 0.28, length, true);

            // Main branches create an irregular shattered-ground pattern.
            if (ray % 2 == 0 && limit > 1.3) {
                double branchAngle = angle + (ray % 4 == 0 ? 0.46 : -0.46);
                drawCrack(world, ground, branchAngle,
                        0.95 + (ray % 3) * 0.24,
                        Math.min(limit, 3.0 + (ray % 3) * 0.45), false);
            }
            if (ray % 3 == 0 && limit > 2.2) {
                drawCrack(world, ground, angle - 0.30,
                        1.85, Math.min(limit, 4.25), false);
            }
        }
    }

    /** Re-light the existing fracture network while the sword remains embedded. */
    public static void tick(Location center, int tick, int rings) {
        World world = center.getWorld();
        if (world == null) return;

        GroundSurface ground = groundSurface(world, center);
        int rayCount = Math.max(12, Math.min(18, Math.max(2, rings) * 4));
        boolean flare = tick % 18 == 0 || tick % 18 == 1 || tick % 18 == 2;
        double rotation = tick * 0.0025;

        for (int ray = 0; ray < rayCount; ray++) {
            double angle = Math.PI * 2.0 * ray / rayCount + rotation
                    + Math.sin(ray * 2.4) * 0.075;
            double length = 4.0 + (ray % 4) * 0.68;

            for (double d = 0.25; d <= length; d += 0.12) {
                int segment = (int) Math.floor(d / 0.12);
                if (!flare && (segment + ray + tick / 5) % 11 == 0) continue;

                double wobble = Math.sin(d * 4.7 + ray * 1.7) * 0.15;
                drawSegment(world, ground, d, angle + wobble, 0.10, flare);

                // Short side fracture makes each main fissure read as a cracked surface.
                if (segment % 4 == 0) {
                    double side = angle + Math.PI * 0.5;
                    drawSegment(world, ground, d,
                            angle + wobble + Math.sin(segment * 1.9) * 0.03,
                            0.065, flare);
                    Location sidePoint = ground.point(d, angle + wobble, 0.026);
                    sidePoint.add(Math.cos(side) * 0.095, 0, Math.sin(side) * 0.095);
                    world.spawnParticle(Particle.DUST, sidePoint, 1, 0, 0, 0, 0,
                            new Particle.DustOptions(Color.fromRGB(255, 123, 18),
                                    flare ? 0.92f : 0.72f));
                }
            }
        }
    }

    private static void drawCrack(World world, GroundSurface ground,
                                  double angle, double start, double end, boolean major) {
        double step = major ? 0.12 : 0.14;
        int segment = 0;
        for (double d = start; d <= end; d += step) {
            // Only tiny gaps: the player must read one continuous crack from a distance.
            if ((segment + (int) Math.round(angle * 10.0)) % (major ? 15 : 12) == 0) {
                segment++;
                continue;
            }

            double wobble = Math.sin(d * 4.15 + angle * 5.0) * (major ? 0.17 : 0.14);
            drawSegment(world, ground, d, angle + wobble,
                    major ? 0.115 : 0.095, major);

            // Wider broken edge on major fissures.
            if (major && segment % 3 == 0) {
                double side = angle + Math.PI * 0.5;
                Location edge = ground.point(d, angle + wobble, 0.032);
                edge.add(Math.cos(side) * 0.11, 0, Math.sin(side) * 0.11);
                world.spawnParticle(Particle.DUST, edge, 1, 0, 0, 0, 0,
                        new Particle.DustOptions(Color.fromRGB(255, 130, 18), 0.82f));
            }
            segment++;
        }
    }

    /** Render one precise crack segment instead of a cloud of particles. */
    private static void drawSegment(World world, GroundSurface ground,
                                    double radius, double angle, double width, boolean hot) {
        Location center = ground.point(radius, angle, 0.022);
        double side = angle + Math.PI * 0.5;

        // Two close lines give the fissure a physical width without hiding the terrain.
        Location left = center.clone().add(Math.cos(side) * width, 0, Math.sin(side) * width);
        Location right = center.clone().add(-Math.cos(side) * width, 0, -Math.sin(side) * width);

        world.spawnParticle(Particle.DUST, center, 1, 0, 0, 0, 0,
                new Particle.DustOptions(Color.fromRGB(24, 7, 2), hot ? 2.05f : 1.75f));
        world.spawnParticle(Particle.DUST, left, 1, 0, 0, 0, 0,
                new Particle.DustOptions(Color.fromRGB(255, 120, 15), hot ? 1.15f : 0.92f));
        world.spawnParticle(Particle.DUST, right, 1, 0, 0, 0, 0,
                new Particle.DustOptions(Color.fromRGB(255, 168, 32), hot ? 0.90f : 0.68f));

        if (hot) {
            world.spawnParticle(Particle.END_ROD, center, 1, 0, 0, 0, 0);
        }
    }

    private static GroundSurface groundSurface(World world, Location center) {
        int x = center.getBlockX();
        int z = center.getBlockZ();
        Block block = world.getHighestBlockAt(x, z);
        BlockData data = block.getType().isSolid()
                ? block.getBlockData()
                : Material.STONE.createBlockData();
        return new GroundSurface(center.getX(), center.getZ(), block.getY() + 1.02, data);
    }

    private record GroundSurface(double x, double z, double y, BlockData data) {
        private Location point(double radius, double angle, double offsetY) {
            return new Location(null,
                    x + Math.cos(angle) * radius,
                    y + offsetY,
                    z + Math.sin(angle) * radius);
        }
    }
}
