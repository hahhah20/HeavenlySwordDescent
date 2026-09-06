package com.hahhah20.heavenlysworddescent.effect;

import com.hahhah20.heavenlysworddescent.HeavenlySwordDescentPlugin;
import com.hahhah20.heavenlysworddescent.config.SkillConfig;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

/** Visual-only impact effect. Damage is handled by SwordDamageHandler. */
public final class ImpactEffect {
    private ImpactEffect() { }

    public static void execute(HeavenlySwordDescentPlugin plugin, Player caster, Location center) {
        World world = center.getWorld();
        if (world == null) return;

        world.playSound(center, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 3f, .65f);
        world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 3.5f, .7f);
        world.playSound(center, Sound.ENTITY_WARDEN_SONIC_BOOM, 2f, .8f);
        world.spawnParticle(Particle.EXPLOSION_EMITTER, center, 1);
        world.spawnParticle(Particle.FLASH, center, 1);

        // The blade orientation is intentionally untouched. Only impact visuals are changed.
        SwordEmbedEffect.execute(plugin, center);

        // A short vertical energy column marks the exact strike point without hiding the sword.
        for (double y = 0; y <= 9; y += .35) {
            world.spawnParticle(Particle.END_ROD, center.clone().add(0, y, 0), 2,
                    .18, .08, .18, .008);
        }

        int crackRings = new SkillConfig(plugin).crackRings();
        // Impact is deliberately staged instead of drawing every ring/crack on the same frame.
        GroundCrackEffect.createStage(center, crackRings, 0.75);
        ShockwaveEffect.createStage(center, 0);
        DebrisEffect.burst(world, center, 42, 0.44);

        // 0.05s: first shockwave expansion.
        later(plugin, 1, () -> ShockwaveEffect.createStage(center, 1));
        // 0.08s: the first large rubble throw is visually separated from the flash.
        later(plugin, 2, () -> DebrisEffect.burst(world, center, 58, 0.62));
        // 0.12s: fractures visibly grow out from the impact center.
        later(plugin, 2, () -> GroundCrackEffect.createStage(center, crackRings, 1.65));
        later(plugin, 3, () -> GroundCrackEffect.createStage(center, crackRings, 2.55));
        // 0.20s: second shockwave expansion.
        later(plugin, 4, () -> ShockwaveEffect.createStage(center, 2));
        // 0.24s: second debris layer gives the impact a longer physical arc.
        later(plugin, 5, () -> DebrisEffect.burst(world, center, 38, 0.50));
        // 0.30s: final crack reach + outer shockwave edge.
        later(plugin, 6, () -> GroundCrackEffect.createStage(center, crackRings, 4.20));
        later(plugin, 6, () -> ShockwaveEffect.createStage(center, 3));
    }

    private static void later(HeavenlySwordDescentPlugin plugin, long ticks, Runnable task) {
        plugin.getServer().getScheduler().runTaskLater(plugin, task, ticks);
    }
}
