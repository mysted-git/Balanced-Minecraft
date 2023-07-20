package com.balancedmc.mixins.crafting;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundEvents;
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

    @Shadow public static void drawEntity(DrawContext context, int x, int y, int size, float mouseX, float mouseY, LivingEntity entity) {}

    /**
     * Move title
     */
    @Inject(
            method = "<init>(Lnet/minecraft/entity/player/PlayerEntity;)V",
            at = @At("TAIL")
    )
    private void injected(PlayerEntity player, CallbackInfo ci) {
        this.titleX = 33;
        this.titleY = 13;
    }

    /**
     * Remove/Move player model
     */
    @Redirect(
            method = "drawBackground",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;drawEntity(Lnet/minecraft/client/gui/DrawContext;IIIFFLnet/minecraft/entity/LivingEntity;)V"
            )
    )
    private void redirect(DrawContext context, int x, int y, int size, float mouseX, float mouseY, LivingEntity entity) {
        if (entity instanceof PlayerEntity player && player.currentScreenHandler.getSlot(0).isEnabled()) {
            drawEntity(context, x + 93, y, size, mouseX + 93, mouseY, entity);
        }
    }

    @Inject(
            method = "drawForeground",
            at = @At("TAIL")
    )
    private void injected(DrawContext context, int mouseX, int mouseY, CallbackInfo ci) {
        mouseX -= this.x;
        mouseY -= this.y;
        int y;
        if (this.handler.getSlot(0).isEnabled()) {
            if (mouseX >= 46 && mouseY >= 61 && mouseX <= 65 && mouseY <= 78) {
                y = 55;
            }
            else {
                y = 37;
            }
        }
        else {
            if (mouseX >= 46 && mouseY >= 61 && mouseX <= 65 && mouseY <= 78) {
                y = 18;
            }
            else {
                y = 0;
            }
        }
        context.drawTexture(BACKGROUND_TEXTURE, 46, 61, 178, y, 20, 18);
    }

    @Inject(
            method = "drawBackground",
            at = @At("TAIL")
    )
    private void injected(DrawContext context, float delta, int mouseX, int mouseY, CallbackInfo ci) {
        if (this.handler.getSlot(0).isEnabled()) {
            // crafting grid
            context.drawTexture(BACKGROUND_TEXTURE, this.x + 51, this.y + 24, 178, 74, 56, 36);
            // player background
            context.drawTexture(BACKGROUND_TEXTURE, this.x + 118, this.y + 7, 205, 0, 51, 72);
        }
        else {
            // input & arrow
            context.drawTexture(BACKGROUND_TEXTURE, this.x + 41, this.y + 34, 178, 112, 36, 18);
            // output
            context.drawTexture(BACKGROUND_TEXTURE, this.x + 78, this.y + 15, 164, 200, 92, 56);
        }
    }

    /* *** REMOVE RECIPE BOOK *** */

    @Shadow private float mouseX;
    @Shadow private float mouseY;

    @Shadow private boolean mouseDown;

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
        double x = mouseX - this.x;
        double y = mouseY - this.y;
        if (x >= 46 && y >= 61 && x <= 65 && y <= 78) {
            this.handler.onButtonClick(this.client.player, 0);
            this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
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
