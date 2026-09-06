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

/** V2.2 skill orchestrator with a bounded ground-embed and linger lifecycle. */
public final class HeavenlySwordSkill {
    private static final int EMBED_DURATION_TICKS = 8;

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
    private int lingerDurationTicks;
    private int embedTick;
    private boolean finished;
    private boolean impactDamageApplied;
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
                        case IMPACT -> impact();
                        case LINGERING -> linger();
                        default -> { }
                    }
                } catch (Throwable error) {
                    plugin.getLogger().severe("天剑降临异常: " + error.getClass().getSimpleName() + ": " + error.getMessage());
                    // Never leave a persistent ItemDisplay behind after an exception.
                    finish();
                    cancel();
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
            embedTick = 0;
            impactDamageApplied = false;
            plugin.getLogger().info("[Sword] LANDED -> IMPACT EMBED (8 ticks)");
        }
    }

    private void impact() {
        if (!sword.exists() || !sword.isLanded()) {
            finish();
            return;
        }

        sword.keepLanded();
        effects.embed(target, embedTick, EMBED_DURATION_TICKS);

        // Gameplay impact damage remains a single event at the start of impact.
        if (!impactDamageApplied) {
            try {
                damage.impact(caster, target);
            } catch (Throwable error) {
                plugin.getLogger().warning("天剑命中伤害异常（不影响剑本体驻留）: " + error.getMessage());
            } finally {
                impactDamageApplied = true;
            }
        }

        embedTick++;
        if (embedTick > EMBED_DURATION_TICKS) {
            lingerTick = 0;
            lingerDurationTicks = Math.max(1, (int) Math.ceil(config.lingerSeconds() * 20.0));
            state = SwordState.LINGERING;
            plugin.getLogger().info("[Sword] IMPACT EMBED -> LINGERING (" + config.lingerSeconds() + "s / " + lingerDurationTicks + " ticks)");
        }
    }

    private void linger() {
        if (!sword.exists() || !sword.isLanded()) {
            finish();
            return;
        }

        lingerTick++;
        sword.keepLanded();

        if (lingerTick % config.lingerDamageInterval() == 0) {
            try {
                damage.lingering(caster, sword.location());
            } catch (Throwable error) {
                plugin.getLogger().warning("天剑持续伤害异常（不影响剑本体驻留）: " + error.getMessage());
            }
        }

        // Deterministic server-tick timeout: 4 seconds = 80 ticks by default.
        if (lingerTick >= lingerDurationTicks) {
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
        } catch (Throwable error) {
            plugin.getLogger().warning("天剑实体清理异常: " + error.getMessage());
        } finally {
            try {
                done.run();
            } catch (Throwable error) {
                plugin.getLogger().warning("天剑完成回调异常: " + error.getMessage());
            }
        }
    }
}
