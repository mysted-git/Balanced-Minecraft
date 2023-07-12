package com.balancedmc.mixins.enchantment.groups;

import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Vex are considered illagers
 */
@Mixin(VexEntity.class)
public abstract class M_VexEntity extends HostileEntity {

    protected M_VexEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.ILLAGER;
    }
}
