package com.hahhah20.heavenlysworddescent.effect;
import org.bukkit.*;
public final class SwordTrail { public static void tick(Location l,double speed){World w=l.getWorld();if(w==null)return;int n=Math.min(30,6+(int)(speed*4));w.spawnParticle(Particle.END_ROD,l,n,.25,.8,.25,.02);w.spawnParticle(Particle.CRIT,l,n/2,.2,.6,.2,.05);}}
