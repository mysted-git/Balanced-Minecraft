package com.balancedmc.mixins.inventory;

import com.balancedmc.Main;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

/**
 * Bows prefer arrows in the arrow slot
 */
@Mixin(RangedWeaponItem.class)
public abstract class M_RangedWeaponItem {

    @Inject(
            method = "getHeldProjectile(Lnet/minecraft/entity/LivingEntity;Ljava/util/function/Predicate;)Lnet/minecraft/item/ItemStack;",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void injected(LivingEntity entity, Predicate<ItemStack> predicate, CallbackInfoReturnable<ItemStack> cir) {
        if (entity instanceof PlayerEntity player && predicate.test(player.getInventory().getStack(52))) {
            cir.setReturnValue(player.getInventory().getStack(52));
        }
    }
}
