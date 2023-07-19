package com.balancedmc.mixins.potion;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BrewingRecipeRegistry.class)
public abstract class M_BrewingRecipeRegistry {

    @Shadow public static void registerPotionRecipe(Potion input, Item item, Potion output) {}
    @Shadow public static void registerItemRecipe(Item input, Item ingredient, Item output) {}

    @Shadow @Final private static List<BrewingRecipeRegistry.Recipe<Item>> ITEM_RECIPES;

    @Shadow @Final private static List<Ingredient> POTION_TYPES;

    /**
     * Leaping is now brewed with a slimeball
     * Slow falling is now brewed with dragon's breath
     */

    @ModifyArg(
            method = "registerDefaults",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/recipe/BrewingRecipeRegistry;registerItemRecipe(Lnet/minecraft/item/Item;Lnet/minecraft/item/Item;Lnet/minecraft/item/Item;)V"
            ),
            index = 1
    )
    private static Item getLingeringIngredient(Item input) {
        if (input == Items.DRAGON_BREATH) {
            return Items.PHANTOM_MEMBRANE;
        }
        return input;
    }

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
    private static void mRegisterItemRecipe(Item input, Item ingredient, Item output, CallbackInfo ci) {
        if (!(input instanceof PotionItem || input == Items.DRAGON_BREATH)) {
            throw new IllegalArgumentException("Expected a potion, got: " + Registries.ITEM.getId(input));
        } else if (!(output instanceof PotionItem || output == Items.DRAGON_BREATH)) {
            throw new IllegalArgumentException("Expected a potion, got: " + Registries.ITEM.getId(output));
        } else {
            ITEM_RECIPES.add(new BrewingRecipeRegistry.Recipe(input, Ingredient.ofItems(new ItemConvertible[]{ingredient}), output));
        }
        ci.cancel();
    }

    @Inject(
            method = "registerPotionType",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void mRegisterPotionType(Item item, CallbackInfo ci) {
        if (!(item instanceof PotionItem || item == Items.DRAGON_BREATH)) {
            throw new IllegalArgumentException("Expected a potion, got: " + Registries.ITEM.getId(item));
        } else {
            POTION_TYPES.add(Ingredient.ofItems(new ItemConvertible[]{item}));
            ci.cancel();
        }
    }

}
