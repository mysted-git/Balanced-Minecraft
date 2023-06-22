package com.balancedmc.mixins.dragon;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShulkerEntity.class)
public abstract class M_ShulkerEntity extends GolemEntity {

    protected M_ShulkerEntity(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);
    }

    private boolean isOnMainIsland() {
        return this.getBlockPos().isWithinDistance(new Vec3i(0, this.getWorld().getTopY(Heightmap.Type.MOTION_BLOCKING, 0, 0), 0), 200);
    }

    /**
     * Prevent shulkers on spikes from teleporting
     */
    @Inject(
            method = "tryTeleport()Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void injected(CallbackInfoReturnable<Boolean> cir) {
        if (isOnMainIsland()) {
            cir.setReturnValue(false);
        }
    }

    /**
     * Stop shulkers on spikes dropping items
     */
    @Override
    public ItemEntity dropStack(ItemStack stack) {
        return isOnMainIsland() ? null : super.dropStack(stack);
    }
}
