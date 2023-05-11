package com.balancedmc.mixins.potions;

import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrewingRecipeRegistry.class)
public abstract class M_BrewingRecipeRegistry {

    /**
     * @author HB0P
     * @reason Remove vanilla leaping recipe
     */

    @Inject(method = "registerPotionRecipe(Lnet/minecraft/potion/Potion;Lnet/minecraft/item/Item;Lnet/minecraft/potion/Potion;)V", at = @At("HEAD"), cancellable = true)
    private static void injected(Potion input, Item item, Potion output, CallbackInfo ci) {
        if (output == Potions.LEAPING) {
            ci.cancel();
        }
    }
}
