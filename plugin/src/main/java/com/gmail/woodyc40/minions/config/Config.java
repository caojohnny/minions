package com.gmail.woodyc40.minions.config;

import com.gmail.woodyc40.minions.Minions;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Singleton
public class Config {
    private final String databaseAddress;
    private final int databasePort;
    private final String databaseDbName;
    private final String databaseUser;
    private final String databasePass;
    private final int databaseMaxConnections;
    private final String databaseTable;

    private final ItemStack spawnEgg;

    private final String internalInvTitle;
    private final int internalInvSize;

    private final ConfigurationSection menu;

    @Inject
    public Config(Minions plugin) {
        FileConfiguration cfg = plugin.getConfig();

        ConfigurationSection database = cfg.getConfigurationSection("database");
        this.databaseAddress = database.getString("address");
        this.databasePort = database.getInt("port");
        this.databaseDbName = database.getString("db-name");
        this.databaseUser = database.getString("user");
        this.databasePass = database.getString("pass");
        this.databaseMaxConnections = database.getInt("max-connections", 10);
        this.databaseTable = database.getString("table");

        this.spawnEgg = parseItemStack(requireNonNull(cfg.getConfigurationSection("spawn-egg")));

        this.internalInvTitle = ChatColor.translateAlternateColorCodes('&', cfg.getString("internal-inv-title"));
        this.internalInvSize = cfg.getInt("internal-inv-size");

        this.menu = cfg.getConfigurationSection("menu");
    }

    public static @NonNull ItemStack parseItemStack(@NonNull ConfigurationSection section) {
        String typeString = requireNonNull(section.getString("type"), "Type is required");
        Material type = requireNonNull(Material.getMaterial(typeString), "Type is not valid Material");
        int amount = section.getInt("amount", 1);
        short data = (short) section.getInt("data", 0);
        ItemStack item = new ItemStack(type, amount);

        ItemMeta meta = requireNonNull(item.getItemMeta());
        if (data != 0) {
            Damageable damageable = (Damageable) meta;
            damageable.setDamage(data);
        }

        String name = section.getString("name");
        if (name != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        }

        var lore = section.getStringList("lore");
        if (!lore.isEmpty()) {
            List<String> parsedLore = new ArrayList<>(lore.size());
            for (var line : lore) {
                parsedLore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            meta.setLore(parsedLore);
        }

        item.setItemMeta(meta);
        return item;
    }

    public String getDatabaseAddress() {
        return this.databaseAddress;
    }

    public int getDatabasePort() {
        return this.databasePort;
    }

    public String getDatabaseDbName() {
        return this.databaseDbName;
    }

    public String getDatabaseUser() {
        return this.databaseUser;
    }

    public String getDatabasePass() {
        return this.databasePass;
    }

    public int getDatabaseMaxConnections() {
        return this.databaseMaxConnections;
    }

    public String getDatabaseTable() {
        return this.databaseTable;
    }

    public ItemStack getSpawnEgg() {
        return this.spawnEgg;
    }

    public int getInternalInvSize() {
        return this.internalInvSize;
    }

    public String getInternalInvTitle() {
        return this.internalInvTitle;
    }

    public ConfigurationSection getMenu() {
        return this.menu;
    }
}
