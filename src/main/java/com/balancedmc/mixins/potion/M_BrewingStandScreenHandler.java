package com.balancedmc.mixins.potion;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.screen.BrewingStandScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BrewingStandScreenHandler.class)
public class M_BrewingStandScreenHandler extends ScreenHandler {

    private Inventory inventory;
    private final Slot ingredientSlot;

    protected M_BrewingStandScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId, Inventory inventory) {
        super(type, syncId);
        this.inventory = inventory;
        this.ingredientSlot = this.addSlot(new IngredientSlot(inventory, 3, 79, 17));
    }

    boolean canInsertPotion(ItemStack stack) {
        return stack.isOf(Items.POTION) || stack.isOf(Items.SPLASH_POTION) || stack.isOf(Items.LINGERING_POTION) || stack.isOf(Items.GLASS_BOTTLE);
    }

    protected boolean insertPotion(ItemStack stack, int startIndex, int endIndex, boolean fromLast) {
        boolean bl = false;
        int i = startIndex;
        if (fromLast) {
            i = endIndex - 1;
        }

        Slot slot;
        ItemStack itemStack;
        if (stack.isStackable()) {
            while(!stack.isEmpty()) {
                if (fromLast) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                slot = (Slot)this.slots.get(i);
                itemStack = slot.getStack();
                if (!itemStack.isEmpty() && ItemStack.canCombine(stack, itemStack)) {
                    int j = itemStack.getCount() + stack.getCount();
                    if (j <= 1) {
                        stack.setCount(0);
                        itemStack.setCount(j);
                        slot.markDirty();
                        bl = true;
                    } else if (itemStack.getCount() < 1) {
                        stack.decrement(1 - itemStack.getCount());
                        itemStack.setCount(1);
                        slot.markDirty();
                        bl = true;
                    }
                }

                if (fromLast) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        if (!stack.isEmpty()) {
            if (fromLast) {
                i = endIndex - 1;
            } else {
                i = startIndex;
            }

            while(true) {
                if (fromLast) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                slot = (Slot)this.slots.get(i);
                itemStack = slot.getStack();
                if (itemStack.isEmpty() && slot.canInsert(stack)) {
                    if (stack.getCount() > 1) {
                        slot.setStack(stack.split(1));
                    } else {
                        slot.setStack(stack.split(stack.getCount()));
                    }
                    slot.markDirty();
                    bl = true;
                    break;
                }

                if (fromLast) {
                    --i;
                } else {
                    ++i;
                }
            }
        }
        return bl;
    }

    @Inject(method = "quickMove", at = @At(value = "RETURN"), cancellable = true)
    private void quickMove(PlayerEntity player, int slot, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack itemStackA = ItemStack.EMPTY;
        Slot slot2A = (Slot) this.slots.get(slot);
        if (slot2A != null) {
            if (slot2A.hasStack()) {
                ItemStack itemStack2A = slot2A.getStack();
                itemStackA = itemStack2A.copy();
                if (!(slot >= 0 && slot <= 4)) {
                    if (canInsertPotion(itemStackA)) {
                        for (int i = 0; i < itemStack2A.getCount(); i++) {
                            if (!this.insertPotion(itemStack2A, 0, 3, false)) {
                                cir.setReturnValue(ItemStack.EMPTY);
                                cir.cancel();
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStackA = ItemStack.EMPTY;
        Slot slot2A = (Slot) this.slots.get(slot);
        if (slot2A != null) {
            if (slot2A.hasStack()) {
                ItemStack itemStack2A = slot2A.getStack();
                itemStackA = itemStack2A.copy();
                if (!(slot >= 0 && slot <= 4)) {
                    if (canInsertPotion(itemStackA)) {
                        for (int i = 0; i < itemStack2A.getCount(); i++) {
                            if (!this.insertPotion(itemStack2A, 0, 3, false)) {
                                return ItemStack.EMPTY;
                            }
                        }
                    }
                }
            }
        }

        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = (Slot)this.slots.get(slot);
        if (slot2 != null && slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            if ((slot < 0 || slot > 2) && slot != 3 && slot != 4) {
                if (FuelSlot.matches(itemStack)) {
                    if (this.insertItem(itemStack2, 4, 5, false) || ingredientSlot.canInsert(itemStack2) && !this.insertItem(itemStack2, 3, 4, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (ingredientSlot.canInsert(itemStack2)) {
                    if (!this.insertItem(itemStack2, 3, 4, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (PotionSlot.matches(itemStack) && itemStack.getCount() == 1) {
                    if (!this.insertPotion(itemStack2, 0, 3, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slot >= 5 && slot < 32) {
                    if (!this.insertItem(itemStack2, 32, 41, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slot >= 32 && slot < 41) {
                    if (!this.insertItem(itemStack2, 5, 32, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.insertItem(itemStack2, 5, 41, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.insertItem(itemStack2, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }
                slot2.onQuickTransfer(itemStack2, itemStack);
            }
            if (itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot2.onTakeItem(player, itemStack2);
        }
        return itemStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    static class PotionSlot extends Slot {
        public PotionSlot(Inventory inventory, int i, int j, int k) {
            super(inventory, i, j, k);
        }
        public boolean canInsert(ItemStack stack) {
            return matches(stack);
        }
        public int getMaxItemCount() {
            return 1;
        }
        public void onTakeItem(PlayerEntity player, ItemStack stack) {
            Potion potion = PotionUtil.getPotion(stack);
            if (player instanceof ServerPlayerEntity) {
                Criteria.BREWED_POTION.trigger((ServerPlayerEntity)player, potion);
            }

            super.onTakeItem(player, stack);
        }
        public static boolean matches(ItemStack stack) {
            return stack.isOf(Items.POTION) || stack.isOf(Items.SPLASH_POTION) || stack.isOf(Items.LINGERING_POTION) || stack.isOf(Items.GLASS_BOTTLE);
        }
    }

    private static class IngredientSlot extends Slot {
        public IngredientSlot(Inventory inventory, int i, int j, int k) {
            super(inventory, i, j, k);
        }
        public boolean canInsert(ItemStack stack) {
            return BrewingRecipeRegistry.isValidIngredient(stack);
        }
        public int getMaxItemCount() {
            return 64;
        }
    }

    private static class FuelSlot extends Slot {
        public FuelSlot(Inventory inventory, int i, int j, int k) {
            super(inventory, i, j, k);
        }
        public boolean canInsert(ItemStack stack) {
            return matches(stack);
        }
        public static boolean matches(ItemStack stack) {
            return stack.isOf(Items.BLAZE_POWDER);
        }
        public int getMaxItemCount() {
            return 64;
        }
    }

}
