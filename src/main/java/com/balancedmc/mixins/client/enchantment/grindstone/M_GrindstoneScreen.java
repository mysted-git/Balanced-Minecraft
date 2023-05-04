package com.balancedmc.mixins.client.enchantment.grindstone;

import net.minecraft.client.gui.screen.ingame.GrindstoneScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Map;

@Mixin(GrindstoneScreen.class)
public abstract class M_GrindstoneScreen extends HandledScreen<GrindstoneScreenHandler> {

    public M_GrindstoneScreen(GrindstoneScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    /**
     * Add cost message to grindstone
     */
    @Override
    public void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        super.drawForeground(matrices, mouseX, mouseY);
        if (this.handler.getSlot(0).hasStack() && this.handler.getSlot(1).hasStack() && this.handler.getSlot(2).hasStack()) {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(this.handler.getSlot(0).getStack());
            int cost = 0;
            for (Enchantment enchantment : enchantments.keySet()) {
                if (EnchantmentHelper.getLevel(enchantment, this.handler.getSlot(2).getStack()) == 0) {
                    cost = enchantments.get(enchantment) * 2;
                    break;
                }
            }
            String text = "Cost: " + cost;
            fill(matrices, 87, 59, this.textRenderer.getWidth(text) + 91, 71, 1325400064);
            this.textRenderer.drawWithShadow(matrices, Text.of(text), 89, 61, 8453920);
        }
    }
}
