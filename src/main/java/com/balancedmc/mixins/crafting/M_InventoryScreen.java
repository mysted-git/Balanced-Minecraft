package com.balancedmc.mixins.crafting;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class M_InventoryScreen extends AbstractInventoryScreen<PlayerScreenHandler> implements RecipeBookProvider {

    public M_InventoryScreen(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    /**
     * Move title
     */
    @Inject(
            method = "<init>(Lnet/minecraft/entity/player/PlayerEntity;)V",
            at = @At("TAIL")
    )
    private void injected(PlayerEntity player, CallbackInfo ci) {
        this.titleX = 33;
        this.titleY = 20;
    }

    /**
     * Remove player model
     */
    @Inject(
            method = "drawEntity(Lnet/minecraft/client/gui/DrawContext;IIIFFLnet/minecraft/entity/LivingEntity;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void injected(DrawContext context, int x, int y, int size, float mouseX, float mouseY, LivingEntity entity, CallbackInfo ci) {
        ci.cancel();
    }

    /* *** REMOVE RECIPE BOOK *** */

    @Shadow private float mouseX;
    @Shadow private float mouseY;

    @Redirect(
            method = "handledScreenTick()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/recipebook/RecipeBookWidget;update()V"
            )
    )
    private void redirect(RecipeBookWidget instance) {}

    @Inject(
            method = "init()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/recipebook/RecipeBookWidget;initialize(IILnet/minecraft/client/MinecraftClient;ZLnet/minecraft/screen/AbstractRecipeScreenHandler;)V"
            ),
            cancellable = true
    )
    private void inject(CallbackInfo ci) {
        ci.cancel();
    }

    /**
     * @author HB0P
     * @reason remove recipe book
     */
    @Overwrite
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    /**
     * @author HB0P
     * @reason remove recipe book
     */
    @Overwrite
    public boolean isPointWithinBounds(int x, int y, int width, int height, double pointX, double pointY) {
        return super.isPointWithinBounds(x, y, width, height, pointX, pointY);
    }

    /**
     * @author HB0P
     * @reason remove recipe book
     */
    @Overwrite
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * @author HB0P
     * @reason remove recipe book
     */
    @Overwrite
    public boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
        return mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.backgroundWidth) || mouseY >= (double)(top + this.backgroundHeight);
    }

    /**
     * @author HB0P
     * @reason remove recipe book
     */
    @Overwrite
    public void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
        super.onMouseClick(slot, slotId, button, actionType);
    }
}
