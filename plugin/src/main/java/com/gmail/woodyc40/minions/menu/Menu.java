package com.gmail.woodyc40.minions.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class Menu implements Listener {
    protected Map<Integer, Consumer<InventoryClickEvent>> clickListeners =
            new HashMap<>();
    protected Inventory inventory;

    public Menu(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (this.inventory == null) {
            return;
        }

        InventoryView view = event.getView();
        Inventory top = view.getTopInventory();
        if (!top.equals(this.inventory)) {
            return;
        }

        boolean isTop = event.getClickedInventory() == top;
        event.setCancelled(event.isShiftClick() || isTop);

        if (isTop) {
            Consumer<InventoryClickEvent> listener = this.clickListeners.get(event.getSlot());
            if (listener != null) {
                listener.accept(event);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        InventoryClickEvent.getHandlerList().unregister(this);
        event.getHandlers().unregister(this);
    }

    public void showTo(HumanEntity he) {
        this.inventory = Bukkit.createInventory(null,
                this.getSize(),
                this.getTitle());
        this.draw((Player) he);

        he.openInventory(inventory);
    }

    public void draw(Player player) {
        this.inventory.clear();
        this.clickListeners.clear();

        this.doDraw(player);
    }

    protected abstract String getTitle();

    protected abstract int getSize();

    protected abstract void doDraw(Player player);
}
