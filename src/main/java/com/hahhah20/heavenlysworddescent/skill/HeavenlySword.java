package com.hahhah20.heavenlysworddescent.skill;

import com.hahhah20.heavenlysworddescent.HeavenlySwordDescentPlugin;
import com.hahhah20.heavenlysworddescent.damage.DamageManager;
import com.hahhah20.heavenlysworddescent.effect.EnergyEffect;
import com.hahhah20.heavenlysworddescent.effect.ImpactEffect;
import com.hahhah20.heavenlysworddescent.effect.SwordTrail;
import com.hahhah20.heavenlysworddescent.effect.WarningEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public final class HeavenlySword {
    private final HeavenlySwordDescentPlugin p;
    private final Player caster;
    private final Runnable done;
    private Location target;
    private SwordProjectile sword;
    private SwordState state;
    private int tick;
    private int lingerTick;
    private long lingerEndNanos;
    private boolean finished;
    private BukkitRunnable task;

    public HeavenlySword(HeavenlySwordDescentPlugin plugin, Player c, Runnable d) {
        p = plugin;
        caster = c;
        done = d;
    }

    public void start() {
        var block = caster.getTargetBlockExact(p.getConfig().getInt("skill.target-range"));
        if (block != null) target = block.getLocation().add(.5, 1, .5);
        else {
            target = caster.getLocation().clone().add(caster.getLocation().getDirection().normalize().multiply(15));
            target.setY(caster.getLocation().getY());
        }
        // The sword model must never inherit the caster camera rotation.  Its
        // ItemDisplay yaw is calculated from the sword toward the caster so
        // the sword face always turns toward the player, regardless of which
        // direction the skill was released.
        sword = new SwordProjectile(p, target, caster);
        sword.spawn();
        state = SwordState.CHARGING;

        task = new BukkitRunnable() {
            @Override public void run() {
                if (finished) { cancel(); return; }
                if (!caster.isOnline()) { finish(); cancel(); return; }
                try {
                    if (state == SwordState.CHARGING) { tick++; charge(); }
                    else if (state == SwordState.FALLING) { tick++; fall(); }
                    else if (state == SwordState.LINGERING) linger();
                } catch (Throwable error) {
                    p.getLogger().severe("天剑降临异常: " + error.getClass().getSimpleName() + ": " + error.getMessage());
                    if (sword != null && sword.isLanded()) {
                        try { sword.keepLanded(); } catch (Throwable ignored) {}
                    } else {
                        finish();
                        cancel();
                    }
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
        if (!sword.exists()) { finish(); return; }
        boolean alive = sword.tickFall();
        SwordTrail.tick(sword.location(), sword.velocity());
        if (!alive && sword.isLanded()) {
            try { ImpactEffect.execute(p, caster, target); }
            catch (Throwable error) {
                p.getLogger().warning("天剑命中效果异常（不影响剑本体驻留）: " + error.getMessage());
            }
            lingerTick = 0;
            double seconds = Math.max(4.0, p.getConfig().getDouble("skill.linger.seconds", 4.0));
            lingerEndNanos = System.nanoTime() + (long) (seconds * 1_000_000_000L);
            state = SwordState.LINGERING;
            p.getLogger().info("[Sword] LANDED -> LINGERING (" + seconds + "s)");
        }
    }

    private void linger() {
        if (!sword.exists() || !sword.isLanded()) {
            // Never allow an early state transition to silently remove a landed sword.
            // If the display disappeared externally, there is nothing left to preserve.
            finish();
            return;
        }
        sword.keepLanded();
        lingerTick++;
        int interval = Math.max(1, p.getConfig().getInt("skill.linger.damage-interval-ticks", 10));
        if (lingerTick % interval == 0) {
            try { DamageManager.lingeringDamage(p, caster, sword.location()); }
            catch (Throwable error) {
                p.getLogger().warning("天剑持续伤害异常（不影响剑本体驻留）: " + error.getMessage());
            }
        }
        // Wall-clock timing prevents the sword from disappearing before a real
        // four seconds have elapsed when the server has irregular tick timing.
        if (System.nanoTime() >= lingerEndNanos) finish();
    }

    private void finish() {
        if (finished) return;
        finished = true;
        if (task != null) task.cancel();
        try {
            if (sword != null) sword.remove();
        } finally {
            done.run();
        }
    }
}
