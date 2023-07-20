package com.balancedmc.mixins.trident;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Riptide is compatible with loyalty and channeling<br>
 * When in water, riptide takes effect<br>
 * When on land, loyalty/channeling takes effect
 */
@Mixin(TridentItem.class)
public abstract class M_TridentItem {

    private LivingEntity owner;

    /**
     * Riptide does not block trident use on land
     */
    @Redirect(
            method = "use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/TypedActionResult;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/enchantment/EnchantmentHelper;getRiptide(Lnet/minecraft/item/ItemStack;)I"
            )
    )
    private int redirect_use(ItemStack stack) {
        return 0;
    }

    /**
     * Update owner
     */
    @Inject(
            method = "onStoppedUsing(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)V",
            at = @At("HEAD")
    )
    private void injected(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
        this.owner = user;
    }

    /**
     * Tridents with riptide work on land<br>
     * Rain does not affect riptide
     */
    @Redirect(
            method = "onStoppedUsing(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/enchantment/EnchantmentHelper;getRiptide(Lnet/minecraft/item/ItemStack;)I"
            )
    )
    private int redirect_onStoppedUsing(ItemStack stack) {
        if (this.owner == null) {
            return 0;
        }
        if (this.owner.isTouchingWater()) {
            return EnchantmentHelper.getRiptide(stack);
        }
        else {
            return 0;
        }
    }
}
