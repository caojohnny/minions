package com.gmail.woodyc40.minions;

import com.gmail.woodyc40.minions.data.MinionData;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface MinionsNms {
    void registerEntity();

    @Nullable
    Minion getMinion(Entity entity);

    @NonNull
    Minion spawnMinion(@NonNull Location spawnLocation, @NonNull MinionData data, Player spawner);

    @NonNull
    Minion respawnMinion(@NonNull Minion template, @NonNull MinionData data, @NonNull OfflinePlayer owner);
}
