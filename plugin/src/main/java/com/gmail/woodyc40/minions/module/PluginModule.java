package com.gmail.woodyc40.minions.module;

import com.gmail.woodyc40.minions.Minions;
import com.google.inject.AbstractModule;

public class PluginModule extends AbstractModule {
    private final Minions plugin;

    public PluginModule(Minions plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void configure() {
        this.bind(Minions.class).toInstance(this.plugin);
    }
}
