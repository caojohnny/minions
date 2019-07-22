package com.gmail.woodyc40.minions.event;

import com.gmail.woodyc40.minions.Minion;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import org.checkerframework.checker.nullness.qual.NonNull;

public class MinionBreakBlockEvent extends BlockEvent implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Minion minion;
    private boolean cancelled;

    public MinionBreakBlockEvent(@NonNull Block theBlock, @NonNull Minion minion) {
        super(theBlock);
        this.minion = minion;
    }

    @NonNull
    public Minion getMinion() {
        return this.minion;
    }

    public static @NonNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NonNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
