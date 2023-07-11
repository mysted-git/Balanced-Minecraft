package com.balancedmc.mixins.potion;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrewingRecipeRegistry.class)
public abstract class M_BrewingRecipeRegistry {

    @Shadow public static void registerPotionRecipe(Potion input, Item item, Potion output) {}
    @Shadow public static void registerItemRecipe(Item input, Item ingredient, Item output) {}

    /**
     * Leaping is now brewed with a slimeball
     * Slow falling is now brewed with phantom membrane
     */
    @Inject(
            method = "registerPotionRecipe(Lnet/minecraft/potion/Potion;Lnet/minecraft/item/Item;Lnet/minecraft/potion/Potion;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void injected(Potion input, Item item, Potion output, CallbackInfo ci) {
        if (input == Potions.AWKWARD && item == Items.RABBIT_FOOT && output == Potions.LEAPING) {
            registerPotionRecipe(input, Items.SLIME_BALL, output);
            ci.cancel();
        } else if (input == Potions.AWKWARD && item == Items.PHANTOM_MEMBRANE && output == Potions.SLOW_FALLING) {
            registerPotionRecipe(input, Items.DRAGON_BREATH, output);
            ci.cancel();
        }
    }

    @Inject(
            method = "registerItemRecipe",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void registerItemRecipe(Item input, Item ingredient, Item output, CallbackInfo ci) {
        if (input == Items.SPLASH_POTION && ingredient == Items.DRAGON_BREATH && output == Items.LINGERING_POTION) {
            registerItemRecipe(input, Items.PHANTOM_MEMBRANE, output);
            ci.cancel();
        }
    }
}
