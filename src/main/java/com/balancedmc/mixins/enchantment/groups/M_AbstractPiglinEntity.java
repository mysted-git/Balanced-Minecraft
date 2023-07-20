package com.balancedmc.mixins.enchantment.groups;

import com.balancedmc.entity.ModEntityGroup;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractPiglinEntity.class)
public abstract class M_AbstractPiglinEntity extends HostileEntity {

    protected M_AbstractPiglinEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public EntityGroup getGroup() {
        return ModEntityGroup.NETHER;
    }
}
