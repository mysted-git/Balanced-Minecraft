package com.balancedmc.mixins.enchantment.groups;

import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Witches are considered illagers
 */
@Mixin(WitchEntity.class)
public abstract class M_WitchEntity extends RaiderEntity {

    protected M_WitchEntity(EntityType<? extends RaiderEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.ILLAGER;
    }
}
