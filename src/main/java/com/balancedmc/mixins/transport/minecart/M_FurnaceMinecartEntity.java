package com.balancedmc.mixins.transport.minecart;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FurnaceMinecartEntity.class)
public abstract class M_FurnaceMinecartEntity {

    @Shadow private int fuel;

    /**
     * Remove fuel limit
     */
    @Redirect(
            method = "interact(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/vehicle/FurnaceMinecartEntity;fuel:I",
                    opcode = Opcodes.GETFIELD
            ),
            slice = @Slice(
                    to = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/entity/player/PlayerAbilities;creativeMode:Z"
                    )
            )
    )
    private int redirect(FurnaceMinecartEntity cart) {
        return 0;
    }

    /**
     * Remove old fuel behaviour
     */
    @Redirect(
            method = "interact(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/vehicle/FurnaceMinecartEntity;fuel:I",
                    opcode = Opcodes.PUTFIELD
            )
    )
    private void redirect(FurnaceMinecartEntity cart, int value) {}

    /**
     * Change fuel validation check
     */
    @Redirect(
            method = "interact(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/recipe/Ingredient;test(Lnet/minecraft/item/ItemStack;)Z"
            )
    )
    private boolean redirect(Ingredient instance, ItemStack itemStack) {
        return AbstractFurnaceBlockEntity.canUseAsFuel(itemStack);
    }

    /**
     * Increment fuel based on fuel time
     */
    @Inject(
            method = "interact(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/vehicle/FurnaceMinecartEntity;fuel:I",
                    opcode = Opcodes.PUTFIELD
            )
    )
    private void injected(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        this.fuel += (AbstractFurnaceBlockEntity.createFuelTimeMap().getOrDefault(player.getStackInHand(hand).getItem(), 0) * 2.25);
    }

    /**
     * Double furnace minecart speed
     */
    @Inject(
            method = "getMaxSpeed()D",
            at = @At("RETURN"),
            cancellable = true
    )
    private void injected(CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(cir.getReturnValueD() * 4);
    }
}
