package com.balancedmc.mixins.enchantment.frost_walker;

import com.balancedmc.enchantments.EnchantmentTags;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.FrostWalkerEnchantment;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Arrays;

@Mixin(FrostWalkerEnchantment.class)
public abstract class M_FrostWalkerEnchantment {

    /**
     * Prevent frost walker from freezing single water blocks
     */
    @Redirect(
            method = "freezeWater(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;canPlaceAt(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;)Z"
            )
    )
    private static boolean redirect(BlockState instance, WorldView world, BlockPos pos) {
        for (Direction direction : Arrays.stream(Direction.values()).filter((direction) -> direction.getAxis() != Direction.Axis.Y).toList()) {
            BlockState state = world.getBlockState(pos.offset(direction));
            if (state.isIn(EnchantmentTags.FROST_WALKER_ADJACENT) || state.getProperties().contains(Properties.WATERLOGGED)) {
                return instance.canPlaceAt(world, pos);
            }
        }
        return false;
    }
}
