package com.balancedmc.mixins.transport.horse;

import net.minecraft.enchantment.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mixin(EnchantmentHelper.class)
public abstract class M_EnchantmentHelper {

    /**
     * Frost walker and soul speed work on horse armor
     */
    @Inject(method = "getEquipmentLevel(Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/entity/LivingEntity;)I", at = @At("HEAD"), cancellable = true)
    private static void injected(Enchantment enchantment, LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        if (entity instanceof HorseEntity horse) {
            Integer i = EnchantmentHelper.get(horse.getArmorType()).get(enchantment);
            cir.setReturnValue(Objects.requireNonNullElse(i, 0));
        }
    }

    /**
     * Remove check that an enchantment can be applied to horse armour
     */
    @Redirect(
            method = "getPossibleEntries(ILnet/minecraft/item/ItemStack;Z)Ljava/util/List;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/enchantment/EnchantmentTarget;isAcceptableItem(Lnet/minecraft/item/Item;)Z"
            )
    )
    private static boolean redirect(EnchantmentTarget instance, Item item) {
        if (item instanceof HorseArmorItem) {
            return true;
        }
        return instance.isAcceptableItem(item);
    }

    /**
     * Check that an enchantment can be applied to horse armour
     */
    @Inject(
            method = "getPossibleEntries(ILnet/minecraft/item/ItemStack;Z)Ljava/util/List;",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void injected(int power, ItemStack stack, boolean treasureAllowed, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
        if (stack.getItem() instanceof HorseArmorItem) {
            List<EnchantmentLevelEntry> result = new ArrayList<>();
            for (EnchantmentLevelEntry enchantment : cir.getReturnValue()) {
                if (enchantment.enchantment == Enchantments.SOUL_SPEED || enchantment.enchantment == Enchantments.FROST_WALKER || enchantment.enchantment instanceof ProtectionEnchantment) {
                    result.add(enchantment);
                }
            }
            cir.setReturnValue(result);
        }
    }
}
