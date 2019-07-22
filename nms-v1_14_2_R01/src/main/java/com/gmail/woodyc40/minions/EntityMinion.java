package com.gmail.woodyc40.minions;

import com.gmail.woodyc40.minions.data.MinionData;
import com.gmail.woodyc40.minions.mode.Mode;
import com.gmail.woodyc40.minions.mode.ModeType;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.UUID;

public class EntityMinion extends EntityArmorStand implements Minion {
    private static final ItemStack CHESTPLATE = new ItemStack(Items.LEATHER_CHESTPLATE);
    private static final ItemStack LEGGINGS = new ItemStack(Items.LEATHER_LEGGINGS);
    private static final ItemStack BOOTS = new ItemStack(Items.LEATHER_BOOTS);

    private MinionData data;

    public EntityMinion(EntityTypes<? extends EntityArmorStand> entitytypes, World world) {
        super(entitytypes, world);

        this.setSmall(true);
        this.setSlot(EnumItemSlot.CHEST, CHESTPLATE);
        this.setSlot(EnumItemSlot.LEGS, LEGGINGS);
        this.setSlot(EnumItemSlot.FEET, BOOTS);
    }

    @Override
    public void tick() {
        super.tick();

        Mode currentMode = this.data.getMode();
        if (currentMode != null) {
            currentMode.tick(this);
        }
    }

    @Override
    public void init(MinionData data, OfflinePlayer owner) {
        this.data = data;

        org.bukkit.inventory.ItemStack skull = new org.bukkit.inventory.ItemStack(org.bukkit.Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwningPlayer(owner);
        skull.setItemMeta(meta);

        this.setSlot(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(skull));
    }

    @Override
    public void init(UUID persistentId, MinionData data, OfflinePlayer owner) {
        this.uniqueID = persistentId;
        this.init(data, owner);
    }

    @Override
    public @Nullable MinionData getData() {
        return this.data;
    }

    @Override
    public UUID getOwner() {
        return this.data.getOwner();
    }

    @Override
    public void setName(String name) {
        CraftEntity entity = this.getBukkitEntity();
        if (name == null || name.isEmpty()) {
            entity.setCustomName("");
            entity.setCustomNameVisible(false);
            return;
        }

        entity.setCustomName(name);
        entity.setCustomNameVisible(true);
    }

    @Override
    public void setTool(org.bukkit.inventory.ItemStack tool) {
        this.setSlot(EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(tool));
    }

    @Override
    public org.bukkit.inventory.@Nullable ItemStack getTool() {
        ItemStack item = this.getEquipment(EnumItemSlot.MAINHAND);
        if (item.getItem() == null || item.getItem() == Items.AIR) {
            return null;
        }

        return CraftItemStack.asBukkitCopy(item);
    }

    @Override
    public @Nullable Inventory getInventory() {
        return this.data.getInventory();
    }

    @Override
    public int getExperience() {
        return this.data.getExperience();
    }

    @Override
    public boolean addExperience(int experience) {
        int currentExp = this.data.getExperience();

        try {
            int newExp = Math.addExact(currentExp, experience);
            this.data.setExperience(newExp);
            return true;
        } catch (ArithmeticException e) {
            return false;
        }
    }

    @Override
    public boolean subtractExperience(int experience) {
        int currentExp = this.data.getExperience();
        int newExp = currentExp - experience;

        if (newExp <= 0) {
            return false;
        }

        this.data.setExperience(newExp);
        return true;
    }

    @Override
    public @NonNull ModeType getMode() {
        Mode mode = this.data.getMode();
        if (mode == null) {
            return ModeType.NONE;
        }

        return mode.getType();
    }

    @Override
    public void setMode(@NonNull ModeType type) {
        Mode currentMode = this.data.getMode();
        if (currentMode != null) {
            currentMode.complete(this);
        }

        Mode mode = MinionsNms1_14_2.getMode(type);
        this.data.setMode(mode);

        if (mode != null) {
            mode.init(this);
        }
    }

    @Override
    public ArmorStand getEntity() {
        return (ArmorStand) this.getBukkitEntity();
    }

    @Override
    public Object getNmsEntity() {
        return this;
    }

    @Override
    public UUID getPersistentId() {
        return this.getUniqueID();
    }

    @Override
    public void remove() {
        this.die();

        Mode mode = this.data.getMode();
        if (mode != null) {
            mode.complete(this);
        }

        CraftEntity entity = this.getBukkitEntity();
        Location location = entity.getLocation();
        var world = location.getWorld();

        var tool = this.getTool();
        if (tool != null) {
            world.dropItem(location, tool);
        }

        Inventory inventory = this.data.getInventory();
        if (inventory != null) {
            for (var item : inventory.getContents()) {
                if (item != null) {
                    world.dropItem(location, item);
                }
            }
        }

        int experience = this.data.getExperience();
        if (experience > 0) {
            ExperienceOrb expOrb = world.spawn(location, ExperienceOrb.class);
            expOrb.setExperience(experience);
        }
    }
}
