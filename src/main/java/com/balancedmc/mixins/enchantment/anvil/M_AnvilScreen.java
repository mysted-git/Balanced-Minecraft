package com.balancedmc.mixins.enchantment.anvil;

import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.entity.player.PlayerAbilities;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilScreen.class)
public abstract class M_AnvilScreen {

    /**
     * @author HB0P
     * @reason Remove "too expensive" text
     */

    @Redirect(method = "drawForeground(Lnet/minecraft/client/gui/DrawContext;II)V", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerAbilities;creativeMode:Z", opcode = Opcodes.GETFIELD))
    private boolean redirect(PlayerAbilities abilities) {
        return true;
    }
}
