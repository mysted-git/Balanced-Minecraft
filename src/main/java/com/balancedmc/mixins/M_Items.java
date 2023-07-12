package com.balancedmc.mixins;

import net.minecraft.block.Block;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Items.class)
public abstract class M_Items {

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


    // some 16-stackables now stack to 64
    @Redirect(
            method = "<clinit>",
            at = @At(
                    value = "NEW",
                    target = "Lnet/minecraft/item/SignItem;*"
            )
    )
    private static SignItem signMaxCount(Item.Settings settings, Block standingBlock, Block wallBlock) {
        return new SignItem(settings.maxCount(64), standingBlock, wallBlock);
    }

    @Redirect(
            method = "<clinit>",
            at = @At(
                    value = "NEW",
                    target = "Lnet/minecraft/item/HangingSignItem;*"
            )
    )
    private static HangingSignItem hangingSignMaxCount(Block hangingSign, Block wallHangingSign, Item.Settings settings) {
        return new HangingSignItem(hangingSign, wallHangingSign, settings.maxCount(64));
    }

    @Redirect(
            method = "<clinit>",
            at = @At(
                    value = "NEW",
                    target = "Lnet/minecraft/item/BannerItem;*"
            )
    )
    private static BannerItem bannerMaxCount(Block bannerBlock, Block wallBannerBlock, Item.Settings settings) {
        return new BannerItem(bannerBlock, wallBannerBlock, settings.maxCount(64));
    }

    @Redirect(
            method = "<clinit>",
            at = @At(
                    value = "NEW",
                    target = "Lnet/minecraft/item/ArmorStandItem;*"
            )
    )
    private static ArmorStandItem armorStandMaxCount(Item.Settings settings) {
        return new ArmorStandItem(settings.maxCount(64));
    }
}
