package com.gmail.woodyc40.minions.module;

import com.gmail.woodyc40.minions.MinionsNms;
import com.gmail.woodyc40.minions.MinionsNms1_14_2;
import com.gmail.woodyc40.minions.mode.ModeProvider;
import com.google.inject.AbstractModule;
import org.bukkit.Bukkit;

public class NmsModule extends AbstractModule {
    @Override
    protected void configure() {
        String apiVersion = Bukkit.getBukkitVersion();
        if (apiVersion.startsWith("1.14")) {
            this.bind(MinionsNms.class).to(MinionsNms1_14_2.class).asEagerSingleton();
            this.bind(ModeProvider.class).to(MinionsNms1_14_2.class).asEagerSingleton();
        }
    }
}
