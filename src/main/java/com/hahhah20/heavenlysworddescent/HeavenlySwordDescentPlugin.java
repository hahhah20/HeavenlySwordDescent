package com.hahhah20.heavenlysworddescent;

import com.hahhah20.heavenlysworddescent.command.HeavenlySwordCommand;
import com.hahhah20.heavenlysworddescent.manager.CooldownManager;
import com.hahhah20.heavenlysworddescent.manager.SkillManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class HeavenlySwordDescentPlugin extends JavaPlugin {
    private CooldownManager cooldownManager;
    private SkillManager skillManager;

    @Override public void onEnable() {
        saveDefaultConfig();
        cooldownManager = new CooldownManager();
        skillManager = new SkillManager(this);
        if (getCommand("heavenlysword") != null) {
            getCommand("heavenlysword").setExecutor(new HeavenlySwordCommand(skillManager));
        }
        getLogger().info("HeavenlySwordDescent V2.2.0 enabled.");
    }

    @Override public void onDisable() {
        if (skillManager != null) skillManager.shutdown();
    }

    public CooldownManager getCooldownManager() { return cooldownManager; }
}
