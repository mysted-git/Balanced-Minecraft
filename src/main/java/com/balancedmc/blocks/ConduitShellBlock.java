package com.balancedmc.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ConduitShellBlock extends Block {

    public ConduitShellBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (hand != Hand.MAIN_HAND) return ActionResult.PASS;
        ItemStack item = player.getMainHandStack();

        if (item.getItem() == Items.HEART_OF_THE_SEA) {
            item.decrement(1);
            boolean waterlogged = state.get(Properties.WATERLOGGED);
            BlockState newState = Blocks.CONDUIT.getDefaultState().with(Properties.WATERLOGGED, waterlogged);
            world.setBlockState(pos, newState);
        }
        return ActionResult.SUCCESS;
    }
}
