package com.gmail.woodyc40.minions.module;

import com.gmail.woodyc40.minions.menu.MenuFactory;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class MenuModule extends AbstractModule {
    @Override
    protected void configure() {
        this.install(new FactoryModuleBuilder().build(MenuFactory.class));
    }
}
