package com.balancedmc.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Degradable;
import net.minecraft.block.Oxidizable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Copper oxidises faster when adjacent to water
 */
@Mixin(Oxidizable.class)
public interface M_Oxidizable extends Degradable<Oxidizable.OxidationLevel> {

    @Override
    default void tickDegradation(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        final float initial = 0.05688889f;
        float chance = initial;
        for (Direction direction : Direction.values()) {
            if (world.getBlockState(pos.offset(direction)).isOf(Blocks.WATER)) {
                chance += initial / 6;
            }
        }
        if (random.nextFloat() < chance) {
            this.tryDegrade(state, world, pos, random);
        }
    }
}
