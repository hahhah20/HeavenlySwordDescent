package com.hahhah20.heavenlysworddescent.damage;

import com.hahhah20.heavenlysworddescent.HeavenlySwordDescentPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public final class DamageManager {
    public static void damage(HeavenlySwordDescentPlugin p, Player caster, Location c) {
        apply(p, caster, c, false);
    }

    /** Applies the normal impact damage once, including knockback. */
    private static void apply(HeavenlySwordDescentPlugin p, Player caster, Location c, boolean lingering) {
        double cr = p.getConfig().getDouble("skill.radius.core");
        double mr = p.getConfig().getDouble("skill.radius.middle");
        double or = p.getConfig().getDouble("skill.radius.outer");
        double cd = p.getConfig().getDouble("skill.damage.core");
        double md = p.getConfig().getDouble("skill.damage.middle");
        double od = p.getConfig().getDouble("skill.damage.outer");
        double multiplier = lingering ? p.getConfig().getDouble("skill.linger.damage-multiplier", 0.10) : 1.0;
        boolean self = p.getConfig().getBoolean("skill.damage-caster");

        for (Entity e : c.getWorld().getNearbyEntities(c, or, 4, or)) {
            if (!(e instanceof LivingEntity le) || (!self && e.equals(caster))) continue;
            double d = le.getLocation().distance(c);
            double dmg = d <= cr ? cd : d <= mr ? md : d <= or ? od : 0;
            dmg *= multiplier;
            if (dmg <= 0) continue;
            le.damage(dmg, caster);

            // Only the initial impact knocks targets back. The lingering damage
            // is deliberately stable so targets are not repeatedly juggled.
            if (!lingering) {
                Vector v = le.getLocation().toVector().subtract(c.toVector());
                if (v.lengthSquared() < .001) v = new Vector(0, 0, 1);
                v.normalize().multiply(p.getConfig().getDouble("skill.impact.knockback") * (.5 + .5 * (1 - d / or)));
                v.setY(p.getConfig().getDouble("skill.impact.vertical-knockback"));
                le.setVelocity(v);
            }
        }
    }

    /** Applies configurable periodic damage while the sword remains at the impact point. */
    public static void lingeringDamage(HeavenlySwordDescentPlugin p, Player caster, Location c) {
        apply(p, caster, c, true);
    }
}
