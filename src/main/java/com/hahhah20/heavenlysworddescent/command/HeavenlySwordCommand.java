package com.hahhah20.heavenlysworddescent.command;

import com.hahhah20.heavenlysworddescent.manager.SkillManager;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public final class HeavenlySwordCommand implements CommandExecutor {
    private final SkillManager skills;
    public HeavenlySwordCommand(SkillManager skills) { this.skills = skills; }
    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) { sender.sendMessage("仅玩家可用。"); return true; }
        if (!player.hasPermission("heavenlysword.use")) { player.sendMessage("§c你没有权限。"); return true; }
        skills.cast(player); return true;
    }
}
