package com.gmail.woodyc40.minions.menu;

import com.gmail.woodyc40.minions.config.Config;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SlottedItem {
    private final int slot;
    private final ItemStack item;

    public SlottedItem(ConfigurationSection section) {
        this.slot = section.getInt("slot");
        this.item = Config.parseItemStack(section);
    }

    public SlottedItem(int slot, ItemStack item) {
        this.slot = slot;
        this.item = item;
    }

    public int getSlot() {
        return this.slot;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public int addTo(Inventory inventory) {
        inventory.setItem(this.slot, this.item);
        return this.slot;
    }
}
