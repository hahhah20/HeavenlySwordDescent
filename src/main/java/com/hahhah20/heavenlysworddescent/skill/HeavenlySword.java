package com.hahhah20.heavenlysworddescent.skill;

import com.hahhah20.heavenlysworddescent.HeavenlySwordDescentPlugin;
import com.hahhah20.heavenlysworddescent.damage.DamageManager;
import com.hahhah20.heavenlysworddescent.effect.EnergyEffect;
import com.hahhah20.heavenlysworddescent.effect.ImpactEffect;
import com.hahhah20.heavenlysworddescent.effect.SwordTrail;
import com.hahhah20.heavenlysworddescent.effect.WarningEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import org.bukkit.entity.Player;

public final class HeavenlySword {
    private final HeavenlySwordDescentPlugin p;
    private final Player caster;
    private final Runnable done;
    private Location target;
    private SwordProjectile sword;
    private SwordState state;
    private int tick;
    private int lingerTick;
    private boolean finished;
    private BukkitRunnable task;

    public HeavenlySword(HeavenlySwordDescentPlugin p, Player c, Runnable d) {
        this.p = p;
        caster = c;
        done = d;
    }

    public void start() {
        var block = caster.getTargetBlockExact(p.getConfig().getInt("skill.target-range"));
        if (block != null) {
            target = block.getLocation().add(.5, 1, .5);
        } else {
            target = caster.getLocation().clone().add(caster.getLocation().getDirection().normalize().multiply(15));
            target.setY(caster.getLocation().getY());
        }
        p.getLogger().info("天剑降临目标: " + target);

        sword = new SwordProjectile(p, target);
        sword.spawn();
        state = SwordState.CHARGING;

        task = new BukkitRunnable() {
            @Override public void run() {
                if (finished) { cancel(); return; }
                if (!caster.isOnline()) { finish(); cancel(); return; }
                tick++;
                try {
                    if (state == SwordState.CHARGING) charge();
                    else if (state == SwordState.FALLING) fall();
                    else if (state == SwordState.LINGERING) linger();
                } catch (Throwable error) {
                    p.getLogger().severe("天剑降临异常: " + error.getMessage());
                    finish();
                    cancel();
                }
            }
        };
        task.runTaskTimer(p, 0, 1);
    }

    private void charge() {
        float prog = Math.min(1f, tick / (float) p.getConfig().getInt("skill.charge-ticks"));
        WarningEffect.tick(p, target, tick);
        EnergyEffect.tick(p, target, tick);
        sword.charge(prog);
        if (tick >= p.getConfig().getInt("skill.charge-ticks")) {
            state = SwordState.FALLING;
            tick = 0;
            sword.beginFall();
            target.getWorld().playSound(target, Sound.ENTITY_WARDEN_SONIC_BOOM, 2f, .6f);
        }
    }

    private void fall() {
        boolean alive = sword.tickFall();
        SwordTrail.tick(sword.location(), sword.velocity());
        if (!alive) {
            state = SwordState.IMPACT;
            try {
                ImpactEffect.execute(p, caster, target);
                sword.land();
                lingerTick = 0;
                tick = 0;
                state = SwordState.LINGERING;
            } catch (Throwable error) {
                p.getLogger().severe("天剑降临命中处理异常: " + error.getMessage());
                finish();
            }
        }
    }

    private void linger() {
        lingerTick++;
        int interval = Math.max(1, p.getConfig().getInt("skill.linger.damage-interval-ticks", 10));
        if (lingerTick % interval == 0) {
            DamageManager.lingeringDamage(p, caster, target);
        }

        if (lingerTick >= Math.max(1, (int) Math.round(
                p.getConfig().getDouble("skill.linger.seconds", 4.0) * 20.0))) {
            state = SwordState.CLEANUP;
            finish();
        }
    }

    private void finish() {
        if (finished) return;
        finished = true;
        if (task != null) task.cancel();
        try {
            if (sword != null) sword.remove();
        } finally {
            p.getLogger().info("天剑降临技能清理完成");
            done.run();
        }
    }
}
