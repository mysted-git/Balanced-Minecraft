package com.balancedmc.mixins.client.enchanting;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantments.class)
public abstract class M_Enchantments {

    /**
     * @author HB0P
     * @reason Remove protection and infinity enchantments
     */

    @Inject(method = "register(Ljava/lang/String;Lnet/minecraft/enchantment/Enchantment;)Lnet/minecraft/enchantment/Enchantment;", at = @At("HEAD"), cancellable = true)
    private static void inject(String name, Enchantment enchantment, CallbackInfoReturnable<Enchantment> ci) {
        if (name.equals("protection") || name.equals("infinity")) {
            ci.cancel();
        }
    }
}
