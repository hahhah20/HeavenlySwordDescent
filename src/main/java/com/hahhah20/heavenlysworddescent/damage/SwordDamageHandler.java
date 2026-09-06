package com.hahhah20.heavenlysworddescent.damage;

import com.hahhah20.heavenlysworddescent.HeavenlySwordDescentPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/** Dedicated damage facade for Heavenly Sword phases. */
public final class SwordDamageHandler {
    private final HeavenlySwordDescentPlugin plugin;

    public SwordDamageHandler(HeavenlySwordDescentPlugin plugin) {
        this.plugin = plugin;
    }

    public void impact(Player caster, Location center) {
        DamageManager.damage(plugin, caster, center);
    }

    public void lingering(Player caster, Location center) {
        DamageManager.lingeringDamage(plugin, caster, center);
    }
}
