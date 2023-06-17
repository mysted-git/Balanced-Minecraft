package com.balancedmc.mixins.dragon;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShulkerEntity.class)
public abstract class M_ShulkerEntity extends GolemEntity {

    protected M_ShulkerEntity(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);
    }

    private boolean onSpike = false;

    /**
     * Check whether the shulker is on a spike
     */
    @Inject(
            method = "initialize",
            at = @At("TAIL")
    )
    private void injected(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData, NbtCompound entityNbt, CallbackInfoReturnable<EntityData> cir) {
        if (spawnReason == SpawnReason.NATURAL) {
            onSpike = true;
        }
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
        if (onSpike) {
            cir.setReturnValue(false);
        }
    }

    /**
     * Stop shulkers on spikes dropping items
     */
    @Override
    public ItemEntity dropStack(ItemStack stack) {
        return onSpike ? null : super.dropStack(stack);
    }

    @Inject(
            method = "readCustomDataFromNbt(Lnet/minecraft/nbt/NbtCompound;)V",
            at = @At("TAIL")
    )
    private void injectedRead(NbtCompound nbt, CallbackInfo ci) {
        onSpike = nbt.getBoolean("OnSpike");
    }

    @Inject(
            method = "writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V",
            at = @At("TAIL")
    )
    private void injectedWrite(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean("OnSpike", onSpike);
    }
}
