package com.hahhah20.heavenlysworddescent.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class CooldownManager {
    private final Map<UUID, Long> end = new HashMap<>();
    public long remaining(UUID id) { long r = end.getOrDefault(id, 0L) - System.currentTimeMillis(); if (r <= 0) end.remove(id); return Math.max(0, r); }
    public boolean active(UUID id) { return remaining(id) > 0; }
    public void set(UUID id, long ms) { end.put(id, System.currentTimeMillis() + ms); }
    public void clearAll() { end.clear(); }
}
