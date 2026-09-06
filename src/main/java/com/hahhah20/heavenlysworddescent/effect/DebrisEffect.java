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
        int safeCount = Math.max(12, count);

        // Low arc: block fragments visibly travel outward from the blade strike.
        for (int i = 0; i < safeCount; i++) {
            double angle = Math.PI * 2.0 * i / safeCount + Math.sin(i * 1.37) * 0.12;
            double radial = 0.18 + (i % 7) * 0.15;
            double y = 0.10 + (i % 5) * 0.10;
            Location point = center.clone().add(
                    Math.cos(angle) * radial, y, Math.sin(angle) * radial);

            world.spawnParticle(Particle.BLOCK_CRUMBLE, point, 3,
                    0.045, 0.045, 0.045, speed, data);
            world.spawnParticle(Particle.BLOCK, point, 1,
                    0.018, 0.018, 0.018, speed * 1.55, data);
        }

        // Tall spray: a second layer gives the impact a clear upward eruption silhouette.
        int upward = Math.max(10, safeCount / 2);
        for (int i = 0; i < upward; i++) {
            double angle = Math.PI * 2.0 * i / upward + 0.23;
            double radial = 0.35 + (i % 5) * 0.18;
            double y = 0.28 + (i % 6) * 0.15;
            Location point = center.clone().add(
                    Math.cos(angle) * radial, y, Math.sin(angle) * radial);
            world.spawnParticle(Particle.BLOCK, point, 1,
                    0.015, 0.035, 0.015, speed * 1.85, data);
            world.spawnParticle(Particle.BLOCK_CRUMBLE, point, 2,
                    0.025, 0.045, 0.025, speed * 1.35, data);
        }

        // Dust cloud separates the fragments from the bright shockwave behind them.
        world.spawnParticle(Particle.CLOUD, center.clone().add(0, 0.24, 0),
                Math.max(8, safeCount / 3), 0.55, 0.16, 0.55, 0.055);
    }

    private static BlockData sampleGround(World world, Location center) {
        Location sample = center.clone().add(0, -1.0, 0);
        var block = world.getBlockAt(sample);
        return block.getType().isSolid()
                ? block.getBlockData()
                : Material.STONE.createBlockData();
    }
}
