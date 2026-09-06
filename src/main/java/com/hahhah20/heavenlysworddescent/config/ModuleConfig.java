package com.hahhah20.heavenlysworddescent.config;

import com.hahhah20.heavenlysworddescent.HeavenlySwordDescentPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/** Loads V2.2.0 split configuration files while keeping config.yml as fallback. */
public final class ModuleConfig {
    private final YamlConfiguration skills;
    private final YamlConfiguration sword;
    private final YamlConfiguration effects;

    public ModuleConfig(HeavenlySwordDescentPlugin plugin) {
        this.skills = load(plugin, "skills.yml");
        this.sword = load(plugin, "sword.yml");
        this.effects = load(plugin, "effects.yml");
    }

    private static YamlConfiguration load(HeavenlySwordDescentPlugin plugin, String name) {
        File file = new File(plugin.getDataFolder(), name);
        if (!file.exists()) plugin.saveResource(name, false);
        return YamlConfiguration.loadConfiguration(file);
    }

    public YamlConfiguration skills() { return skills; }
    public YamlConfiguration sword() { return sword; }
    public YamlConfiguration effects() { return effects; }
}
