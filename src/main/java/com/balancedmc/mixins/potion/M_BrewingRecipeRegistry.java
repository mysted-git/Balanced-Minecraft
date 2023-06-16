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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrewingRecipeRegistry.class)
public abstract class M_BrewingRecipeRegistry {

    @Shadow public static void registerPotionRecipe(Potion input, Item item, Potion output) {}

    /**
     * Leaping is now brewed with a slimeball
     */
    @Inject(
            method = "registerPotionRecipe(Lnet/minecraft/potion/Potion;Lnet/minecraft/item/Item;Lnet/minecraft/potion/Potion;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void injected(Potion input, Item item, Potion output, CallbackInfo ci) {
        if (item == Items.RABBIT_FOOT && output == Potions.LEAPING) {
            registerPotionRecipe(input, Items.SLIME_BALL, output);
            ci.cancel();
        }
    }
}
