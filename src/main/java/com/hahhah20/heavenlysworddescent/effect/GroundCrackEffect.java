package com.hahhah20.heavenlysworddescent.effect;
import org.bukkit.*;
public final class GroundCrackEffect { public static void create(Location c,int rings){World w=c.getWorld();if(w==null)return;for(int ring=1;ring<=rings;ring++){double r=ring*1.25;for(int i=0;i<16;i++){double a=Math.PI*2*i/16.0;Location p=c.clone().add(Math.cos(a)*r,.12,Math.sin(a)*r);w.spawnParticle(Particle.BLOCK,p,2,.05,.03,.05,.02,Material.CRYING_OBSIDIAN.createBlockData());}}}}
