package com.hahhah20.heavenlysworddescent.effect;

import com.hahhah20.heavenlysworddescent.HeavenlySwordDescentPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;

/** Animated visual pass for the sword piercing and settling into the ground. */
public final class SwordEmbedEffect {
    private SwordEmbedEffect() { }

    public static void execute(HeavenlySwordDescentPlugin plugin, Location center) {
        tick(plugin, center, 0, 1);
    }

    /** One frame of the embed animation. Duration is measured in server ticks. */
    public static void tick(HeavenlySwordDescentPlugin plugin, Location center, int tick, int duration) {
        World world = center.getWorld();
        if (world == null) return;

        double progress = Math.max(0.0, Math.min(1.0, tick / (double) Math.max(1, duration)));
        double radius = 0.65 + progress * 2.7;
        Location base = center.clone().add(0, 0.12, 0);

        int columnCount = Math.max(1, (int) Math.round(5 - progress * 3));
        for (double y = 0.15; y <= 3.6; y += 0.36) {
            world.spawnParticle(Particle.END_ROD, base.clone().add(0, y, 0),
                    columnCount, 0.06 + progress * 0.10, 0.04,
                    0.06 + progress * 0.10, 0.004);
        }

        int points = 24;
        for (int i = 0; i < points; i++) {
            double angle = (Math.PI * 2.0 * i) / points;
            Location ring = base.clone().add(Math.cos(angle) * radius, 0.06, Math.sin(angle) * radius);
            world.spawnParticle(Particle.END_ROD, ring, 1, 0.04, 0.025, 0.04, 0.002);
        }

        Material ground = world.getBlockAt(base).getType();
        if (ground.isSolid() && ground != Material.AIR) {
            int debris = Math.max(2, (int) Math.round(28 * (1.0 - progress * 0.75)));
            world.spawnParticle(Particle.BLOCK, base, debris,
                    0.55 + progress * 0.35, 0.08, 0.55 + progress * 0.35,
                    0.10, world.getBlockAt(base).getBlockData());
        }

        if (tick == 0) {
            world.spawnParticle(Particle.EXPLOSION_EMITTER, base, 1);
            world.spawnParticle(Particle.FLASH, base, 1);
            world.playSound(base, Sound.BLOCK_STONE_BREAK, 2.0f, 0.55f);
            world.playSound(base, Sound.ITEM_TRIDENT_RIPTIDE_1, 1.4f, 0.65f);
        }

        if (tick == duration) {
            world.spawnParticle(Particle.CRIT, base.clone().add(0, 0.45, 0),
                    22, 0.45, 0.25, 0.45, 0.08);
            world.spawnParticle(Particle.CLOUD, base.clone().add(0, 0.15, 0),
                    14, 0.55, 0.08, 0.55, 0.035);
        }
    }
}
