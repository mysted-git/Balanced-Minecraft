package com.balancedmc.entity;

import com.balancedmc.Main;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {

    public static final EntityType<SentryShulkerEntity> SENTRY_SHULKER = FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, SentryShulkerEntity::new).fireImmune().spawnableFarFromPlayer().dimensions(EntityDimensions.fixed(1.0f, 1.0f)).trackRangeBlocks(10).build();

    public static void registerEntities() {
        Registry.register(Registries.ENTITY_TYPE, new Identifier(Main.MOD_ID, "sentry_shulker"), SENTRY_SHULKER);

        FabricDefaultAttributeRegistry.register(SENTRY_SHULKER, ShulkerEntity.createShulkerAttributes());
    }
}
