package com.balancedmc.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Reinforced deepslate can be moved by pistons
 */
@Mixin(PistonBlock.class)
public abstract class M_PistonBlock {

    @Redirect(
            method = "isMovable(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;ZLnet/minecraft/util/math/Direction;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"
            )
    )
    private static boolean redirect(BlockState state, Block block) {
        if (block == Blocks.REINFORCED_DEEPSLATE) {
            return false;
        }
        return state.isOf(block);
    }
}
