package com.hahhah20.heavenlysworddescent.effect;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

/** High-visibility, layered ground-fragment burst for the sword impact. */
public final class DebrisEffect {
    private DebrisEffect() { }

    public static void burst(World world, Location center, int count, double speed) {
        if (world == null || center == null) return;
        BlockData data = sampleGround(world, center);
        int safeCount = Math.max(12, count);

        // Large chunks: few particles, large spacing, and a clear outward/upward arc.
        int chunks = Math.max(10, safeCount / 3);
        for (int i = 0; i < chunks; i++) {
            double angle = Math.PI * 2.0 * i / chunks + Math.sin(i * 1.73) * 0.10;
            double radial = 0.30 + (i % 6) * 0.24;
            Location point = center.clone().add(
                    Math.cos(angle) * radial, 0.18 + (i % 4) * 0.13,
                    Math.sin(angle) * radial);
            world.spawnParticle(Particle.BLOCK, point, 2,
                    0.018, 0.025, 0.018, speed * 1.05, data);
            world.spawnParticle(Particle.BLOCK_CRUMBLE, point, 3,
                    0.028, 0.028, 0.028, speed * 0.92, data);
        }

        // Medium fragments fill the radial arc so the direction reads as physical debris.
        for (int i = 0; i < safeCount; i++) {
            double angle = Math.PI * 2.0 * i / safeCount + Math.sin(i * 1.37) * 0.12;
            double radial = 0.24 + (i % 9) * 0.20;
            double y = 0.10 + (i % 6) * 0.13;
            Location point = center.clone().add(
                    Math.cos(angle) * radial, y, Math.sin(angle) * radial);
            world.spawnParticle(Particle.BLOCK_CRUMBLE, point, 2,
                    0.035, 0.035, 0.035, speed, data);
            world.spawnParticle(Particle.BLOCK, point, 1,
                    0.012, 0.020, 0.012, speed * 1.45, data);
        }

        // Upward crest: a separate silhouette from the ground-hugging shockwave.
        int upward = Math.max(12, safeCount / 2);
        for (int i = 0; i < upward; i++) {
            double angle = Math.PI * 2.0 * i / upward + 0.23;
            double radial = 0.42 + (i % 6) * 0.22;
            double y = 0.34 + (i % 7) * 0.17;
            Location point = center.clone().add(
                    Math.cos(angle) * radial, y, Math.sin(angle) * radial);
            world.spawnParticle(Particle.BLOCK, point, 1,
                    0.012, 0.028, 0.012, speed * 1.95, data);
            world.spawnParticle(Particle.BLOCK_CRUMBLE, point, 2,
                    0.020, 0.035, 0.020, speed * 1.45, data);
        }

        // A restrained dust shell makes the block fragments readable instead of disappearing into grass.
        world.spawnParticle(Particle.CLOUD, center.clone().add(0, 0.22, 0),
                Math.max(8, safeCount / 4), 0.62, 0.14, 0.62, 0.050);
    }

    private static BlockData sampleGround(World world, Location center) {
        Location sample = center.clone().add(0, -1.0, 0);
        var block = world.getBlockAt(sample);
        return block.getType().isSolid()
                ? block.getBlockData()
                : Material.STONE.createBlockData();
    }
}
