package com.hahhah20.heavenlysworddescent.effect;
import com.hahhah20.heavenlysworddescent.HeavenlySwordDescentPlugin;
import org.bukkit.*;
public final class WarningEffect {
 public static void tick(HeavenlySwordDescentPlugin p, Location c, int tick){
  World w=c.getWorld(); if(w==null)return;
  int n=Math.max(16,p.getConfig().getInt("visual.warning-points",32));
  double max=p.getConfig().getDouble("skill.radius.outer"), prog=Math.min(1,tick/(double)p.getConfig().getInt("skill.charge-ticks")), r=1+prog*(max-1);
  int stride=tick%2==0?0:1;
  for(int i=stride;i<n;i+=2){double a=Math.PI*2*i/n;w.spawnParticle(Particle.SOUL_FIRE_FLAME,c.clone().add(Math.cos(a)*r,.05,Math.sin(a)*r),1);}
  if(tick%2==0){
   for(int i=-4;i<=4;i++){w.spawnParticle(Particle.END_ROD,c.clone().add(i*.5,.08,0),1);w.spawnParticle(Particle.END_ROD,c.clone().add(0,.08,i*.5),1);}
  }
 }
}
