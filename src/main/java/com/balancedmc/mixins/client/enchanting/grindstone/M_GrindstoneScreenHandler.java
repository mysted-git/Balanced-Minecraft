package com.balancedmc.mixins.client.enchanting.grindstone;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(GrindstoneScreenHandler.class)
public abstract class M_GrindstoneScreenHandler extends ScreenHandler {

    protected M_GrindstoneScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    /**
     * @author HB0P
     * @reason Grindstones transfer enchantments to books
     */

    @Shadow
    Inventory input;
    @Shadow
    Inventory result;
    private EnchantmentLevelEntry removedEnchantment;
    private int experienceChange;
    private PlayerEntity player;

    @Inject(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V", at = @At("TAIL"))
    private void injected(int syncId, PlayerInventory playerInventory, final ScreenHandlerContext context, CallbackInfo ci) {
        this.player = playerInventory.player;
    }

    @Redirect(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/GrindstoneScreenHandler;addSlot(Lnet/minecraft/screen/slot/Slot;)Lnet/minecraft/screen/slot/Slot;"))
    private Slot redirect(GrindstoneScreenHandler handler, Slot slot) {

        if (slot.inventory != this.input && slot.inventory != this.result) {
            return this.addSlot(slot);
        }

        switch (slot.getIndex()) {
            case 0 -> {
                return this.addSlot(new Slot(this.input, 0, 49, 19) {
                    public boolean canInsert(ItemStack stack) {
                        return stack.hasEnchantments();
                    }
                });
            }

            case 1 -> {
                return this.addSlot(new Slot(this.input, 1, 49, 40) {
                    public boolean canInsert(ItemStack stack) {
                        return stack.isOf(Items.BOOK) && stack.getCount() == 1 && !this.hasStack();
                    }
                });
            }

            case 2 -> {
                return this.addSlot(new Slot(this.result, 2, 129, 34) {
                    public boolean canInsert(ItemStack stack) {
                        return false;
                    }

                    public void onTakeItem(PlayerEntity player, ItemStack stack) {
                        input.setStack(0, ItemStack.EMPTY);
                        if (input.getStack(1).isOf(Items.BOOK)) {
                            input.setStack(1, EnchantedBookItem.forEnchantment(removedEnchantment));
                            player.addExperienceLevels(-experienceChange);
                        }
                        else {
                            player.addExperience(experienceChange * 4);
                        }
                        player.playSound(SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS, 0.5F, 1);
                    }
                });
            }

            default -> {
                return null;
            }
        }
    }

    @Overwrite
    public void updateResult() {
        ItemStack input = this.input.getStack(0);

        if (input == ItemStack.EMPTY) {
            this.result.clear();
            return;
        }
        if (!this.getSlot(1).getStack().isOf(Items.BOOK) && this.getSlot(1).hasStack()) {
            this.result.clear();
            return;
        }

        Map<Enchantment, Integer> enchantmentLevels = EnchantmentHelper.get(input);
        Enchantment[] enchantments = enchantmentLevels.keySet().toArray(Enchantment[]::new);
        Enchantment toRemove = enchantments[(int) (Math.random() * enchantments.length)];
        removedEnchantment = new EnchantmentLevelEntry(toRemove, enchantmentLevels.get(toRemove));

        experienceChange = removedEnchantment.level * 2;
        if (experienceChange > player.experienceLevel && this.getSlot(1).hasStack()) {
            this.result.clear();
            return;
        }

        ItemStack result = input.copy();
        result.removeSubNbt("Enchantments");
        result.removeSubNbt("StoredEnchantments");

        for (Enchantment enchantment : enchantments) {
            if (enchantment == Enchantments.MENDING) {
                this.result.clear();
                return;
            }
            if (enchantment != removedEnchantment.enchantment) {
                result.addEnchantment(enchantment, enchantmentLevels.get(enchantment));
            }
        }

        this.result.setStack(0, result);
    }
}
