package com.balancedmc.mixins.conduit;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.List;

@Mixin(ConduitBlockEntity.class)
public abstract class M_ConduitBlockEntity {

    /**
     * @author HB0P
     * @reason Conduit custom effects
     */
    @Overwrite
    private static void givePlayersEffects(World world, BlockPos pos, List<BlockPos> activatingBlocks) {
        HashMap<Block, Integer> blockCounts = new HashMap<>();
        blockCounts.put(Blocks.PRISMARINE, 0);
        blockCounts.put(Blocks.PRISMARINE_BRICKS, 0);
        blockCounts.put(Blocks.DARK_PRISMARINE, 0);

        // count blocks
        for (BlockPos p : activatingBlocks) {
            BlockState blockState = world.getBlockState(p);
            for (Block block : blockCounts.keySet()) {
                if (blockState.isOf(block)) {
                    blockCounts.replace(block, blockCounts.get(block) + 1);
                }
            }
        }

        // calculate effect levels
        for (Block block : blockCounts.keySet()) {
            blockCounts.replace(block, blockCounts.get(block) / 14);
        }

        // get range bounding box
        int range = blockCounts.get(Blocks.PRISMARINE) * 32;
        if (range == 0) {
            return;
        }
        Box box = new Box(pos.add(-range, -range, -range), pos.add(range, range, range));

        // apply effects
        List<PlayerEntity> players = world.getNonSpectatingEntities(PlayerEntity.class, box);
        for (PlayerEntity player : players) {
            if (!pos.isWithinDistance(player.getBlockPos(), range)) continue;
            if (!player.isTouchingWaterOrRain()) continue;

            player.addStatusEffect(new StatusEffectInstance(StatusEffects.CONDUIT_POWER, 260, 0, true, true));
            int bricks = blockCounts.get(Blocks.PRISMARINE_BRICKS);
            if (bricks > 0) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, 260, bricks - 1, true, true));
            }
            int dark = blockCounts.get(Blocks.DARK_PRISMARINE);
            if (dark > 0) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 260, dark - 1, true, true));
            }
        }
    }

    /**
     * Conduit will activate if there are 14 prismarine blocks
     */
    @Inject(method = "updateActivatingBlocks(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Ljava/util/List;)Z", at = @At("TAIL"), cancellable = true)
    private static void updateActivatingBlocks(World world, BlockPos pos, List<BlockPos> activatingBlocks, CallbackInfoReturnable<Boolean> cir) {
        int count = 0;
        for (BlockPos p : activatingBlocks) {
            BlockState blockState = world.getBlockState(p);
            if (blockState.isOf(Blocks.PRISMARINE)) {
                count++;
            }
        }

        cir.setReturnValue(count >= 14);
    }
}
