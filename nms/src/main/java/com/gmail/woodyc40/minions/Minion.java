package com.gmail.woodyc40.minions;

import com.gmail.woodyc40.minions.data.MinionData;
import com.gmail.woodyc40.minions.mode.ModeType;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.UUID;

public interface Minion {
    void init(MinionData data, OfflinePlayer owner);

    void init(UUID persistentId, MinionData data, OfflinePlayer owner);

    @Nullable
    MinionData getData();

    UUID getOwner();

    void setName(String name);

    void setTool(ItemStack tool);

    @Nullable
    ItemStack getTool();

    @Nullable
    Inventory getInventory();

    int getExperience();

    boolean addExperience(int experience);

    boolean subtractExperience(int experience);

    @NonNull
    ModeType getMode();

    void setMode(@NonNull ModeType type);

    ArmorStand getEntity();

    Object getNmsEntity();

    UUID getPersistentId();

    void remove();
}
