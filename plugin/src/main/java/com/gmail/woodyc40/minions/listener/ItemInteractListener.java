package com.gmail.woodyc40.minions.listener;

import com.gmail.woodyc40.minions.Minion;
import com.gmail.woodyc40.minions.MinionsNms;
import com.google.inject.Inject;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.EnumSet;

public class ItemInteractListener implements Listener {
    private static final EnumSet<Material> PICKAXES =
            EnumSet.of(Material.AIR,
                    Material.WOODEN_PICKAXE,
                    Material.STONE_PICKAXE,
                    Material.IRON_PICKAXE,
                    Material.GOLDEN_PICKAXE,
                    Material.DIAMOND_PICKAXE);

    private final MinionsNms nms;

    @Inject
    public ItemInteractListener(MinionsNms nms) {
        this.nms = nms;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onInteract(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        if (!player.isSneaking()) {
            return;
        }

        PlayerInventory inventory = player.getInventory();
        ItemStack hand = inventory.getItemInMainHand();
        if (!PICKAXES.contains(hand.getType())) {
            return;
        }

        Entity entity = event.getRightClicked();
        Minion minion = this.nms.getMinion(entity);
        if (minion != null) {
            event.setCancelled(true);

            ItemStack tool = minion.getTool();
            inventory.setItemInMainHand(tool);

            minion.setTool(hand);
        }
    }
}
