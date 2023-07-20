package com.balancedmc.mixins.enchantment.groups;

import com.balancedmc.entity.ModEntityGroup;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(StriderEntity.class)
public abstract class M_StriderEntity extends AnimalEntity {

    protected M_StriderEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public EntityGroup getGroup() {
        return ModEntityGroup.NETHER;
    }
}
