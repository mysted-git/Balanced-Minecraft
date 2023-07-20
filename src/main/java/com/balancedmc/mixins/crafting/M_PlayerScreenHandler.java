package com.balancedmc.mixins.crafting;

import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static net.minecraft.screen.PlayerScreenHandler.BLOCK_ATLAS_TEXTURE;
import static net.minecraft.screen.PlayerScreenHandler.EMPTY_OFFHAND_ARMOR_SLOT;

@Mixin(PlayerScreenHandler.class)
public abstract class M_PlayerScreenHandler extends AbstractRecipeScreenHandler<CraftingInventory> {

    public M_PlayerScreenHandler(ScreenHandlerType<?> screenHandlerType, int i) {
        super(screenHandlerType, i);
    }

    private int mode = 0;
    private final Inventory craftingInventory = new SimpleInventory(16);
    @Shadow @Final private RecipeInputInventory craftingInput;
    @Shadow @Final private CraftingResultInventory craftingResult;
    @Shadow @Final private PlayerEntity owner;
    @Shadow static void onEquipStack(PlayerEntity player, EquipmentSlot slot, ItemStack newStack, ItemStack currentStack) {}
    @Shadow public abstract boolean canInsertIntoSlot(ItemStack stack, Slot slot);

    private void updateCrafting() {
        ItemStack stack = craftingInventory.getStack(0);
        List<CraftingRecipe> recipes = owner.getWorld().getRecipeManager().listAllOfType(RecipeType.CRAFTING);
        for (int i = 1; i < 16; i++) {craftingInventory.removeStack(i);}
        ArrayList<Pair<ItemStack, Integer>> items = new ArrayList<>();
        for (CraftingRecipe recipe : recipes) {
            ItemStack output = recipe.getOutput(owner.getWorld().getRegistryManager());
            // handle special cases
            if (output.isEmpty() || output.isOf(Items.FIREWORK_ROCKET) || output.isOf(Items.FIREWORK_STAR)) continue;
            // check if valid
            boolean valid = true;
            int count = 0;
            for (Ingredient ingredient : recipe.getIngredients()) {
                if (ingredient.getMatchingStacks().length == 0) continue;
                boolean matches = false;
                for (ItemStack iStack : ingredient.getMatchingStacks()) {
                    if (iStack.isOf(stack.getItem())) {
                        matches = true;
                        count++;
                        break;
                    }
                }
                if (!matches) {
                    valid = false;
                    break;
                }
            }
            if (valid && stack.getCount() >= count) {
                items.add(new Pair<>(new ItemStack(output.getItem(), output.getCount()), count));
            }
        }
        items.sort(Comparator.comparingInt(Pair::getSecond));
        int i = 1;
        for (Pair<ItemStack, Integer> pair : items) {
            craftingInventory.setStack(i++, pair.getFirst());
        }
    }

    private void handleTakeItem(Item item) {
        List<CraftingRecipe> recipes = owner.getWorld().getRecipeManager().listAllOfType(RecipeType.CRAFTING);
        for (CraftingRecipe recipe : recipes) {
            ItemStack output = recipe.getOutput(owner.getWorld().getRegistryManager());
            // remove input items
            if (output.isOf(item)) {
                craftingInventory.removeStack(0, (int) recipe.getIngredients().stream().filter((ingredient) -> {
                    for (ItemStack iStack : ingredient.getMatchingStacks()) {
                        if (iStack.isOf(craftingInventory.getStack(0).getItem())) return true;
                    }
                    return false;
                }).count());
                updateCrafting();
            }
        }
    }

    /**
     * Add slots
     */
    @Inject(
            method = "<init>(Lnet/minecraft/entity/player/PlayerInventory;ZLnet/minecraft/entity/player/PlayerEntity;)V",
            at = @At("TAIL")
    )
    private void injected(PlayerInventory inventory, boolean onServer, PlayerEntity owner, CallbackInfo ci) {
        this.addSlot(new Slot(craftingInventory, 0, 42, 35){
            @Override
            public void setStack(ItemStack stack) {
                super.setStack(stack);
                updateCrafting();
            }
            @Override
            public boolean isEnabled() {
                return mode == 1;
            }
        });
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 3; y++) {
                this.addSlot(new Slot(craftingInventory, x + y * 5 + 1, x * 18 + 80, y * 18 + 17){
                    @Override
                    public boolean canInsert(ItemStack stack) {
                        return false;
                    }
                    @Override
                    public void onTakeItem(PlayerEntity player, ItemStack stack) {
                        super.onTakeItem(player, stack);
                        handleTakeItem(stack.getItem());
                    }
                    @Override
                    public boolean isEnabled() {
                        return this.hasStack() && mode == 1;
                    }
                });
            }
        }
    }

    /**
     * Handle exiting the screen
     */
    @Inject(
            method = "onClosed(Lnet/minecraft/entity/player/PlayerEntity;)V",
            at = @At("TAIL")
    )
    private void injected(PlayerEntity player, CallbackInfo ci) {
        ItemStack stack = this.craftingInventory.getStack(0);
        if (!player.giveItemStack(stack)) {
            player.dropItem(stack, true);
        }
        this.craftingInventory.clear();
    }

    /**
     * Remove crafting slots<br>
     * Move offhand slot
     */
    @Redirect(
            method = "<init>(Lnet/minecraft/entity/player/PlayerInventory;ZLnet/minecraft/entity/player/PlayerEntity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/PlayerScreenHandler;addSlot(Lnet/minecraft/screen/slot/Slot;)Lnet/minecraft/screen/slot/Slot;"
            )
    )
    private Slot redirect(PlayerScreenHandler instance, Slot slot) {
        if (slot.inventory == this.craftingResult) {
            return this.addSlot(new CraftingResultSlot(this.owner, this.craftingInput, this.craftingResult, slot.getIndex(), 90, 35) {
                @Override
                public boolean isEnabled() {
                    return mode == 0;
                }
            });
        }
        if (slot.inventory == this.craftingInput) {
            int x = slot.getIndex() == 0 || slot.getIndex() == 2 ? 52 : Integer.MAX_VALUE;
            int y = slot.getIndex() == 0 ? 25 : slot.getIndex() == 2 ? 43 : Integer.MAX_VALUE;
            return this.addSlot(new Slot(slot.inventory, slot.getIndex(), x, y) {
                @Override
                public boolean isEnabled() {
                    return mode == 0;
                }
            });
        }
        if (slot.getIndex() == 40) {
            return this.addSlot(new Slot(slot.inventory, 40, 26, 62){
                @Override
                public void setStack(ItemStack stack) {
                    onEquipStack(owner, EquipmentSlot.OFFHAND, stack, this.getStack());
                    super.setStack(stack);
                }
                @Override
                public Pair<Identifier, Identifier> getBackgroundSprite() {
                    return Pair.of(BLOCK_ATLAS_TEXTURE, EMPTY_OFFHAND_ARMOR_SLOT);
                }
            });
        }
        return this.addSlot(slot);
    }

    /**
     * Handle shift-clicking from crafting output
     */
    @Redirect(
            method = "quickMove(Lnet/minecraft/entity/player/PlayerEntity;I)Lnet/minecraft/item/ItemStack;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/slot/Slot;getStack()Lnet/minecraft/item/ItemStack;"
            )
    )
    private ItemStack redirect(Slot slot, PlayerEntity player) {
        ItemStack result = slot.getStack();
        if (slot.inventory == this.craftingInventory && slot.getIndex() >= 1) {
            slot.onTakeItem(player, result);
        }
        return result;
    }

    /**
     * Block shift clicking if inventory full
     */
    @Redirect(
            method = "quickMove(Lnet/minecraft/entity/player/PlayerEntity;I)Lnet/minecraft/item/ItemStack;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/slot/Slot;hasStack()Z",
                    ordinal = 0
            )
    )
    private boolean redirect(Slot slot) {
        for (int i = 9; i < 45; i++) {
            ItemStack sourceItem = slot.getStack();
            ItemStack targetItem = this.getSlot(i).getStack();
            if (targetItem.isEmpty() || (targetItem.isOf(sourceItem.getItem()) && targetItem.getCount() + sourceItem.getCount() <= 64)) {
                return slot.hasStack();
            }
        }
        return false;
    }

    /**
     * Prevent right-clicking crafting output<br>
     * Update when crafting input is right-clicked
     */
    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (slotIndex >= 47 && button == 1) {
            return;
        }
        super.onSlotClick(slotIndex, button, actionType, player);
        if (slotIndex == 46) {
            updateCrafting();
        }
    }

    /**
     * Handle clicking "switch mode" button
     */
    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id == 0) {
            this.mode = this.mode == 0 ? 1 : 0;
            return true;
        }
        return false;
    }
}
