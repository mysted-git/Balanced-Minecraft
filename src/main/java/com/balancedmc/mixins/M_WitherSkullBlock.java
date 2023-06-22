package com.balancedmc.mixins;

import net.minecraft.block.WitherSkullBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WitherSkullBlock.class)
public abstract class M_WitherSkullBlock {

    /**
     * Wither requires sky access or nether to spawn
     */
    @Redirect(
            method = "onPlaced(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/SkullBlockEntity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/pattern/BlockPattern;searchAround(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/pattern/BlockPattern$Result;"
            )
    )
    private static BlockPattern.Result redirect(BlockPattern pattern, WorldView world, BlockPos pos) {
        BlockPattern.Result result = pattern.searchAround(world, pos);
        if (result == null) return null;
        // allow nether
        if (world.getDimension().hasCeiling()) {
            return result;
        }
        // get centre block pos
        BlockPos centre = result.translate(1, 2, 0).getBlockPos();
        if (result.getUp() == Direction.UP) {
            centre = centre.up(3);
        }
        else if (result.getUp() == Direction.DOWN) {
            centre = centre.up();
        }
        else {
            centre = centre.offset(result.getUp()).up();
        }
        // require sky access
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                BlockPos test = centre.add(x, 0, z);
                BlockPos top = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, test);
                if (top.getY() > test.getY()) {
                    return null;
                }
            }
        }
        return result;
    }
}
