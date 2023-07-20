package com.balancedmc.mixins.enchantment.groups;

import com.balancedmc.entity.ModEntityGroup;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZoglinEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ZoglinEntity.class)
public abstract class M_ZoglinEntity extends HostileEntity {

    protected M_ZoglinEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    /**
     * @author HB0P
     * @reason Change zoglin entity group
     */
    @Override
    @Overwrite
    public EntityGroup getGroup() {
        return ModEntityGroup.NETHER;
    }
}
