package com.hahhah20.heavenlysworddescent.effect;
import com.hahhah20.heavenlysworddescent.HeavenlySwordDescentPlugin;
import org.bukkit.*;
public final class EnergyEffect {
 public static void tick(HeavenlySwordDescentPlugin p,Location c,int tick){
  World w=c.getWorld();if(w==null)return;
  int orbits=Math.min(2,p.getConfig().getInt("visual.energy-orbits",2));
  for(int ring=0;ring<orbits;ring++){
   double h=10+ring*2+Math.sin(tick*.16+ring)*.7,r=1.1+ring*.65;
   for(int i=0;i<4;i++){
    double a=tick*.12+i*Math.PI/2+ring*.8;
    w.spawnParticle(Particle.END_ROD,c.clone().add(Math.cos(a)*r,h,Math.sin(a)*r),1);
   }
  }
  if(tick%12==0)w.spawnParticle(Particle.SOUL_FIRE_FLAME,c.clone().add(0,18,0),2,.3,.1,.3,.01);
 }
}
