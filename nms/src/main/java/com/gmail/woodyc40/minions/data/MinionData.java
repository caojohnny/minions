package com.gmail.woodyc40.minions.data;

import com.gmail.woodyc40.minions.mode.Mode;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class MinionData {
    private UUID owner;
    private Inventory inventory;
    private int experience;
    private Mode mode;

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }
}
