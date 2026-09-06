package com.hahhah20.heavenlysworddescent.skill;

import com.hahhah20.heavenlysworddescent.HeavenlySwordDescentPlugin;
import com.hahhah20.heavenlysworddescent.config.SkillConfig;
import com.hahhah20.heavenlysworddescent.damage.SwordDamageHandler;
import com.hahhah20.heavenlysworddescent.effect.SwordEffectManager;
import com.hahhah20.heavenlysworddescent.entity.HeavenlySwordEntity;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * V2.2.0 skill orchestrator. Owns targeting and phase transitions while
 * delegating entity, model, effect and damage work to dedicated modules.
 */
public final class HeavenlySwordSkill {
    private final HeavenlySwordDescentPlugin plugin;
    private final Player caster;
    private final Runnable done;
    private final SkillConfig config;
    private final SwordEffectManager effects;
    private final SwordDamageHandler damage;

    private Location target;
    private HeavenlySwordEntity sword;
    private SwordState state;
    private int tick;
    private int lingerTick;
    private long lingerEndNanos;
    private boolean finished;
    private BukkitRunnable task;

    public HeavenlySwordSkill(HeavenlySwordDescentPlugin plugin, Player caster, Runnable done) {
        this.plugin = plugin;
        this.caster = caster;
        this.done = done;
        this.config = new SkillConfig(plugin);
        this.effects = new SwordEffectManager(plugin);
        this.damage = new SwordDamageHandler(plugin);
    }

    public void start() {
        state = SwordState.TARGET_LOCK;
        acquireTarget();

        sword = new HeavenlySwordEntity(plugin, target, caster);
        sword.spawn();
        state = SwordState.CHARGING;

        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (finished) {
                    cancel();
                    return;
                }
                if (!caster.isOnline()) {
                    finish();
                    cancel();
                    return;
                }

                try {
                    switch (state) {
                        case CHARGING -> {
                            tick++;
                            charge();
                        }
                        case FALLING -> {
                            tick++;
                            fall();
                        }
                        case LINGERING -> linger();
                        default -> { }
                    }
                } catch (Throwable error) {
                    plugin.getLogger().severe("天剑降临异常: " + error.getClass().getSimpleName() + ": " + error.getMessage());
                    if (sword != null && sword.isLanded()) {
                        try {
                            sword.keepLanded();
                        } catch (Throwable ignored) { }
                    } else {
                        finish();
                        cancel();
                    }
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 1L);
    }

    private void acquireTarget() {
        var block = caster.getTargetBlockExact(config.targetRange());
        if (block != null) {
            target = block.getLocation().add(0.5, 1.0, 0.5);
            return;
        }

        target = caster.getLocation().clone()
                .add(caster.getLocation().getDirection().normalize().multiply(15));
        target.setY(caster.getLocation().getY());
    }

    private void charge() {
        float progress = Math.min(1f, tick / (float) config.chargeTicks());
        effects.charge(target, tick);
        sword.charge(progress);

        if (tick >= config.chargeTicks()) {
            state = SwordState.FALLING;
            tick = 0;
            sword.beginFall();
            if (target.getWorld() != null) {
                target.getWorld().playSound(target, Sound.ENTITY_WARDEN_SONIC_BOOM, 2f, 0.6f);
            }
        }
    }

    private void fall() {
        if (!sword.exists()) {
            finish();
            return;
        }

        boolean alive = sword.tickFall();
        effects.falling(sword.location(), sword.velocity());

        if (!alive && sword.isLanded()) {
            state = SwordState.IMPACT;
            try {
                effects.impact(target, caster);
                damage.impact(caster, target);
            } catch (Throwable error) {
                plugin.getLogger().warning("天剑命中阶段异常（不影响剑本体驻留）: " + error.getMessage());
            }

            lingerTick = 0;
            lingerEndNanos = System.nanoTime() + (long) (config.lingerSeconds() * 1_000_000_000L);
            state = SwordState.LINGERING;
            plugin.getLogger().info("[Sword] LANDED -> LINGERING (" + config.lingerSeconds() + "s)");
        }
    }

    private void linger() {
        if (!sword.exists() || !sword.isLanded()) {
            finish();
            return;
        }

        sword.keepLanded();
        lingerTick++;

        if (lingerTick % config.lingerDamageInterval() == 0) {
            try {
                damage.lingering(caster, sword.location());
            } catch (Throwable error) {
                plugin.getLogger().warning("天剑持续伤害异常（不影响剑本体驻留）: " + error.getMessage());
            }
        }

        if (System.nanoTime() >= lingerEndNanos) {
            finish();
        }
    }

    private void finish() {
        if (finished) return;
        finished = true;
        state = SwordState.CLEANUP;

        if (task != null) task.cancel();
        try {
            if (sword != null) sword.remove();
        } finally {
            done.run();
        }
    }
}
