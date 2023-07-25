package com.balancedmc.mixins.trim;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Optional;

@Mixin(ArmorTrim.class)
public abstract class M_ArmorTrim {

    @Redirect(
            method = "apply(Lnet/minecraft/registry/DynamicRegistryManager;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/trim/ArmorTrim;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"
            )
    )
    private static boolean redirect_apply(ItemStack stack, TagKey<Item> tag) {
        return stack.isIn(tag) || stack.isIn(ItemTags.TOOLS);
    }

    @Redirect(
            method = "getTrim(Lnet/minecraft/registry/DynamicRegistryManager;Lnet/minecraft/item/ItemStack;)Ljava/util/Optional;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"
            )
    )
    private static boolean redirect_getTrim(ItemStack stack, TagKey<Item> tag) {
        return stack.isIn(tag) || stack.isIn(ItemTags.TOOLS);
    }

    /**
     * Change the tooltip text for tool armour trims
     */
    @Inject(
            method = "appendTooltip(Lnet/minecraft/item/ItemStack;Lnet/minecraft/registry/DynamicRegistryManager;Ljava/util/List;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    private static void injected(ItemStack stack, DynamicRegistryManager registryManager, List<Text> tooltip, CallbackInfo ci, Optional<ArmorTrim> optional) {
        if (stack.isIn(ItemTags.TOOLS)) {
            Text text = optional.get().getMaterial().value().description();
            tooltip.add(ScreenTexts.space().append(Text.of(text.getString().split(" ")[0] + " Trim").copy().fillStyle(text.getStyle())));
            ci.cancel();
        }
    }
}
