package com.hahhah20.heavenlysworddescent.skill;

import com.hahhah20.heavenlysworddescent.HeavenlySwordDescentPlugin;
import com.hahhah20.heavenlysworddescent.effect.*;
import org.bukkit.*;
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
    private boolean finished;
    private BukkitRunnable task;

    public HeavenlySword(HeavenlySwordDescentPlugin p, Player c, Runnable d) { this.p=p; caster=c; done=d; }

    public void start() {
        var b=caster.getTargetBlockExact(p.getConfig().getInt("skill.target-range"));
        target=b!=null?b.getLocation().add(.5,1,.5):caster.getLocation().add(caster.getLocation().getDirection().normalize().multiply(12));
        sword=new SwordProjectile(p,target);
        sword.spawn();
        state=SwordState.CHARGING;
        task=new BukkitRunnable(){
            @Override public void run(){
                if(finished){cancel();return;}
                if(!caster.isOnline()){finish();cancel();return;}
                tick++;
                try {
                    if(state==SwordState.CHARGING) charge();
                    else if(state==SwordState.FALLING) fall();
                } catch(Throwable error) {
                    p.getLogger().severe("天剑降临技能运行异常，正在强制清理: "+error.getMessage());
                    finish();
                    cancel();
                }
            }
        };
        task.runTaskTimer(p,0,1);
    }

    private void charge(){
        float prog=Math.min(1f,tick/(float)p.getConfig().getInt("skill.charge-ticks"));
        WarningEffect.tick(p,target,tick);
        EnergyEffect.tick(p,target,tick);
        sword.charge(prog);
        if(tick%10==0) target.getWorld().playSound(target,Sound.BLOCK_BEACON_AMBIENT,1f,.75f+prog);
        if(tick>=30&&tick%2==0) target.getWorld().playSound(target,Sound.BLOCK_NOTE_BLOCK_BELL,1.3f,1.4f+prog);
        if(tick>=p.getConfig().getInt("skill.charge-ticks")){
            state=SwordState.FALLING;
            sword.beginFall();
            target.getWorld().playSound(target,Sound.ENTITY_WARDEN_SONIC_BOOM,2f,.6f);
        }
    }

    private void fall(){
        boolean alive=sword.tickFall();
        SwordTrail.tick(sword.location(),sword.velocity());
        if(!alive){
            state=SwordState.IMPACT;
            try { ImpactEffect.execute(p,caster,target); }
            finally {
                // 命中后的清理必须无条件执行，避免异常导致技能永久占用 casting 状态。
                state=SwordState.CLEANUP;
                finish();
            }
        }
    }

    private void finish(){
        if(finished)return;
        finished=true;
        if(task!=null) task.cancel();
        try { if(sword!=null) sword.remove(); }
        finally { done.run(); }
    }
}
