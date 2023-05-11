package com.balancedmc.mixins.enchantment.anvil;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilScreenHandler.class)
public abstract class M_AnvilScreenHandler {

    /**
     * Remove "too expensive" functionality
     */
    @Redirect(method = "updateResult()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/Property;get()I"))
    private int redirect(Property levelCost) {
        return 1;
    }

    /**
     * Allow level 5 environmental protection enchantments to be applied
     */
    @Redirect(method = "updateResult()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I"))
    private int redirect(Enchantment enchantment) {
        return enchantment instanceof ProtectionEnchantment ? 5 : enchantment.getMaxLevel();
    }
}
