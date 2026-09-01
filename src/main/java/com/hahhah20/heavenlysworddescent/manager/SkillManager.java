package com.hahhah20.heavenlysworddescent.manager;

import com.hahhah20.heavenlysworddescent.HeavenlySwordDescentPlugin;
import com.hahhah20.heavenlysworddescent.skill.HeavenlySword;
import org.bukkit.entity.Player;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class SkillManager {
    private final HeavenlySwordDescentPlugin plugin;
    private final Set<UUID> casting = new HashSet<>();
    public SkillManager(HeavenlySwordDescentPlugin plugin) { this.plugin = plugin; }
    public void cast(Player player) {
        UUID id = player.getUniqueId();
        if (casting.contains(id)) { player.sendMessage("§c技能正在释放中。"); return; }
        if (plugin.getCooldownManager().active(id)) { player.sendMessage(String.format("§c冷却中：%.1fs", plugin.getCooldownManager().remaining(id) / 1000.0)); return; }
        casting.add(id);
        plugin.getCooldownManager().set(id, plugin.getConfig().getLong("skill.cooldown-seconds") * 1000L);
        new HeavenlySword(plugin, player, () -> casting.remove(id)).start();
    }
    public void shutdown() { casting.clear(); plugin.getCooldownManager().clearAll(); }
}
