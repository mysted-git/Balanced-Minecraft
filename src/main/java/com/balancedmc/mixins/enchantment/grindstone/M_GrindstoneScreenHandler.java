package com.balancedmc.mixins.enchantment.grindstone;

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
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Grindstones transfer a single enchantment to a book<br>
 * If no book is provided, they will refund xp as usual
 */
@Mixin(GrindstoneScreenHandler.class)
public abstract class M_GrindstoneScreenHandler extends ScreenHandler {

    protected M_GrindstoneScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Shadow @Final
    Inventory input;
    @Shadow @Final
    private Inventory result;
    private EnchantmentLevelEntry removedEnchantment;
    private int experienceChange;
    private PlayerEntity player;

    /**
     * Store which player is using the screen
     */
    @Inject(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V", at = @At("TAIL"))
    private void injected(int syncId, PlayerInventory playerInventory, final ScreenHandlerContext context, CallbackInfo ci) {
        this.player = playerInventory.player;
    }

    /**
     * Change which items are allowed to be inserted into slots<br>
     * Input 1 can accept enchanted items without mending<br>
     * Input 2 can accept only a single book
     * <p>
     * Set behaviour when items are taken from output slot<br>
     * Enchants book if possible<br>
     * Refunds or charges experience
     */
    @Redirect(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/GrindstoneScreenHandler;addSlot(Lnet/minecraft/screen/slot/Slot;)Lnet/minecraft/screen/slot/Slot;"))
    private Slot redirect(GrindstoneScreenHandler handler, Slot slot) {

        if (slot.inventory != this.input && slot.inventory != this.result) {
            return this.addSlot(slot);
        }

        switch (slot.getIndex()) {
            // input 1
            case 0 -> {
                return this.addSlot(new Slot(this.input, 0, 49, 19) {
                    // allow insert if the item has enchantments other than curses, and does not have mending
                    public boolean canInsert(ItemStack stack) {
                        int enchantmentCount = EnchantmentHelper.get(stack).size();
                        if (EnchantmentHelper.hasBindingCurse(stack)) {
                            enchantmentCount--;
                        }
                        if (EnchantmentHelper.hasVanishingCurse(stack)) {
                            enchantmentCount--;
                        }
                        return enchantmentCount != 0 && EnchantmentHelper.getLevel(Enchantments.MENDING, stack) == 0;
                    }
                });
            }
            // input 2
            case 1 -> {
                return this.addSlot(new Slot(this.input, 1, 49, 40) {
                    public boolean canInsert(ItemStack stack) {
                        return stack.isOf(Items.BOOK) && stack.getCount() == 1 && !this.hasStack();
                    }
                });
            }
            // output
            case 2 -> {
                return this.addSlot(new Slot(this.result, 2, 129, 34) {
                    public boolean canInsert(ItemStack stack) {
                        return false;
                    }

                    public void onTakeItem(PlayerEntity player, ItemStack stack) {
                        input.setStack(0, ItemStack.EMPTY);
                        if (input.getStack(1).isOf(Items.BOOK)) {
                            input.setStack(1, EnchantedBookItem.forEnchantment(removedEnchantment));
                            if (!player.getAbilities().creativeMode) {
                                player.addExperienceLevels(-experienceChange);
                            }
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

    /**
     * @author HB0P
     * @reason Update the output slot with one fewer enchantment than the input
     */
    @Overwrite
    private void updateResult() {
        ItemStack input = this.input.getStack(0);

        // clear if there is no input
        if (input == ItemStack.EMPTY) {
            this.result.clear();
            return;
        }
        // do not allow enchanted books remaining in second slot
        if (!this.getSlot(1).getStack().isOf(Items.BOOK) && this.getSlot(1).hasStack()) {
            this.result.clear();
            return;
        }

        Map<Enchantment, Integer> enchantmentLevels = EnchantmentHelper.get(input);
        Set<Enchantment> enchantments = enchantmentLevels.keySet();
        Set<Enchantment> selectableEnchantments = new HashSet<>(enchantments);
        selectableEnchantments.remove(Enchantments.BINDING_CURSE);
        selectableEnchantments.remove(Enchantments.VANISHING_CURSE);
        Enchantment toRemove = selectableEnchantments.toArray(Enchantment[]::new)[(int) (Math.random() * selectableEnchantments.size())];
        removedEnchantment = new EnchantmentLevelEntry(toRemove, enchantmentLevels.get(toRemove));

        // stop if player does not have required experience
        experienceChange = removedEnchantment.level * 2;
        if (!player.getAbilities().creativeMode && experienceChange > player.experienceLevel && this.getSlot(1).hasStack()) {
            this.result.clear();
            return;
        }

        ItemStack result = input.copy();
        result.removeSubNbt("Enchantments");
        result.removeSubNbt("StoredEnchantments");

        // add all but one enchantment to output item
        for (Enchantment enchantment : enchantments) {
            if (enchantment != removedEnchantment.enchantment) {
                result.addEnchantment(enchantment, enchantmentLevels.get(enchantment));
            }
        }

        this.result.setStack(0, result);
    }
}
