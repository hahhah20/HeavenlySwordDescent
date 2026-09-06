package com.hahhah20.heavenlysworddescent.effect;

import com.hahhah20.heavenlysworddescent.HeavenlySwordDescentPlugin;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;

/** Visual pass for the sword piercing into the ground at the impact point. */
public final class SwordEmbedEffect {
    private SwordEmbedEffect() { }

    public static void execute(HeavenlySwordDescentPlugin plugin, Location center) {
        World world = center.getWorld();
        if (world == null) return;

        Location base = center.clone().subtract(0, 0.9, 0);
        var groundData = world.getBlockAt(base).getBlockData();

        // Tight vertical energy column visually connects the blade to the impact point.
        for (double y = 0.15; y <= 3.6; y += 0.3) {
            world.spawnParticle(Particle.END_ROD, center.clone().add(0, y, 0),
                    2, 0.08, 0.05, 0.08, 0.005);
        }

        // Concentrated ground burst makes the blade read as physically piercing the block.
        world.spawnParticle(Particle.BLOCK, base, 28, 0.65, 0.08, 0.65, 0.12, groundData);
        world.spawnParticle(Particle.CLOUD, base.clone().add(0, 0.15, 0),
                12, 0.45, 0.08, 0.45, 0.035);
        world.spawnParticle(Particle.CRIT, center.clone().add(0, 0.45, 0),
                18, 0.35, 0.35, 0.35, 0.08);

        world.playSound(base, Sound.BLOCK_STONE_BREAK, 2.0f, 0.55f);
        world.playSound(base, Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.4f, 0.65f);
    }
}
