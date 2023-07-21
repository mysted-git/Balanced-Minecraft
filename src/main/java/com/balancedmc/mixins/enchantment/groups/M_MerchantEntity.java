package com.balancedmc.mixins.enchantment.groups;

import com.balancedmc.entity.ModEntityGroup;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MerchantEntity.class)
public abstract class M_MerchantEntity extends PassiveEntity {

    protected M_MerchantEntity(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public EntityGroup getGroup() {
        return ModEntityGroup.VILLAGER;
    }
}
