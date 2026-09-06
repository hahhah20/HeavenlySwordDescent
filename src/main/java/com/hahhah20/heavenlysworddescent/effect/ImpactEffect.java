package com.hahhah20.heavenlysworddescent.effect;

import com.hahhah20.heavenlysworddescent.HeavenlySwordDescentPlugin;
import com.hahhah20.heavenlysworddescent.config.SkillConfig;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

/** Timed, visual-only impact sequence. Damage is handled by SwordDamageHandler. */
public final class ImpactEffect {
    private ImpactEffect() { }

    /** 0.00s frame: center impact, white flash and fracture origin. */
    public static void execute(HeavenlySwordDescentPlugin plugin, Player caster, Location center) {
        World world = center.getWorld();
        if (world == null) return;

        world.playSound(center, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 3.0f, .65f);
        world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 3.0f, .74f);
        world.playSound(center, Sound.ENTITY_WARDEN_SONIC_BOOM, 1.6f, .84f);
        world.spawnParticle(Particle.EXPLOSION_EMITTER, center, 1);
        world.spawnParticle(Particle.FLASH, center, 1);

        // The blade orientation is intentionally untouched. Only impact visuals are changed.
        SwordEmbedEffect.execute(plugin, center);

        world.spawnParticle(Particle.DUST, center.clone().add(0, .08, 0), 24,
                .16, .035, .16, 0.0,
                new Particle.DustOptions(Color.fromRGB(255, 236, 150), 1.45f));

        int crackRings = new SkillConfig(plugin).crackRings();
        GroundCrackEffect.createStage(center, crackRings, 0.72);
        ShockwaveEffect.createStage(center, 0);
        DebrisEffect.burst(world, center, 28, 0.40);

        // The rest of the impact is intentionally sequenced instead of stacking on frame 0.
        later(plugin, 1, () -> {
            ShockwaveEffect.createStage(center, 1);
            DebrisEffect.burst(world, center, 44, 0.58);
        });
        later(plugin, 2, () -> {
            GroundCrackEffect.createStage(center, crackRings, 1.65);
            DebrisEffect.burst(world, center, 30, 0.50);
        });
        later(plugin, 3, () -> GroundCrackEffect.createStage(center, crackRings, 2.55));
        later(plugin, 4, () -> {
            ShockwaveEffect.createStage(center, 2);
            GroundCrackEffect.createStage(center, crackRings, 3.35);
            world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 1.2f, .92f);
        });
        later(plugin, 5, () -> DebrisEffect.burst(world, center, 24, 0.38));
        later(plugin, 6, () -> {
            GroundCrackEffect.createStage(center, crackRings, 5.2);
            ShockwaveEffect.createStage(center, 3);
        });
    }

    private static void later(HeavenlySwordDescentPlugin plugin, long ticks, Runnable task) {
        plugin.getServer().getScheduler().runTaskLater(plugin, task, ticks);
    }
}
