package com.balancedmc.mixins.crafting;

import com.balancedmc.Main;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static net.minecraft.screen.PlayerScreenHandler.BLOCK_ATLAS_TEXTURE;
import static net.minecraft.screen.PlayerScreenHandler.EMPTY_OFFHAND_ARMOR_SLOT;

@Mixin(PlayerScreenHandler.class)
public abstract class M_PlayerScreenHandler extends AbstractRecipeScreenHandler<CraftingInventory> {

    public M_PlayerScreenHandler(ScreenHandlerType<?> screenHandlerType, int i) {
        super(screenHandlerType, i);
    }

    private final Inventory craftingInventory = new SimpleInventory(16);
    @Shadow @Final private RecipeInputInventory craftingInput;
    @Shadow @Final private CraftingResultInventory craftingResult;
    @Shadow @Final private PlayerEntity owner;
    @Shadow static void onEquipStack(PlayerEntity player, EquipmentSlot slot, ItemStack newStack, ItemStack currentStack) {}

    private void updateCrafting() {
        ItemStack stack = craftingInventory.getStack(0);
        List<CraftingRecipe> recipes = owner.getWorld().getRecipeManager().listAllOfType(RecipeType.CRAFTING);
        for (int i = 1; i < 16; i++) {craftingInventory.removeStack(i);}
        int i = 1;
        for (CraftingRecipe recipe : recipes) {
            ItemStack output = recipe.getOutput(owner.getWorld().getRegistryManager());
            // handle special cases
            if (output.isEmpty() || output.isOf(Items.FIREWORK_ROCKET) || output.isOf(Items.FIREWORK_STAR)) continue;
            if (output.isIn(ItemTags.STAIRS) && recipe.getIngredients().size() == 9) continue;
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
                craftingInventory.setStack(i, new ItemStack(output.getItem(), output.getCount()));
                i++;
            }
        }
    }

    private void handleTakeItem(Item item) {
        List<CraftingRecipe> recipes = owner.getWorld().getRecipeManager().listAllOfType(RecipeType.CRAFTING);
        for (CraftingRecipe recipe : recipes) {
            ItemStack output = recipe.getOutput(owner.getWorld().getRegistryManager());
            // fix stairs double recipe
            if (output.isIn(ItemTags.STAIRS) && recipe.getIngredients().size() == 9) continue;
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
        });
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 3; y++) {
                this.addSlot(new Slot(craftingInventory, x + y * 5 + 1, x * 18 + 80, y * 18 + 17){
                    @Override
                    public boolean canInsert(ItemStack stack) {
                        return false;
                    }
                    @Override
                    public ItemStack takeStack(int amount) {
                        Item item = this.getStack().getItem();
                        ItemStack result = super.takeStack(this.getStack().getCount());
                        handleTakeItem(item);
                        return result;
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
        if (slot.inventory == this.craftingInput || slot.inventory == this.craftingResult) {
            return this.addSlot(new Slot(slot.inventory, slot.getIndex(), Integer.MAX_VALUE, Integer.MAX_VALUE));
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
     * Prevent shift clicking out of crafting result
     */
    @Inject(
            method = "quickMove(Lnet/minecraft/entity/player/PlayerEntity;I)Lnet/minecraft/item/ItemStack;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/mob/MobEntity;getPreferredEquipmentSlot(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/EquipmentSlot;"
            ),
            cancellable = true
    )
    private void injected(PlayerEntity player, int slot, CallbackInfoReturnable<ItemStack> cir) {
        if (slot >= 47) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }
}
