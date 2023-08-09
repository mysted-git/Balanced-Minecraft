package com.balancedmc.mixins.inventory;

import com.balancedmc.Main;
import com.balancedmc.MainClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

/**
 * Render tool hotbar depending on hotbar mode
 */
@Mixin(InGameHud.class)
public abstract class M_InGameHud {

    @Shadow @Final private static Identifier WIDGETS_TEXTURE;

    @Redirect(
            method = "renderHotbar(FLnet/minecraft/client/gui/DrawContext;)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/player/PlayerInventory;main:Lnet/minecraft/util/collection/DefaultedList;"
            )
    )
    private DefaultedList<ItemStack> redirect(PlayerInventory inventory) {
        return inventory.combinedInventory.get(MainClient.activeHotbar * 3);
    }

    @Redirect(
            method = "renderHotbar(FLnet/minecraft/client/gui/DrawContext;)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;WIDGETS_TEXTURE:Lnet/minecraft/util/Identifier;"
            ),
            slice = @Slice(
                    to = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/entity/player/PlayerInventory;selectedSlot:I"
                    )
            )
    )
    private Identifier redirect() {
        if (MainClient.activeHotbar == 0) {
            return WIDGETS_TEXTURE;
        }
        else {
            return new Identifier(Main.MOD_ID, "textures/gui/tool_hotbar.png");
        }
    }
}
