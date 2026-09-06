package com.hahhah20.heavenlysworddescent.effect;

import com.hahhah20.heavenlysworddescent.HeavenlySwordDescentPlugin;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

/** Continuous visual effect while the landed sword remains embedded in the ground. */
public final class SwordLingerEffect {
    private SwordLingerEffect() { }

    public static void tick(HeavenlySwordDescentPlugin plugin,
                            Location groundCenter,
                            Location swordLocation,
                            int tick,
                            int duration) {
        World world = groundCenter.getWorld();
        if (world == null) return;

        double progress = Math.max(0.0, Math.min(1.0, tick / (double) Math.max(1, duration)));
        double fade = 1.0 - progress * 0.45;
        double phase = tick * 0.18;

        // Three horizontally rotating energy rings around the embedded blade.
        for (int ringIndex = 0; ringIndex < 3; ringIndex++) {
            double radius = (0.95 + ringIndex * 0.62) * fade;
            double y = groundCenter.getY() + 0.95 + ringIndex * 0.72;
            int points = 20;

            for (int i = 0; i < points; i++) {
                double angle = phase * (ringIndex % 2 == 0 ? 1.0 : -1.0)
                        + (Math.PI * 2.0 * i / points)
                        + ringIndex * 0.45;
                Location point = new Location(
                        world,
                        groundCenter.getX() + Math.cos(angle) * radius,
                        y + Math.sin(angle * 2.0) * 0.06,
                        groundCenter.getZ() + Math.sin(angle) * radius
                );
                world.spawnParticle(Particle.END_ROD, point, 1, 0.015, 0.015, 0.015, 0.0);
            }
        }

        // Vertical energy motes keep climbing along the sword instead of the model looking dead.
        if (tick % 2 == 0) {
            double bladeBaseY = groundCenter.getY() + 0.35;
            double bladeTopY = Math.max(bladeBaseY + 2.0, swordLocation.getY() + 2.8);
            for (int i = 0; i < 8; i++) {
                double y = bladeBaseY + ((tick * 0.13 + i * 0.57) % Math.max(1.0, bladeTopY - bladeBaseY));
                double angle = phase + i * (Math.PI * 2.0 / 8.0);
                double radius = 0.32 + (i % 3) * 0.08;
                Location mote = new Location(
                        world,
                        swordLocation.getX() + Math.cos(angle) * radius,
                        y,
                        swordLocation.getZ() + Math.sin(angle) * radius
                );
                world.spawnParticle(Particle.END_ROD, mote, 1, 0.02, 0.04, 0.02, 0.002);
            }
        }

        // A pulsing ground energy ring remains visible through the entire 4-second linger.
        if (tick % 4 == 0) {
            double radius = 2.0 + 0.28 * Math.sin(tick * 0.24);
            int points = 28;
            for (int i = 0; i < points; i++) {
                double angle = Math.PI * 2.0 * i / points + phase * 0.35;
                Location point = groundCenter.clone().add(
                        Math.cos(angle) * radius,
                        0.08,
                        Math.sin(angle) * radius
                );
                world.spawnParticle(Particle.SOUL_FIRE_FLAME, point, 1, 0.01, 0.01, 0.01, 0.0);
            }
        }
    }
}
