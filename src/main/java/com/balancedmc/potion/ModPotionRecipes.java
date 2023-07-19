package com.balancedmc.potion;

import com.balancedmc.items.ModItems;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;

public class ModPotionRecipes {

    public static void registerPotionRecipes() {
        BrewingRecipeRegistry.registerPotionType(Items.DRAGON_BREATH);
        BrewingRecipeRegistry.registerItemRecipe(Items.DRAGON_BREATH, Items.BLAZE_ROD, ModItems.MINGLING_POTION_ITEM);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.AWKWARD, Items.RABBIT_FOOT, Potions.LUCK);
    }

}
