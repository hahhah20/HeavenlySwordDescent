package com.hahhah20.heavenlysworddescent.effect;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

/** Directional block-fragment burst for the sword impact. */
public final class DebrisEffect {
    private DebrisEffect() { }

    public static void burst(World world, Location center, int count, double speed) {
        if (world == null || center == null) return;

        BlockData data = sampleGround(world, center);
        int safeCount = Math.max(1, count);

        // Multiple directional arcs make the debris visibly leave the impact point.
        for (int i = 0; i < safeCount; i++) {
            double angle = Math.PI * 2.0 * i / safeCount + (i % 3) * 0.12;
            double radial = 0.34 + (i % 5) * 0.13;
            double y = 0.18 + (i % 4) * 0.09;
            Location point = center.clone().add(
                    Math.cos(angle) * radial,
                    y,
                    Math.sin(angle) * radial
            );
            world.spawnParticle(Particle.BLOCK, point, 1,
                    0.03, 0.03, 0.03, speed, data);
        }

        // A smaller high-speed layer creates the unmistakable upward spray.
        for (int i = 0; i < Math.max(4, safeCount / 3); i++) {
            double angle = Math.PI * 2.0 * i / Math.max(4, safeCount / 3) + 0.2;
            Location point = center.clone().add(
                    Math.cos(angle) * 0.55,
                    0.32 + (i % 3) * 0.10,
                    Math.sin(angle) * 0.55
            );
            world.spawnParticle(Particle.BLOCK, point, 1,
                    0.02, 0.04, 0.02, speed * 1.25, data);
        }
    }

    private static BlockData sampleGround(World world, Location center) {
        Location sample = center.clone().add(0, -1.0, 0);
        var block = world.getBlockAt(sample);
        return block.getType().isSolid()
                ? block.getBlockData()
                : Material.STONE.createBlockData();
    }
}
