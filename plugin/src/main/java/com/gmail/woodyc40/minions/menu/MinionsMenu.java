package com.gmail.woodyc40.minions.menu;

import com.gmail.woodyc40.minions.Minion;
import com.gmail.woodyc40.minions.Minions;
import com.gmail.woodyc40.minions.config.Config;
import com.gmail.woodyc40.minions.listener.RenameListener;
import com.gmail.woodyc40.minions.mode.ModeType;
import com.gmail.woodyc40.minions.util.ExperienceManager;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

public class MinionsMenu extends Menu {
    private final Minions plugin;
    private final RenameListener listener;
    private final Minion minion;

    private final String title;
    private final int size;

    private final SlottedItem rename;
    private final Map<ModeType, SlottedItem> mode = new HashMap<>();
    private final SlottedItem inventory;
    private final SlottedItem exp;
    private final SlottedItem remove;

    @Inject
    public MinionsMenu(Minions plugin, Config config,
                       RenameListener listener,
                       @Assisted Minion minion) {
        super(plugin);
        this.plugin = plugin;
        this.listener = listener;
        this.minion = minion;

        ConfigurationSection menu = config.getMenu();
        this.title = ChatColor.translateAlternateColorCodes('&', menu.getString("title"));
        this.size = menu.getInt("size");

        this.rename = new SlottedItem(menu.getConfigurationSection("rename"));
        ConfigurationSection mode = menu.getConfigurationSection("mode");
        int modeSlot = mode.getInt("slot");
        for (String key : mode.getKeys(false)) {
            if (!mode.isConfigurationSection(key)) {
                continue;
            }

            var iconSection = mode.getConfigurationSection(key);
            ModeType type = ModeType.valueOf(key.toUpperCase());
            SlottedItem item = new SlottedItem(modeSlot, Config.parseItemStack(iconSection));
            this.mode.put(type, item);
        }
        this.inventory = new SlottedItem(menu.getConfigurationSection("inventory"));
        this.exp = new SlottedItem(menu.getConfigurationSection("exp"));
        this.remove = new SlottedItem(menu.getConfigurationSection("remove"));
    }

    @Override
    protected String getTitle() {
        return this.title;
    }

    @Override
    protected int getSize() {
        return this.size;
    }

    @Override
    protected void doDraw(Player player) {
        int renameSlot = this.rename.addTo(super.inventory);
        this.clickListeners.put(renameSlot, ev -> {
            listener.getWaitingToRename().put(player, this.minion);
            player.sendMessage("You may now type the new minion name into chat");
            this.delayTask(player::closeInventory);
        });

        ModeType currentMode = this.minion.getMode();
        SlottedItem mode = this.mode.get(currentMode);
        int modeSlot = mode.addTo(super.inventory);
        this.clickListeners.put(modeSlot, ev -> {
            this.minion.setMode(currentMode.getNext());
            this.delayTask(() -> this.draw(player));
        });

        int inventorySlot = this.inventory.addTo(super.inventory);
        this.clickListeners.put(inventorySlot, ev -> {
            Inventory inv = this.minion.getInventory();
            if (inv == null) {
                player.sendMessage("Minion has no inventory!");
                return;
            }

            this.delayTask(() -> player.openInventory(inv));
        });

        int expSlot = this.exp.addTo(super.inventory);
        this.clickListeners.put(expSlot, ev -> {
            int exp = this.minion.getExperience();
            if (exp > 0) {
                ExperienceManager em = new ExperienceManager(player);
                int currentExp = em.getCurrentExp();
                if ((long) currentExp + exp > Integer.MAX_VALUE) {
                    player.sendMessage("Minion has too much experience, clear your own experience and try again");
                    return;
                }

                em.changeExp(exp);
                minion.subtractExperience(exp);
                player.sendMessage(format("You have collected %d experience points", exp));
            } else {
                player.sendMessage("Minion has no experience!");
            }
        });

        int removeSlot = this.remove.addTo(super.inventory);
        this.clickListeners.put(removeSlot, ev -> {
            this.minion.remove();
            player.sendMessage("Removed the minion!");

            this.delayTask(player::closeInventory);
        });
    }

    private void delayTask(Runnable task) {
        Bukkit.getScheduler().runTaskLater(this.plugin, task, 1);
    }
}
