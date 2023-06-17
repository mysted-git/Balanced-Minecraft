package com.balancedmc.mixins.transport.horse;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.FrostWalkerEnchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FrostWalkerEnchantment.class)
public abstract class M_FrostWalkerEnchantment extends Enchantment {

    protected M_FrostWalkerEnchantment(Rarity weight, EnchantmentTarget target, EquipmentSlot[] slotTypes) {
        super(weight, target, slotTypes);
    }

    /**
     * Frost walker can be applied to horse armor
     */
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof HorseArmorItem || super.isAcceptableItem(stack);
    }
}
