package com.gmail.woodyc40.minions;

import com.gmail.woodyc40.minions.command.MinionsCommand;
import com.gmail.woodyc40.minions.data.DataManager;
import com.gmail.woodyc40.minions.data.MinionData;
import com.gmail.woodyc40.minions.listener.*;
import com.gmail.woodyc40.minions.module.DataManagerModule;
import com.gmail.woodyc40.minions.module.MenuModule;
import com.gmail.woodyc40.minions.module.NmsModule;
import com.gmail.woodyc40.minions.module.PluginModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.logging.Level;

public class Minions extends JavaPlugin {
    private Injector injector;

    @Override
    public void onLoad() {
        this.saveDefaultConfig();

        PluginModule pluginModule = new PluginModule(this);
        NmsModule nmsModule = new NmsModule();
        MenuModule menuModule = new MenuModule();
        DataManagerModule dmModule = new DataManagerModule();
        this.injector = Guice.createInjector(pluginModule,
                nmsModule,
                menuModule,
                dmModule);

        MinionsNms nms = this.injector.getInstance(MinionsNms.class);
        nms.registerEntity();
    }

    @Override
    public void onEnable() {
        DataManager dm = this.injector.getInstance(DataManager.class);
        dm.createTable();

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(this.injector.getInstance(RenameListener.class), this);
        pm.registerEvents(this.injector.getInstance(SpawnListener.class), this);
        pm.registerEvents(this.injector.getInstance(InteractListener.class), this);
        pm.registerEvents(this.injector.getInstance(ChunkListener.class), this);
        pm.registerEvents(this.injector.getInstance(ItemInteractListener.class), this);

        this.getCommand("minions").setExecutor(this.injector.getInstance(MinionsCommand.class));
    }

    @Override
    public void onDisable() {
        MinionsNms nms = this.injector.getInstance(MinionsNms.class);
        DataManager dm = this.injector.getInstance(DataManager.class);
        for (var world : Bukkit.getWorlds()) {
            for (var entity : world.getEntities()) {
                try {
                    Minion minion = nms.getMinion(entity);
                    if (minion != null) {
                        UUID persistentId = minion.getPersistentId();
                        MinionData data = minion.getData();
                        dm.writeData(persistentId, data);
                    }
                } catch (Exception e) {
                    this.getLogger().log(Level.SEVERE, "Failed to save minion", e);
                }
            }
        }

        dm.close();
    }
}
