package com.gmail.woodyc40.minions;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.NamespacedKey;

@Singleton
public final class Constants {
    private final NamespacedKey spawnEggKey;

    @Inject
    public Constants(Minions plugin) {
        this.spawnEggKey = new NamespacedKey(plugin, "constants.spawn-egg-key");
    }

    public NamespacedKey getSpawnEggKey() {
        return this.spawnEggKey;
    }
}
