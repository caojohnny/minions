package com.gmail.woodyc40.minions.module;

import com.gmail.woodyc40.minions.Minions;
import com.gmail.woodyc40.minions.config.Config;
import com.gmail.woodyc40.minions.data.DataManager;
import com.gmail.woodyc40.minions.mode.ModeProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DataManagerModule extends AbstractModule {
    @Provides
    @Singleton
    public DataManager provideDataManager(Minions plugin, ModeProvider mp, Config config) {
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s",
                config.getDatabaseAddress(),
                config.getDatabasePort(),
                config.getDatabaseDbName()));
        cfg.setUsername(config.getDatabaseUser());
        cfg.setPassword(config.getDatabasePass());
        cfg.setMaximumPoolSize(config.getDatabaseMaxConnections());
        cfg.setConnectionTimeout(30_000);

        HikariDataSource ds = new HikariDataSource(cfg);
        return new DataManager(plugin, mp, ds, config.getDatabaseTable());
    }
}
