package com.balancedmc.mixins;

import net.minecraft.block.WitherSkullBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WitherSkullBlock.class)
public abstract class M_WitherSkullBlock {

    /**
     * Wither requires sky access or nether to spawn
     */
    @Redirect(method = "onPlaced(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/SkullBlockEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/pattern/BlockPattern;searchAround(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/pattern/BlockPattern$Result;"))
    private static BlockPattern.Result redirect(BlockPattern pattern, WorldView world, BlockPos pos) {
        // allow nether
        if (world.getDimension().hasCeiling()) {
            return pattern.searchAround(world, pos);
        }
        // require sky access
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (!world.isSkyVisible(pos.add(x, 0, z))) {
                    return null;
                }
            }
        }
        return pattern.searchAround(world, pos);
    }
}
