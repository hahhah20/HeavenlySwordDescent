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

        for (double y = 0; y <= 9; y += .25) {
            world.spawnParticle(Particle.END_ROD, center.clone().add(0, y, 0), 3,
                    .25, .1, .25, .01);
        }

        int crackRings = new SkillConfig(plugin).crackRings();
        GroundCrackEffect.create(center, crackRings);
        ShockwaveEffect.create(center);

        // Immediate, high-visibility rubble spray at the exact impact frame.
        DebrisEffect.burst(world, center, 64, 0.48);
    }
}
