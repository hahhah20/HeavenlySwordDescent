package com.hahhah20.heavenlysworddescent.effect;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

/** Radial ground-fracture effect emitted from and sustained at the impact point. */
public final class GroundCrackEffect {
    private GroundCrackEffect() { }

    public static void create(Location center, int rings) {
        World world = center.getWorld();
        if (world == null) return;

        BlockData groundData = groundData(world, center);
        int visibleRings = Math.max(2, rings);
        int rays = Math.max(16, visibleRings * 8);

        for (int ray = 0; ray < rays; ray++) {
            double baseAngle = (Math.PI * 2.0 * ray / rays) + Math.sin(ray * 1.73) * 0.08;
            double length = 2.4 + (ray % 5) * 0.58 + Math.max(0, visibleRings - 3) * 0.18;
            spawnCrackPath(world, center, groundData, baseAngle, 0.35, length, 0);

            if (ray % 3 == 0) {
                double branchStart = 1.05 + (ray % 4) * 0.25;
                double branchAngle = baseAngle + (ray % 2 == 0 ? 0.48 : -0.48);
                spawnCrackPath(world, center, groundData, branchAngle, branchStart,
                        Math.min(length, branchStart + 1.45), 1);
            }
        }

        for (int ring = 1; ring <= visibleRings; ring++) {
            double radius = 1.15 + ring * 0.78;
            int points = 36 + ring * 5;
            for (int i = 0; i < points; i++) {
                if ((i + ring) % 3 != 0) continue;
                double angle = Math.PI * 2.0 * i / points + ring * 0.19;
                Location point = surface(center, radius, angle, 0.025);
                world.spawnParticle(Particle.END_ROD, point, 1,
                        0.025, 0.012, 0.025, 0.0);
            }
        }
    }

    /** Continuous glow pass used while the sword remains embedded. */
    public static void tick(Location center, int tick, int rings) {
        World world = center.getWorld();
        if (world == null) return;

        BlockData groundData = groundData(world, center);
        int rays = Math.max(12, Math.min(24, Math.max(2, rings) * 4));
        double rotation = tick * 0.035;

        for (int ray = 0; ray < rays; ray++) {
            double baseAngle = Math.PI * 2.0 * ray / rays + rotation;
            double length = 1.5 + (ray % 5) * 0.48;
            for (double distance = 0.55; distance <= length; distance += 0.34) {
                int segment = (int) Math.floor(distance * 3.0);
                if ((segment + ray + tick / 4) % 5 == 0) continue;
                double wobble = Math.sin(distance * 4.1 + ray * 1.7) * 0.09;
                Location point = surface(center, distance, baseAngle + wobble, 0.035);
                world.spawnParticle(Particle.BLOCK, point, 1,
                        0.025, 0.012, 0.025, 0.008, groundData);
                if ((segment + ray + tick) % 7 == 0) {
                    world.spawnParticle(Particle.SOUL_FIRE_FLAME, point, 1,
                            0.008, 0.004, 0.008, 0.0);
                }
            }
        }
    }

    private static void spawnCrackPath(World world, Location center, BlockData groundData,
                                       double baseAngle, double start, double end, int branch) {
        for (double distance = start; distance <= end; distance += 0.26) {
            double wobble = Math.sin(distance * 3.2 + branch * 2.1) * (branch == 0 ? 0.12 : 0.08);
            Location point = surface(center, distance, baseAngle + wobble, 0.045);
            world.spawnParticle(Particle.BLOCK, point, 1,
                    0.045, 0.015, 0.045, 0.018, groundData);
            if (((int) (distance * 10) + branch) % 5 == 0) {
                world.spawnParticle(Particle.SOUL_FIRE_FLAME, point, 1,
                        0.012, 0.006, 0.012, 0.0);
            }
        }
    }

    private static Location surface(Location center, double radius, double angle, double yOffset) {
        return center.clone().add(
                Math.cos(angle) * radius,
                -0.96 + yOffset,
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
