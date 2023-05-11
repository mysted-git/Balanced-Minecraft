package com.balancedmc.mixins.transport.horse;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.HorseEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(EnchantmentHelper.class)
public abstract class M_EnchantmentHelper {

    /**
     * @author HB0P
     * @reason Frost walker and soul speed work on horse armor
     */

    @Inject(method = "getEquipmentLevel(Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/entity/LivingEntity;)I", at = @At("HEAD"), cancellable = true)
    private static void injected(Enchantment enchantment, LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        if (entity instanceof HorseEntity horse) {
            Integer i = EnchantmentHelper.get(horse.getArmorType()).get(enchantment);
            cir.setReturnValue(Objects.requireNonNullElse(i, 0));
        }
    }
}
