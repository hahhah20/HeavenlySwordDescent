package com.hahhah20.heavenlysworddescent.skill;

import com.hahhah20.heavenlysworddescent.HeavenlySwordDescentPlugin;
import org.bukkit.entity.Player;

/**
 * Compatibility facade retained for older callers.
 * V2.2.0 gameplay is implemented by HeavenlySwordSkill.
 */
@Deprecated
public final class HeavenlySword {
    private final HeavenlySwordSkill delegate;

    public HeavenlySword(HeavenlySwordDescentPlugin plugin, Player caster, Runnable done) {
        this.delegate = new HeavenlySwordSkill(plugin, caster, done);
    }

    public void start() {
        delegate.start();
    }
}
