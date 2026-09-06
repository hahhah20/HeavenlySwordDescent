package com.hahhah20.heavenlysworddescent.effect;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

/** Radial ground-fracture effect emitted from the impact point. */
public final class GroundCrackEffect {
    private GroundCrackEffect() { }

    public static void create(Location center, int rings) {
        World world = center.getWorld();
        if (world == null) return;

        Location groundSample = center.clone().add(0, -1.0, 0);
        BlockData groundData = world.getBlockAt(groundSample).getBlockData();
        if (!world.getBlockAt(groundSample).getType().isSolid()) {
            groundData = Material.STONE.createBlockData();
        }

        int rays = Math.max(12, rings * 7);
        for (int ray = 0; ray < rays; ray++) {
            double baseAngle = (Math.PI * 2.0 * ray / rays) + Math.sin(ray * 1.73) * 0.10;
            double length = 2.3 + (ray % 5) * 0.55 + Math.max(0, rings - 3) * 0.18;

            for (double distance = 0.35; distance <= length; distance += 0.28) {
                double wobble = Math.sin(distance * 3.2 + ray * 0.9) * 0.11;
                double angle = baseAngle + wobble;
                Location point = center.clone().add(
                        Math.cos(angle) * distance,
                        0.06,
                        Math.sin(angle) * distance
                );

                world.spawnParticle(Particle.BLOCK, point, 1,
                        0.035, 0.018, 0.035, 0.015, groundData);

                if (((int) (distance * 10) + ray) % 4 == 0) {
                    world.spawnParticle(Particle.SOUL_FIRE_FLAME, point, 1,
                            0.015, 0.01, 0.015, 0.0);
                }
            }
        }

        // Bright outer fracture accents make the crack pattern readable from a distance.
        int visibleRings = Math.max(2, rings);
        for (int ring = 1; ring <= visibleRings; ring++) {
            double radius = 1.25 + ring * 0.75;
            int points = 28 + ring * 4;
            for (int i = 0; i < points; i++) {
                if ((i + ring) % 3 != 0) continue;
                double angle = Math.PI * 2.0 * i / points + ring * 0.17;
                Location point = center.clone().add(
                        Math.cos(angle) * radius,
                        0.08,
                        Math.sin(angle) * radius
                );
                world.spawnParticle(Particle.END_ROD, point, 1,
                        0.02, 0.01, 0.02, 0.0);
            }
        }
    }
}
