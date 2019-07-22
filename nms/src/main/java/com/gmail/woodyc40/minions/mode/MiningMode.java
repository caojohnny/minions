package com.gmail.woodyc40.minions.mode;

import com.gmail.woodyc40.minions.Minion;
import com.gmail.woodyc40.minions.event.MinionBreakBlockEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import static org.bukkit.Material.*;

public abstract class MiningMode implements Mode {
    private static final EnumSet<Material> IGNORED_TYPES =
            EnumSet.of(AIR, BEDROCK, CHEST, BARRIER,
                    TRAPPED_CHEST, ACACIA_WALL_SIGN,
                    BIRCH_WALL_SIGN, DARK_OAK_WALL_SIGN, JUNGLE_WALL_SIGN,
                    OAK_WALL_SIGN, SPRUCE_WALL_SIGN, ACACIA_SIGN, BIRCH_SIGN,
                    DARK_OAK_SIGN, JUNGLE_SIGN, OAK_SIGN, SPRUCE_SIGN);

    private Block block;
    private int breakTicks;
    private int ticksUntilBroken;

    @Override
    public void init(Minion minion) {
    }

    @Override
    public void tick(Minion minion) {
        ItemStack tool = minion.getTool();
        if (tool == null) {
            return;
        }

        Inventory inventory = minion.getInventory();
        if (inventory == null) {
            return;
        }

        ArmorStand entity = minion.getEntity();
        List<Block> list = entity.getLineOfSight(null, 1);
        if (list.size() != 2) {
            return;
        }

        Block block = list.get(1);
        Material type = block.getType();
        if (IGNORED_TYPES.contains(type)) {
            this.block = null;
            return;
        }

        if (this.block == null || this.block.getType() != type) {
            this.block = block;
            this.breakTicks = this.getBreakTicks(minion, block);
            this.ticksUntilBroken = this.breakTicks;
        }

        this.ticksUntilBroken--;
        this.showBreakAnimation(this.block, (breakTicks - this.ticksUntilBroken) / (double) breakTicks);

        if (this.ticksUntilBroken <= 0) {
            MinionBreakBlockEvent ev = new MinionBreakBlockEvent(this.block, minion);
            Bukkit.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                this.ticksUntilBroken = breakTicks;
                return;
            }

            Collection<ItemStack> drops = this.block.getDrops(tool);
            for (var drop : drops) {
                if (!inventory.addItem(drop).isEmpty()) {
                    this.ticksUntilBroken = breakTicks;
                    return;
                }
            }

            int exp = this.getExp(minion, this.block);
            if (!minion.addExperience(exp)) {
                this.ticksUntilBroken = breakTicks;
                return;
            }

            this.block.setType(AIR);
        }
    }

    @Override
    public void complete(Minion minion) {
        if (this.block != null) {
            this.showBreakAnimation(this.block, -1);
        }
    }

    @Override
    public ModeType getType() {
        return ModeType.MINING;
    }

    public abstract int getExp(Minion minion, Block block);

    public abstract int getBreakTicks(Minion minion, Block block);

    public abstract void showBreakAnimation(Block block, double breakPercentage);
}
