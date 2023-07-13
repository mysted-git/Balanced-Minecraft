package com.balancedmc.mixins.food;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.StewItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StewItem.class)
public abstract class M_StewItem {

    /**
     * Bowls are inserted into a new stack after consumption
     */
    @Inject(
            method = "finishUsing(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/item/ItemStack;",
            at = @At("TAIL"),
            cancellable = true
    )
    public void finishUsing(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if (user instanceof PlayerEntity player) {
            player.getInventory().offerOrDrop(new ItemStack(Items.BOWL));
        }
        cir.setReturnValue(stack);
    }
}
