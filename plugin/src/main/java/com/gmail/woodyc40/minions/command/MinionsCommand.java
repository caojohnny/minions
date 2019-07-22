package com.gmail.woodyc40.minions.command;

import com.gmail.woodyc40.minions.Constants;
import com.gmail.woodyc40.minions.config.Config;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

@Singleton
public class MinionsCommand implements CommandExecutor, TabCompleter {
    private static final String GIVE_PERMISSION = "minions.give";

    private final Constants constants;
    private final Config config;

    @Inject
    public MinionsCommand(Constants constants, Config config) {
        this.constants = constants;
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("give")) {
                if (!sender.hasPermission(GIVE_PERMISSION)) {
                    sender.sendMessage("You do not have permission to use the give command");
                    return true;
                }

                if (args[1].equalsIgnoreCase("all")) {
                    this.giveAllEgg(sender, 1);
                    return true;
                }

                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) {
                    sender.sendMessage(format("Player '%s' is offline", args[1]));
                    return true;
                }

                this.giveEgg(sender, target, 1);

                return true;
            }
        }

        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("give")) {
                if (!sender.hasPermission(GIVE_PERMISSION)) {
                    sender.sendMessage("You do not have permission to use the give command");
                    return true;
                }

                int amount;
                try {
                    amount = Integer.parseInt(args[2]);
                    if (amount < 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(format("'%s' is not a valid number", args[2]));
                    return true;
                }

                if (args[1].equalsIgnoreCase("all")) {
                    this.giveAllEgg(sender, amount);
                    return true;
                }

                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) {
                    sender.sendMessage(format("Player '%s' is offline", args[1]));
                    return true;
                }

                this.giveEgg(sender, target, amount);

                return true;
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("give");
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("give") && sender.hasPermission(GIVE_PERMISSION)) {
                return Stream.concat(Stream.of("all"),
                        Bukkit.getOnlinePlayers()
                                .stream()
                                .map(Player::getName))
                        .collect(Collectors.toList());
            }
        }

        return null;
    }

    private ItemStack createNewEgg(int amount) {
        ItemStack newEgg = this.config.getSpawnEgg().clone();

        int newAmount = newEgg.getAmount();
        newEgg.setAmount(newAmount * amount);

        ItemMeta meta = requireNonNull(newEgg.getItemMeta(), "Spawn egg is AIR");
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(this.constants.getSpawnEggKey(), PersistentDataType.STRING, "");
        newEgg.setItemMeta(meta);

        return newEgg;
    }

    private void giveEgg(CommandSender sender, Player target, int amount) {
        ItemStack egg = this.createNewEgg(amount);

        target.sendMessage(format("You received %d minion spawn eggs", egg.getAmount()));
        sender.sendMessage(format("You gave %s %dx minion spawn eggs", target.getName(), egg.getAmount()));

        PlayerInventory inventory = target.getInventory();
        for (var remaining : inventory.addItem(egg).values()) {
            target.getWorld().dropItem(target.getLocation(), remaining);
        }
    }

    private void giveAllEgg(CommandSender sender, int amount) {
        ItemStack egg = this.createNewEgg(amount);

        sender.sendMessage(format("You gave everyone %dx minion spawn eggs", egg.getAmount()));

        for (var target : Bukkit.getOnlinePlayers()) {
            PlayerInventory inventory = target.getInventory();
            for (var remaining : inventory.addItem(egg).values()) {
                target.getWorld().dropItem(target.getLocation(), remaining);
            }
        }
    }
}
