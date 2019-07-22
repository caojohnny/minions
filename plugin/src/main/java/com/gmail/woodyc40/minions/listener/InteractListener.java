package com.gmail.woodyc40.minions.listener;

import com.gmail.woodyc40.minions.Minion;
import com.gmail.woodyc40.minions.MinionsNms;
import com.gmail.woodyc40.minions.menu.MenuFactory;
import com.gmail.woodyc40.minions.menu.MinionsMenu;
import com.google.inject.Inject;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.UUID;

public class InteractListener implements Listener {
    private static final String ADMIN_PERM = "minions.admin";

    private final MinionsNms nms;
    private final MenuFactory mf;

    @Inject
    public InteractListener(MinionsNms nms, MenuFactory mf) {
        this.nms = nms;
        this.mf = mf;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractAtEntityEvent event) {
        Entity entity = event.getRightClicked();
        Minion minion = this.nms.getMinion(entity);
        if (minion == null) {
            return;
        }

        Player player = event.getPlayer();
        UUID id = player.getUniqueId();
        if (!player.hasPermission(ADMIN_PERM) &&
                !id.equals(minion.getOwner())) {
            return;
        }

        MinionsMenu menu = this.mf.newMinionsMenu(minion);
        menu.showTo(player);

        event.setCancelled(true);
    }
}
