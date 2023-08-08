package com.balancedmc.mixins.inventory;

import com.balancedmc.MainClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Render tool hotbar depending on hotbar mode
 */
@Mixin(InGameHud.class)
public abstract class M_InGameHud {

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
}
