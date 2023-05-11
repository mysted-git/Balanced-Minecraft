package com.balancedmc.mixins.potions;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Deepslate can be instantly mined with haste 2
 */
@Mixin(PlayerEntity.class)
public abstract class M_PlayerEntity {

    @Inject(method = "getBlockBreakingSpeed(Lnet/minecraft/block/BlockState;)F", at = @At("RETURN"), cancellable = true)
    private void injected(BlockState block, CallbackInfoReturnable<Float> cir) {
        float f = cir.getReturnValueF();
        if (block.isOf(Blocks.DEEPSLATE) && f >= 47.6F) {
            cir.setReturnValue(f * 2);
        }
    }
}
