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
        double fade = 1.0 - progress * 0.22;
        double phase = tick * 0.18;
        SkillConfig config = new SkillConfig(plugin);

        // Four complete rings continuously orbit the sword body. Their radii and heights
        // are deliberately separated so they remain readable as individual energy bands.
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

        // Rising energy motes hug the blade so the sword itself remains visually powered.
        if (tick % 2 == 0) {
            double bladeBaseY = groundCenter.getY() + 0.42;
            double bladeTopY = Math.max(bladeBaseY + 2.0, swordLocation.getY() + 2.9);
            double span = Math.max(1.0, bladeTopY - bladeBaseY);
            for (int i = 0; i < 12; i++) {
                double y = bladeBaseY + ((tick * 0.15 + i * 0.49) % span);
                double angle = phase * 1.35 + i * (Math.PI * 2.0 / 12.0);
                double radius = 0.28 + (i % 4) * 0.075;
                Location mote = new Location(
                        world,
                        swordLocation.getX() + Math.cos(angle) * radius,
                        y,
                        swordLocation.getZ() + Math.sin(angle) * radius
                );
                world.spawnParticle(Particle.END_ROD, mote, 1,
                        0.018, 0.035, 0.018, 0.002);
            }
        }

        // The ground field has a complete pulse ring and persistent crack glow.
        ShockwaveEffect.tick(groundCenter, tick);
        GroundCrackEffect.tick(groundCenter, tick, config.crackRings());

        // Strong, visible debris bursts on the first second, then occasional fragments
        // keep the impact site alive without flooding the server for all four seconds.
        if (tick <= 20 || tick % 12 == 0) {
            DebrisEffect.burst(world, groundCenter, tick <= 20 ? 30 : 8,
                    tick <= 20 ? 0.34 : 0.22);
        }
    }
}
