package com.balancedmc.mixins;

import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Items.class)
public class M_Items {

    // Stackable Potions
    @ModifyArg(
        method = "<clinit>",
        slice = @Slice( from = @At(value = "NEW", target = "Lnet/minecraft/item/PotionItem;")),
        at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item$Settings;maxCount(I)Lnet/minecraft/item/Item$Settings;", ordinal = 0)
    )
    private static int potionMaxCount(int oldMax) {
        return 16;
    }

    @ModifyArg(
            method = "<clinit>",
            slice = @Slice( from = @At(value = "NEW", target = "Lnet/minecraft/item/SplashPotionItem;")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item$Settings;maxCount(I)Lnet/minecraft/item/Item$Settings;", ordinal = 0)
    )
    private static int splashPotionMaxCount(int oldMax) {
        return 16;
    }

    @ModifyArg(
            method = "<clinit>",
            slice = @Slice( from = @At(value = "NEW", target = "Lnet/minecraft/item/LingeringPotionItem;")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item$Settings;maxCount(I)Lnet/minecraft/item/Item$Settings;", ordinal = 0)
    )
    private static int lingeringPotionMaxCount(int oldMax) {
        return 16;
    }
}
