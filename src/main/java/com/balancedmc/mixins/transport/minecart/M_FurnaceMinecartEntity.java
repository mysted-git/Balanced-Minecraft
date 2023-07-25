package com.balancedmc.mixins.transport.minecart;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RailBlock;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FurnaceMinecartEntity.class)
public abstract class M_FurnaceMinecartEntity extends AbstractMinecartEntity {

    protected M_FurnaceMinecartEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

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
        BlockState state = this.getWorld().getBlockState(this.getBlockPos());
        if (state.isIn(BlockTags.RAILS) && !(state.isOf(Blocks.RAIL) && isRailCurved(state.get(RailBlock.SHAPE)))) {
            cir.setReturnValue(cir.getReturnValue() * 2);
        }
    }

    private boolean isRailCurved(RailShape shape) {
        return shape == RailShape.NORTH_EAST || shape == RailShape.NORTH_WEST || shape == RailShape.SOUTH_EAST || shape == RailShape.SOUTH_WEST;
    }
}
