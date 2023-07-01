package com.balancedmc.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Represents a shulker spawned on an obsidian pillar<br>
 * Cannot teleport<br>
 * Will not drop items
 */
public class SentryShulkerEntity extends ShulkerEntity {

    public SentryShulkerEntity(EntityType<? extends SentryShulkerEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected boolean tryTeleport() {
        return false;
    }

    @Override
    public ItemEntity dropStack(ItemStack stack) {
        return null;
    }
}
