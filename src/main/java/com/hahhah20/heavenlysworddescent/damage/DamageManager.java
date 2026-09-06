package com.hahhah20.heavenlysworddescent.damage;

import com.hahhah20.heavenlysworddescent.HeavenlySwordDescentPlugin;
import com.hahhah20.heavenlysworddescent.config.SkillConfig;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/** Low-level damage implementation used by SwordDamageHandler. */
public final class DamageManager {
    private DamageManager() { }

    public static void damage(HeavenlySwordDescentPlugin plugin, Player caster, Location center) {
        apply(plugin, caster, center, false);
    }

    public static void lingeringDamage(HeavenlySwordDescentPlugin plugin, Player caster, Location center) {
        apply(plugin, caster, center, true);
    }

    private static void apply(HeavenlySwordDescentPlugin plugin, Player caster, Location center, boolean lingering) {
        World world = center.getWorld();
        if (world == null) return;

        SkillConfig config = new SkillConfig(plugin);
        double coreRadius = config.coreRadius();
        double middleRadius = config.middleRadius();
        double outerRadius = config.outerRadius();
        double coreDamage = config.coreDamage();
        double middleDamage = config.middleDamage();
        double outerDamage = config.outerDamage();
        double multiplier = lingering ? config.lingerDamageMultiplier() : 1.0;

        for (Entity entity : world.getNearbyEntities(center, outerRadius, 4, outerRadius)) {
            if (!(entity instanceof LivingEntity living)) continue;
            if (!config.damageCaster() && entity.equals(caster)) continue;

            double distance = living.getLocation().distance(center);
            double damage = distance <= coreRadius
                    ? coreDamage
                    : distance <= middleRadius
                    ? middleDamage
                    : distance <= outerRadius ? outerDamage : 0.0;
            damage *= multiplier;

            if (damage <= 0.0) continue;
            living.damage(damage, caster);

            if (!lingering) {
                Vector knockback = living.getLocation().toVector().subtract(center.toVector());
                if (knockback.lengthSquared() < .001) knockback = new Vector(0, 0, 1);
                knockback.normalize().multiply(config.knockback() * (.5 + .5 * (1 - distance / outerRadius)));
                knockback.setY(config.verticalKnockback());
                living.setVelocity(knockback);
            }
        }
    }
}
