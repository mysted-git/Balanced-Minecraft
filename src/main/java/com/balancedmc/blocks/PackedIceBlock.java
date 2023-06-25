package com.balancedmc.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

/**
 * Custom class for packed ice<br>
 * This makes it now melt
 */
public class PackedIceBlock extends Block {

    public PackedIceBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getDimension().ultrawarm()) {
            world.removeBlock(pos, false);
        }
        super.randomTick(state, world, pos, random);
    }
}
