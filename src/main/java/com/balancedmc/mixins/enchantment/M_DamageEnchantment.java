package com.balancedmc.mixins.enchantment;

import com.balancedmc.entity.ModEntityGroup;
import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DamageEnchantment.class)
public abstract class M_DamageEnchantment extends Enchantment {

    protected M_DamageEnchantment(Rarity weight, EnchantmentTarget target, EquipmentSlot[] slotTypes) {
        super(weight, target, slotTypes);
    }

    @Shadow @Final private static final int[] BASE_POWERS = new int[]{1, 5, 5, 5, 5, 5, 5};
    @Shadow @Final private static final int[] POWERS_PER_LEVEL = new int[]{11, 8, 8, 8, 8, 8, 8};
    @Shadow @Final private static final int[] MIN_MAX_POWER_DIFFERENCES = new int[]{20, 20, 20, 20, 20, 20, 20};
    @Shadow @Final public int typeIndex;

    /**
     * Sharpness is nerfed such that level 5 is equal to the old level 3
     */
    @Inject(
            method = "getAttackDamage(ILnet/minecraft/entity/EntityGroup;)F",
            at = @At("HEAD"),
            cancellable = true
    )
    private void injectedHead(int level, EntityGroup group, CallbackInfoReturnable<Float> cir) {
        if (this.typeIndex == 0) {
            cir.setReturnValue(1.0F + (float)Math.max(0, (level * 0.6) - 1) * 0.5F);
        }
    }

    /**
     * Bane of the Aquatic & Bane of Illagers work the same as Smite & BoA
     */
    @Inject(
            method = "getAttackDamage(ILnet/minecraft/entity/EntityGroup;)F",
            at = @At("TAIL"),
            cancellable = true
    )
    private void injectedTail(int level, EntityGroup group, CallbackInfoReturnable<Float> cir) {
        if (this.typeIndex == 3 && group == EntityGroup.AQUATIC) {
            cir.setReturnValue(level * 2.5f);
        }
        else if (this.typeIndex == 4 && group == EntityGroup.ILLAGER) {
            cir.setReturnValue(level * 2.5f);
        }
        else if (this.typeIndex == 5 && group == ModEntityGroup.NETHER) {
            cir.setReturnValue(level * 2.5f);
        }
        else if (this.typeIndex == 6 && group == ModEntityGroup.VILLAGER) {
            cir.setReturnValue(20f);
        }
    }

    /**
     * Bane of villagers max level is 1
     */
    @Inject(
            method = "getMaxLevel()I",
            at = @At("RETURN"),
            cancellable = true
    )
    private void injected(CallbackInfoReturnable<Integer> cir) {
        if (this.typeIndex == 6) {
            cir.setReturnValue(1);
        }
    }
}
