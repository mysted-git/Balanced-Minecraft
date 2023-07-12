package com.balancedmc.mixins.enchantment.groups;

import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Slime are considered aquatic
 */
@Mixin(SlimeEntity.class)
public abstract class M_SlimeEntity extends MobEntity {

    protected M_SlimeEntity(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.AQUATIC;
    }
}
