package com.gmail.woodyc40.minions.listener;

import com.gmail.woodyc40.minions.Constants;
import com.gmail.woodyc40.minions.MinionsNms;
import com.gmail.woodyc40.minions.config.Config;
import com.gmail.woodyc40.minions.data.MinionData;
import com.google.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import static java.lang.String.format;

public class SpawnListener implements Listener {
    private final Constants constants;
    private final MinionsNms nms;
    private final Config config;

    @Inject
    public SpawnListener(Constants constants, MinionsNms nms, Config config) {
        this.constants = constants;
        this.nms = nms;
        this.config = config;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (!pdc.has(this.constants.getSpawnEggKey(), PersistentDataType.STRING)) {
            return;
        }

        Player player = event.getPlayer();

        Block clickedBlock = event.getClickedBlock();
        BlockFace clickedFace = event.getBlockFace();
        Block spawnBlock = clickedBlock.getRelative(clickedFace);
        Location spawnBlockLocation = spawnBlock.getLocation();

        Location spawnLocation = spawnBlockLocation.add(0.5, 0, 0.5);
        spawnLocation.setYaw(player.getLocation().getYaw() + 180);

        MinionData data = new MinionData();
        data.setOwner(player.getUniqueId());
        data.setInventory(Bukkit.createInventory(null,
                this.config.getInternalInvSize(),
                this.config.getInternalInvTitle()));
        this.nms.spawnMinion(spawnLocation, data, player);

        event.setCancelled(true);

        EquipmentSlot hand = event.getHand();

        int newAmount = item.getAmount() - 1;
        if (newAmount == 0) {
            setHand(player, hand, null);
        } else {
            item.setAmount(newAmount);
            setHand(player, hand, item);
        }

        player.sendMessage("You've spawned a new minion!");
    }

    private static void setHand(Player player, EquipmentSlot slot, ItemStack item) {
        PlayerInventory inv = player.getInventory();
        if (slot == EquipmentSlot.HAND) {
            inv.setItemInMainHand(item);
        } else if (slot == EquipmentSlot.OFF_HAND) {
            inv.setItemInOffHand(item);
        } else {
            throw new IllegalArgumentException(format("'%s' is not a valid hand slot", slot));
        }
    }
}
