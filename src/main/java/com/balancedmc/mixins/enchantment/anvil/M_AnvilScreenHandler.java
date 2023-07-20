package com.balancedmc.mixins.enchantment.anvil;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.screen.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilScreenHandler.class)
public abstract class M_AnvilScreenHandler extends ForgingScreenHandler {

    @Shadow @Final public static int INPUT_1_ID;

    public M_AnvilScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    /**
     * Remove "too expensive" functionality
     */
    @Redirect(method = "updateResult()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/Property;get()I"))
    private int redirect(Property levelCost) {
        return 1;
    }

    /**
     * Allow level 5 environmental protection enchantments to be applied<br>
     * Do not allow combining level 4 books
     */
    @Redirect(method = "updateResult()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I"))
    private int redirect(Enchantment enchantment) {
        return enchantment instanceof ProtectionEnchantment && !getSlot(INPUT_1_ID).getStack().isOf(Items.ENCHANTED_BOOK)  ? 5 : enchantment.getMaxLevel();
    }
}
