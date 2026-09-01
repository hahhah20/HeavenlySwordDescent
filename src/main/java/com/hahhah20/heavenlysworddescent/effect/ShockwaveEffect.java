package com.hahhah20.heavenlysworddescent.effect;
import org.bukkit.*;
public final class ShockwaveEffect { public static void create(Location c){World w=c.getWorld();if(w==null)return;for(double r=1;r<=7;r+=.35)for(int i=0;i<32;i++){double a=Math.PI*2*i/32.0;w.spawnParticle(Particle.END_ROD,c.clone().add(Math.cos(a)*r,.15,Math.sin(a)*r),1);}for(double r=2;r<=6;r+=1)for(int i=0;i<24;i++){double a=Math.PI*2*i/24.0;w.spawnParticle(Particle.SOUL_FIRE_FLAME,c.clone().add(Math.cos(a)*r,.08,Math.sin(a)*r),1);}}}
