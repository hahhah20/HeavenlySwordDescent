package com.hahhah20.heavenlysworddescent.manager;

import com.hahhah20.heavenlysworddescent.HeavenlySwordDescentPlugin;
import com.hahhah20.heavenlysworddescent.skill.HeavenlySword;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class SkillManager {
    private final HeavenlySwordDescentPlugin plugin;
    private final Set<UUID> casting = new HashSet<>();
    private final BukkitTask actionBarTask;

    public SkillManager(HeavenlySwordDescentPlugin plugin) {
        this.plugin = plugin;
        this.actionBarTask = Bukkit.getScheduler().runTaskTimer(plugin, this::updateActionBars, 0L, 2L);
    }

    public void cast(Player player) {
        UUID id = player.getUniqueId();
        if (casting.contains(id)) {
            showActionBar(player, "§e⚔ 天剑降临 §f释放中...");
            return;
        }
        if (plugin.getCooldownManager().active(id)) {
            showCooldown(player);
            return;
        }

        casting.add(id);
        plugin.getCooldownManager().set(id, plugin.getConfig().getLong("skill.cooldown-seconds") * 1000L);
        showActionBar(player, "§b⚔ 天剑降临 §f释放中...");
        new HeavenlySword(plugin, player, () -> casting.remove(id)).start();
    }

    private void updateActionBars() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID id = player.getUniqueId();
            if (casting.contains(id)) {
                showActionBar(player, "§b⚔ 天剑降临 §f释放中...");
            } else if (plugin.getCooldownManager().active(id)) {
                showCooldown(player);
            }
        }
    }

    private void showCooldown(Player player) {
        double seconds = plugin.getCooldownManager().remaining(player.getUniqueId()) / 1000.0;
        showActionBar(player, String.format("§6⚔ 天剑降临 §f冷却：%.1fs", seconds));
    }

    private void showActionBar(Player player, String message) {
        player.sendActionBar(Component.text(message));
    }

    public void shutdown() {
        casting.clear();
        plugin.getCooldownManager().clearAll();
        actionBarTask.cancel();
    }
}
