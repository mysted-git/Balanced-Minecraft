package com.balancedmc.mixins.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantments.class)
public abstract class M_Enchantments {

    /**
     * Remove protection, sharpness, and infinity enchantments
     */
    @Inject(
            method = "register(Ljava/lang/String;Lnet/minecraft/enchantment/Enchantment;)Lnet/minecraft/enchantment/Enchantment;",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void inject(String name, Enchantment enchantment, CallbackInfoReturnable<Enchantment> cir) {
        if (name.equals("protection") || name.equals("infinity") || name.equals("sharpness")) {
            cir.cancel();
        }
    }
}
