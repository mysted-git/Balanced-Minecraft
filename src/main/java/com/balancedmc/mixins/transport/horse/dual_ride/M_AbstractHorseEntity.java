package com.balancedmc.mixins.transport.horse.dual_ride;

import com.balancedmc.Main;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractHorseEntity.class)
public abstract class M_AbstractHorseEntity extends AnimalEntity {

    protected M_AbstractHorseEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(
            method = "interactMob(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/passive/AbstractHorseEntity;hasPassengers()Z"
            )
    )
    private boolean redirect(AbstractHorseEntity horse) {
        return horse.getPassengerList().size() >= 2;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengerList().size() < 2;
    }

    @Inject(
            method = "updatePassengerPosition(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity$PositionUpdater;)V",
            at = @At("TAIL")
    )
    private void injected(Entity passenger, PositionUpdater positionUpdater, CallbackInfo ci) {
        if (this.getPassengerList().size() < 2) return;

        int i = (this.getPassengerList().indexOf(passenger));
        float horizontal_offset = (float)((this.isRemoved() ? (double)0.01f : this.getMountedHeightOffset()) + passenger.getHeightOffset());
        float x_offset = i == 0 ? 0.2f : -0.6f;
        Vec3d vec3d = new Vec3d(x_offset, 0.0, 0.0).rotateY(-this.getYaw() * ((float)Math.PI / 180) - 1.5707964f);
        passenger.setPosition(this.getX() + vec3d.x,this.getY() + (double)horizontal_offset,this.getZ() + vec3d.z);
    }
}
