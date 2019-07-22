package com.gmail.woodyc40.minions.mode;

import com.gmail.woodyc40.minions.Minion;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class MiningMode1_14_2 extends MiningMode {
    @Override
    public int getExp(Minion minion, org.bukkit.block.Block block) {
        ItemStack tool = minion.getTool();
        net.minecraft.server.v1_14_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(tool);
        WorldServer ws = ((CraftWorld) block.getWorld()).getHandle();
        BlockPosition bp = new BlockPosition(block.getX(), block.getY(), block.getZ());
        IBlockData blockData = ws.getType(bp);

        return blockData.getBlock().getExpDrop(blockData, ws, bp, nmsItem);
    }

    @Override
    public int getBreakTicks(Minion minion, org.bukkit.block.Block block) {
        ItemStack tool = minion.getTool();
        net.minecraft.server.v1_14_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(tool);
        WorldServer ws = ((CraftWorld) block.getWorld()).getHandle();
        BlockPosition bp = new BlockPosition(block.getX(), block.getY(), block.getZ());
        IBlockData blockData = ws.getType(bp);

        float hardness = blockData.f(ws, bp);
        boolean canHarvest = blockData.getMaterial().isAlwaysDestroyable() ||
                nmsItem.getItem().canDestroySpecialBlock(blockData);
        double multiplier = canHarvest ? 1.5 : 5;
        double baseBreakTimeSeconds = hardness * multiplier;

        float toolMultiplier = nmsItem.a(blockData);
        if (toolMultiplier > 1.0F) {
            int i = tool.getEnchantmentLevel(Enchantment.DIG_SPEED);
            if (i > 0) {
                toolMultiplier += (float) (i * i + 1);
            }
        }

        return (int) Math.ceil(baseBreakTimeSeconds * 20 / toolMultiplier);
    }

    @Override
    public void showBreakAnimation(Block block, double breakPercentage) {
        BlockPosition bp = new BlockPosition(block.getX(), block.getY(), block.getZ());
        PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(0, bp, (byte) (breakPercentage * 9));
        block.getWorld().getNearbyEntities(block.getLocation(), 16, 16, 16, entity -> {
            if (entity.getType() == EntityType.PLAYER) {
                EntityPlayer ep = ((CraftPlayer) entity).getHandle();
                ep.playerConnection.sendPacket(packet);
            }

            return false;
        });
    }
}
