package com.balancedmc.mixins.trim;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ArmorTrim.class)
public abstract class M_ArmorTrim {

    @Redirect(
            method = "apply(Lnet/minecraft/registry/DynamicRegistryManager;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/trim/ArmorTrim;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"
            )
    )
    private static boolean redirect(ItemStack stack, TagKey<Item> tag) {
        return stack.isIn(tag) || stack.isIn(ItemTags.TOOLS);
    }
}
