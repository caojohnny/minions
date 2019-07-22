package com.gmail.woodyc40.minions.listener;

import com.gmail.woodyc40.minions.Minion;
import com.gmail.woodyc40.minions.Minions;
import com.gmail.woodyc40.minions.MinionsNms;
import com.gmail.woodyc40.minions.data.DataManager;
import com.gmail.woodyc40.minions.data.MinionData;
import com.google.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldSaveEvent;

import java.util.UUID;

public class ChunkListener implements Listener {
    private final Minions plugin;
    private final MinionsNms nms;
    private final DataManager dm;

    @Inject
    public ChunkListener(Minions plugin, MinionsNms nms, DataManager dm) {
        this.plugin = plugin;
        this.nms = nms;
        this.dm = dm;
    }

    @EventHandler(ignoreCancelled = true)
    public void onLoad(ChunkLoadEvent event) {
        for (var entity : event.getChunk().getEntities()) {
            Minion minion = this.nms.getMinion(entity);
            if (minion != null) {
                entity.remove();

                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                    UUID persistentId = minion.getPersistentId();
                    MinionData data = this.dm.getData(persistentId);
                    if (data == null) {
                        return;
                    }

                    UUID ownerId = data.getOwner();
                    OfflinePlayer owner = Bukkit.getOfflinePlayer(ownerId);

                    Bukkit.getScheduler().runTask(this.plugin, () ->
                            this.nms.respawnMinion(minion, data, owner));
                });
            }
        }
    }

    @EventHandler
    public void onUnload(ChunkUnloadEvent event) {
        for (var entity : event.getChunk().getEntities()) {
            Minion minion = this.nms.getMinion(entity);
            if (minion != null) {
                MinionData data = minion.getData();
                if (data != null) {
                    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () ->
                            this.dm.writeData(minion.getPersistentId(), data));
                }
            }
        }
    }

    @EventHandler
    public void onSave(WorldSaveEvent event) {
        for (var entity : event.getWorld().getEntities()) {
            Minion minion = this.nms.getMinion(entity);
            if (minion != null) {
                MinionData data = minion.getData();
                if (data != null) {
                    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () ->
                            this.dm.writeData(minion.getPersistentId(), data));
                }
            }
        }
    }
}
