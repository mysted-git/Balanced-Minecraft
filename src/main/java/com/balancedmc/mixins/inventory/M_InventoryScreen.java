package com.balancedmc.mixins.inventory;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryScreen.class)
public abstract class M_InventoryScreen extends AbstractInventoryScreen<PlayerScreenHandler> implements RecipeBookProvider {

    public M_InventoryScreen(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Shadow public static void drawEntity(DrawContext context, int x, int y, int size, float mouseX, float mouseY, LivingEntity entity) {}

    @Shadow @Final private RecipeBookWidget recipeBook;

    @Shadow private boolean mouseDown;

    @Shadow @Final private static Identifier RECIPE_BUTTON_TEXTURE;

    /**
     * Move title
     */
    @Inject(
            method = "<init>(Lnet/minecraft/entity/player/PlayerEntity;)V",
            at = @At("TAIL")
    )
    private void injected(PlayerEntity player, CallbackInfo ci) {
        this.titleX = 79;
        this.titleY = 7;
    }

    /**
     * Move recipe book button
     */
    @Redirect(
            method = "init()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;"
            )
    )
    private Element redirect(InventoryScreen instance, Element element) {
        return this.addDrawableChild(new TexturedButtonWidget(this.x + 114, this.height / 2 - 23, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, button -> {
            this.recipeBook.toggleOpen();
            this.x = this.recipeBook.findLeftEdge(this.width, this.backgroundWidth);
            button.setPosition(this.x + 114, this.height / 2 - 23);
            this.mouseDown = true;
        }));
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
        drawEntity(context, x - 20, y, size, mouseX - 20, mouseY, entity);
    }

    /**
     * Draw buttons
     */
    @Inject(
            method = "drawForeground",
            at = @At("TAIL")
    )
    private void injected(DrawContext context, int mouseX, int mouseY, CallbackInfo ci) {
        mouseX -= this.x;
        mouseY -= this.y;
        int y;
        // inventory switcher
        if (this.handler.getSlot(this.handler.slots.size() - 1).isEnabled()) {
            // tool inventory
            if (mouseX >= 78 && mouseY >= 59 && mouseX <= 97 && mouseY <= 78) {
                y = 63;
            }
            else {
                y = 42;
            }
        }
        else {
            // normal inventory
            if (mouseX >= 78 && mouseY >= 59 && mouseX <= 97 && mouseY <= 78) {
                y = 21;
            }
            else {
                y = 0;
            }
        }
        context.drawTexture(BACKGROUND_TEXTURE, 78, 59, 178, y, 20, 20);
        // crafting mode switcher
        if (this.handler.getSlot(0).isEnabled()) {
            // crafting
            if (mouseX >= 150 && mouseY >= 59 && mouseX <= 169 && mouseY <= 78) {
                y = 63;
            }
            else {
                y = 42;
            }
        }
        else {
            // cutter
            if (mouseX >= 150 && mouseY >= 59 && mouseX <= 169 && mouseY <= 78) {
                y = 21;
            }
            else {
                y = 0;
            }
        }
        context.drawTexture(BACKGROUND_TEXTURE, 150, 59, 199, y, 20, 20);
    }

    @Inject(
            method = "drawBackground",
            at = @At("TAIL")
    )
    private void injected(DrawContext context, float delta, int mouseX, int mouseY, CallbackInfo ci) {
        if (this.handler.getSlot(0).isEnabled()) {
            // crafting grid
            context.drawTexture(BACKGROUND_TEXTURE, this.x + 79, this.y + 19, 33, 220, 90, 36);
        }
        else {
            // cutter
            context.drawTexture(BACKGROUND_TEXTURE, this.x + 79, this.y + 19, 123, 220, 90, 36);
        }
        if (this.handler.getSlot(this.handler.slots.size() - 1).isEnabled()) {
            // block slots when on tool inventory
            for (int i = 0; i < 2; i++) {
                context.drawTexture(BACKGROUND_TEXTURE, this.x + 43 + (i * 72), this.y + 83, 179, 84, 18, 54);
            }
        }
    }

    @Inject(
            method = "mouseClicked(DDI)Z",
            at = @At("TAIL")
    )
    private void injected(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        double x = mouseX - this.x;
        double y = mouseY - this.y;
        // inventory toggle
        if (x >= 78 && y >= 59 && x <= 97 && y <= 78) {
            this.handler.onButtonClick(this.client.player, 0);
            this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
        // crafting toggle
        else if (x >= 150 && y >= 59 && x <= 169 && y <= 78) {
            this.handler.onButtonClick(this.client.player, 1);
            this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }
}
