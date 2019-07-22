package com.gmail.woodyc40.minions;

import com.gmail.woodyc40.minions.data.MinionData;
import com.gmail.woodyc40.minions.mode.MiningMode1_14_2;
import com.gmail.woodyc40.minions.mode.Mode;
import com.gmail.woodyc40.minions.mode.ModeProvider;
import com.gmail.woodyc40.minions.mode.ModeType;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Map;
import java.util.Objects;

public class MinionsNms1_14_2 implements MinionsNms, ModeProvider {
    private EntityTypes<EntityArmorStand> armorStandType = EntityTypes.ARMOR_STAND;

    @Override
    public void registerEntity() {
        armorStandType = register(1, "ridable_armor_stand", "armor_stand", EntityMinion::new, EnumCreatureType.MISC);
    }

    @Override
    public @Nullable Minion getMinion(org.bukkit.entity.Entity entity) {
        Entity handle = ((CraftEntity) entity).getHandle();
        if (handle instanceof EntityMinion) {
            return (EntityMinion) handle;
        }

        return null;
    }

    // Lifted from: https://www.spigotmc.org/threads/1-14-nms-registering-custom-entity.371482/
    private static <T extends Entity> EntityTypes<T> register(int id, String name, String superTypeName, EntityTypes.b producer, EnumCreatureType type) {
        Map<Object, Type<?>> dataTypes = (Map<Object, Type<?>>) DataConverterRegistry.a()
                .getSchema(DataFixUtils.makeKey(SharedConstants.a().getWorldVersion()))
                .findChoiceType(DataConverterTypes.ENTITY)
                .types();
        String keyName = "minecraft:" + name;
        dataTypes.put(keyName, dataTypes.get("minecraft:" + superTypeName));

        EntityTypes.a<T> a = EntityTypes.a.a(producer, type);
        return IRegistry.a(IRegistry.ENTITY_TYPE, id, keyName, a.a(name));
    }

    @Override
    public @NonNull Minion spawnMinion(@NonNull Location spawnLocation,
                                       @NonNull MinionData data,
                                       @NonNull Player spawner) {
        org.bukkit.World world = Objects.requireNonNull(spawnLocation.getWorld());
        WorldServer worldServer = ((CraftWorld) world).getHandle();

        EntityMinion ras = new EntityMinion(this.armorStandType, worldServer);
        ras.setPositionRotation(spawnLocation.getX(),
                spawnLocation.getY(),
                spawnLocation.getZ(),
                spawnLocation.getYaw(),
                spawnLocation.getPitch());

        ras.init(data, spawner);
        ras.getWorld().addEntity(ras);

        return ras;
    }

    @Override
    public @NonNull Minion respawnMinion(@NonNull Minion template,
                                         @NonNull MinionData data,
                                         @NonNull OfflinePlayer owner) {
        EntityMinion templateEntity = (EntityMinion) template;

        World world = templateEntity.getWorld();
        EntityMinion ras = new EntityMinion(this.armorStandType, world);
        ras.setPositionRotation(templateEntity.locX,
                templateEntity.locY,
                templateEntity.locZ,
                templateEntity.yaw,
                templateEntity.pitch);

        ras.setTool(template.getTool());
        ras.init(template.getPersistentId(), data, owner);
        world.addEntity(ras);

        return ras;
    }

    @Override
    public @Nullable Mode provideMode(ModeType typeName) {
        return getMode(typeName);
    }

    public static Mode getMode(ModeType typeName) {
        switch (typeName) {
            case MINING:
                return new MiningMode1_14_2();
            default:
                return null;
        }
    }
}
