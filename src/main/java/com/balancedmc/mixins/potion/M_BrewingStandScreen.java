package com.balancedmc.mixins.potion;

import net.minecraft.client.gui.screen.ingame.BrewingStandScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(BrewingStandScreen.class)
public class M_BrewingStandScreen {

    @ModifyArg(
            method = "drawBackground",
            index = 5,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V",
                    ordinal = 1
            )
    )
    private int adjustWidth(int oldWidth) {
        return (int) Math.floor(18 * oldWidth/5);
    }

}
