package com.balancedmc.mixins.potion;

import com.balancedmc.Main;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.BrewingStandScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.BrewingStandScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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
        Main.LOGGER.info(String.valueOf(oldWidth));
        return 5 * oldWidth;
    }

}
