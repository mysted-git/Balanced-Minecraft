package com.balancedmc.potion;

import com.balancedmc.items.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModPotionRecipes {

    public static void registerPotionRecipes() {
        BrewingRecipeRegistry.registerPotionType(Items.DRAGON_BREATH);
        BrewingRecipeRegistry.registerItemRecipe(Items.DRAGON_BREATH, Items.BLAZE_ROD, ModItems.MINGLING_POTION_ITEM);
    }

}
