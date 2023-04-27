package com.balancedmc.mixins.client.enchanting.anvil;

import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilScreenHandler.class)
public abstract class M_AnvilScreenHandler {

    /**
     * @author HB0P
     * @reason Remove "too expensive" functionality
     */

    @Redirect(method = "updateResult()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/Property;get()I"))
    private int redirect(Property levelCost) {
        return 1;
    }
}
