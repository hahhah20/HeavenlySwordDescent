package com.hahhah20.heavenlysworddescent.effect;

import com.hahhah20.heavenlysworddescent.HeavenlySwordDescentPlugin;
import com.hahhah20.heavenlysworddescent.config.SkillConfig;
import org.bukkit.Color;
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
        double fade = 1.0 - progress * 0.30;
        double phase = tick * 0.18;
        SkillConfig config = new SkillConfig(plugin);

        // The impact sequence is scheduled by ImpactEffect itself. Do not replay it here.
        // Replaying it from the linger tick caused a compile-time call to a nonexistent
        // ImpactEffect.tick(...) API and would also duplicate the impact visuals.

        // Four complete rings continuously orbit the sword body. Their radii and heights
        // remain separated so the sword reads as a powered object rather than a particle cloud.
        for (int ringIndex = 0; ringIndex < 4; ringIndex++) {
            double radius = (0.78 + ringIndex * 0.52) * fade;
            double y = groundCenter.getY() + 1.05 + ringIndex * 0.82;
            int points = 48;
            double direction = ringIndex % 2 == 0 ? 1.0 : -1.0;

            for (int i = 0; i < points; i++) {
                double angle = phase * direction
                        + (Math.PI * 2.0 * i / points)
                        + ringIndex * 0.31;
                Location point = new Location(
                        world,
                        swordLocation.getX() + Math.cos(angle) * radius,
                        y + Math.sin(angle * 3.0 + phase) * 0.055,
                        swordLocation.getZ() + Math.sin(angle) * radius
                );
                world.spawnParticle(Particle.END_ROD, point, 1,
                        0.012, 0.012, 0.012, 0.0);

                if ((i + ringIndex + tick) % 4 == 0) {
                    Particle.DustOptions dust = new Particle.DustOptions(
                            Color.fromRGB(255, 224, 92), 0.72f);
                    world.spawnParticle(Particle.DUST, point, 1,
                            0.006, 0.006, 0.006, 0.0, dust);
                }
            }
        }

        // A stronger ground-to-blade energy stream makes the embedded sword feel powered.
        if (tick % 2 == 0) {
            double bladeBaseY = groundCenter.getY() + 0.32;
            double bladeTopY = Math.max(bladeBaseY + 2.0, swordLocation.getY() + 3.05);
            double span = Math.max(1.0, bladeTopY - bladeBaseY);
            for (int i = 0; i < 18; i++) {
                double y = bladeBaseY + ((tick * 0.19 + i * 0.37) % span);
                double angle = phase * 1.35 + i * (Math.PI * 2.0 / 18.0);
                double radius = 0.20 + (i % 5) * 0.07;
                Location mote = new Location(
                        world,
                        swordLocation.getX() + Math.cos(angle) * radius,
                        y,
                        swordLocation.getZ() + Math.sin(angle) * radius
                );
                world.spawnParticle(Particle.END_ROD, mote, 1,
                        0.018, 0.040, 0.018, 0.004);
            }
        }

        // Every half-second, energy visibly erupts from the ground and climbs toward the blade.
        if (tick % 10 == 0) {
            for (int i = 0; i < 10; i++) {
                double angle = phase + i * Math.PI * 2.0 / 10.0;
                double radius = 0.35 + (i % 3) * 0.18;
                double y = groundCenter.getY() + 0.12 + (i % 5) * 0.30;
                Location surge = groundCenter.clone().add(
                        Math.cos(angle) * radius, y - groundCenter.getY(),
                        Math.sin(angle) * radius);
                world.spawnParticle(Particle.END_ROD, surge, 2,
                        0.025, 0.055, 0.025, 0.012);
            }
        }

        // The ground field has a pulse ring and persistent crack glow.
        ShockwaveEffect.tick(groundCenter, tick);
        GroundCrackEffect.tick(groundCenter, tick, config.crackRings());

        // Small fragments fall occasionally after the initial impact, rather than flooding every frame.
        if (tick <= 20 || tick % 16 == 0) {
            DebrisEffect.burst(world, groundCenter, tick <= 20 ? 18 : 6,
                    tick <= 20 ? 0.30 : 0.18);
        }
    }
}
