package com.hahhah20.heavenlysworddescent.effect;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

/** High-visibility directional ground-fragment burst for the sword impact. */
public final class DebrisEffect {
    private DebrisEffect() { }

    public static void burst(World world, Location center, int count, double speed) {
        if (world == null || center == null) return;
        BlockData data = sampleGround(world, center);
        int safeCount = Math.max(8, count);

        // Dense outward spray: each direction gets several visible block fragments.
        for (int i = 0; i < safeCount; i++) {
            double angle = Math.PI * 2.0 * i / safeCount + Math.sin(i * 1.37) * 0.10;
            double radial = 0.22 + (i % 6) * 0.12;
            double y = 0.10 + (i % 5) * 0.085;
            Location point = center.clone().add(
                    Math.cos(angle) * radial, y, Math.sin(angle) * radial);
            world.spawnParticle(Particle.BLOCK_CRUMBLE, point, 2,
                    0.055, 0.055, 0.055, speed, data);
            world.spawnParticle(Particle.BLOCK, point, 1,
                    0.025, 0.025, 0.025, speed * 1.35, data);
        }

        // Strong upward layer makes the fragments visibly leave the ground instead of
        // looking like a flat cloud at the impact point.
        int upward = Math.max(8, safeCount / 2);
        for (int i = 0; i < upward; i++) {
            double angle = Math.PI * 2.0 * i / upward + 0.23;
            double radial = 0.38 + (i % 4) * 0.16;
            Location point = center.clone().add(
                    Math.cos(angle) * radial,
                    0.30 + (i % 5) * 0.12,
                    Math.sin(angle) * radial);
            world.spawnParticle(Particle.BLOCK_CRUMBLE, point, 2,
                    0.04, 0.06, 0.04, speed * 1.55, data);
        }

        // A few sparks outline the trajectory of the debris.
        world.spawnParticle(Particle.CRIT, center.clone().add(0, 0.28, 0),
                Math.max(10, safeCount / 2), 0.65, 0.38, 0.65, speed * 0.5);
    }

    private static BlockData sampleGround(World world, Location center) {
        Location sample = center.clone().add(0, -1.0, 0);
        var block = world.getBlockAt(sample);
        return block.getType().isSolid()
                ? block.getBlockData()
                : Material.STONE.createBlockData();
    }
}
