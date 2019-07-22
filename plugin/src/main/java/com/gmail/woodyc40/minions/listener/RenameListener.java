package com.gmail.woodyc40.minions.listener;

import com.gmail.woodyc40.minions.Minion;
import com.gmail.woodyc40.minions.Minions;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.format;

@Singleton
public class RenameListener implements Listener {
    private final Minions plugin;

    private final Map<Player, Minion> waitingToRename = new ConcurrentHashMap<>();

    @Inject
    public RenameListener(Minions plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Minion minion = this.waitingToRename.remove(player);
        if (minion == null) {
            return;
        }

        event.setCancelled(true);
        Bukkit.getScheduler().runTask(this.plugin, () -> {
            String name = ChatColor.translateAlternateColorCodes('&', event.getMessage());
            minion.setName(name);
            player.sendMessage(format("You've renamed your minion to '%s'!", name));
        });
    }

    public Map<Player, Minion> getWaitingToRename() {
        return this.waitingToRename;
    }
}
