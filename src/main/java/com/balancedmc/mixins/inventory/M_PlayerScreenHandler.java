package com.balancedmc.mixins.inventory;

import com.balancedmc.Main;
import com.mojang.datafixers.util.Pair;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.tag.ItemTags;
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
import java.util.function.Predicate;

import static net.minecraft.screen.PlayerScreenHandler.BLOCK_ATLAS_TEXTURE;
import static net.minecraft.screen.PlayerScreenHandler.EMPTY_OFFHAND_ARMOR_SLOT;

@Mixin(PlayerScreenHandler.class)
public abstract class M_PlayerScreenHandler extends AbstractRecipeScreenHandler<CraftingInventory> {

    public M_PlayerScreenHandler(ScreenHandlerType<?> screenHandlerType, int i) {
        super(screenHandlerType, i);
    }

    private int craftingMode = 0;
    private int activeInventory = 0;
    private final Inventory craftingInventory = new SimpleInventory(16);
    @Shadow @Final private RecipeInputInventory craftingInput;
    @Shadow @Final private CraftingResultInventory craftingResult;
    @Shadow @Final private PlayerEntity owner;
    @Shadow static void onEquipStack(PlayerEntity player, EquipmentSlot slot, ItemStack newStack, ItemStack currentStack) {}
    @Shadow public abstract boolean canInsertIntoSlot(ItemStack stack, Slot slot);
    @Shadow @Final private static EquipmentSlot[] EQUIPMENT_SLOT_ORDER;
    @Shadow @Final static Identifier[] EMPTY_ARMOR_SLOT_TEXTURES;
    private static final Identifier EMPTY_WEAPON_SLOT_TEXTURE = new Identifier("item/empty_slot_sword");
    private static final Identifier EMPTY_FOOD_SLOT_TEXTURE = new Identifier(Main.MOD_ID, "item/slots/empty_slot_food");
    private static final Identifier EMPTY_TOOL_SLOT_TEXTURE = new Identifier("item/empty_slot_pickaxe");
    private static final Identifier EMPTY_CUTTER_SLOT_TEXTURE = new Identifier(Main.MOD_ID, "item/slots/empty_slot_cutter");
    private static final Identifier[] TOOL_INVENTORY_SLOT_TEXTURES = {
            EMPTY_WEAPON_SLOT_TEXTURE,
            EMPTY_WEAPON_SLOT_TEXTURE,
            new Identifier(Main.MOD_ID, "item/slots/empty_slot_arrow"),
            EMPTY_FOOD_SLOT_TEXTURE,
            EMPTY_FOOD_SLOT_TEXTURE,
            EMPTY_TOOL_SLOT_TEXTURE,
            EMPTY_TOOL_SLOT_TEXTURE,
            new Identifier(Main.MOD_ID, "item/slots/empty_slot_spyglass"),
            new Identifier(Main.MOD_ID, "item/slots/empty_slot_crafting_table"),
            EMPTY_CUTTER_SLOT_TEXTURE,
            new Identifier(Main.MOD_ID, "item/slots/empty_slot_elytra"),
            EMPTY_TOOL_SLOT_TEXTURE,
            EMPTY_TOOL_SLOT_TEXTURE,
            new Identifier(Main.MOD_ID, "item/slots/empty_slot_vehicle"),
            new Identifier(Main.MOD_ID, "item/slots/empty_slot_ender_chest"),
            EMPTY_CUTTER_SLOT_TEXTURE
    };
    private static final Predicate<ItemStack> WEAPON_SLOT_PREDICATE = itemStack -> itemStack.isIn(ItemTags.SWORDS) || itemStack.isOf(Items.BOW) || itemStack.isOf(Items.CROSSBOW) || itemStack.isOf(Items.TRIDENT);
    private static final Predicate<ItemStack> FOOD_SLOT_PREDICATE = itemStack -> itemStack.getItem().isFood() || itemStack.isOf(Items.CAKE) || itemStack.isOf(Items.MILK_BUCKET) || itemStack.getItem() instanceof PotionItem;
    private static final Predicate<ItemStack> TOOL_SLOT_PREDICATE = itemStack -> itemStack.isIn(ItemTags.PICKAXES) || itemStack.isIn(ItemTags.AXES) || itemStack.isIn(ItemTags.SHOVELS) || itemStack.isIn(ItemTags.HOES) || itemStack.isOf(Items.SHEARS) || itemStack.isOf(Items.FLINT_AND_STEEL) || itemStack.isOf(Items.FISHING_ROD) || itemStack.isOf(Items.CARROT_ON_A_STICK) || itemStack.isOf(Items.WARPED_FUNGUS_ON_A_STICK);
    private static final Predicate<ItemStack> CUTTER_SLOT_PREDICATE = itemStack -> itemStack.isOf(Items.STONECUTTER);
    private static final List<Predicate<ItemStack>> TOOL_SLOT_PREDICATES = List.of(
            WEAPON_SLOT_PREDICATE,
            WEAPON_SLOT_PREDICATE,
            itemStack -> itemStack.isIn(ItemTags.ARROWS) || itemStack.isOf(Items.FIREWORK_ROCKET),
            FOOD_SLOT_PREDICATE,
            FOOD_SLOT_PREDICATE,
            TOOL_SLOT_PREDICATE,
            TOOL_SLOT_PREDICATE,
            itemStack -> itemStack.isOf(Items.SPYGLASS),
            itemStack -> itemStack.isOf(Items.CRAFTING_TABLE),
            CUTTER_SLOT_PREDICATE,
            itemStack -> itemStack.isOf(Items.ELYTRA),
            TOOL_SLOT_PREDICATE,
            TOOL_SLOT_PREDICATE,
            itemStack -> itemStack.isIn(ItemTags.BOATS) || itemStack.isOf(Items.MINECART),
            itemStack -> itemStack.isOf(Items.ENDER_CHEST),
            CUTTER_SLOT_PREDICATE
    );

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
        int i;
        // crafting output
        this.addSlot(new CraftingResultSlot(this.owner, this.craftingInput, this.craftingResult, 0, 152, 30) {
            @Override
            public boolean isEnabled() {
                return craftingMode == 0;
            }
        });
        // crafting input
        for (i = 0; i < 2; ++i) {
            for (int j = 0; j < 2; ++j) {
                this.addSlot(new Slot(this.craftingInput, j + i * 2, 80 + j * 18, 20 + i * 18) {
                    @Override
                    public boolean isEnabled() {
                        return craftingMode == 0;
                    }
                });
            }
        }
        // armour
        for (i = 0; i < 4; ++i) {
            final EquipmentSlot equipmentSlot = EQUIPMENT_SLOT_ORDER[i];
            this.addSlot(new Slot(inventory, 39 - i, 8 + (i % 2 == 1 ? 18 : 0), 84 + (i >= 2 ? 18 : 0)){
                @Override
                public void setStack(ItemStack stack) {
                    onEquipStack(owner, equipmentSlot, stack, this.getStack());
                    super.setStack(stack);
                }
                @Override
                public int getMaxItemCount() {
                    return 1;
                }
                @Override
                public boolean canInsert(ItemStack stack) {
                    return equipmentSlot == MobEntity.getPreferredEquipmentSlot(stack);
                }
                @Override
                public boolean canTakeItems(PlayerEntity playerEntity) {
                    ItemStack itemStack = this.getStack();
                    if (!itemStack.isEmpty() && !playerEntity.isCreative() && EnchantmentHelper.hasBindingCurse(itemStack)) {
                        return false;
                    }
                    return super.canTakeItems(playerEntity);
                }
                @Override
                public Pair<Identifier, Identifier> getBackgroundSprite() {
                    return Pair.of(BLOCK_ATLAS_TEXTURE, EMPTY_ARMOR_SLOT_TEXTURES[equipmentSlot.getEntitySlotId()]);
                }
                @Override
                public boolean isEnabled() {
                    return activeInventory == 1 || owner.isCreative();
                }
            });
        }
        // main inventory
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18) {
                    @Override
                    public boolean isEnabled() {
                        return activeInventory == 0 || owner.isCreative();
                    }
                });
            }
        }
        // main hotbar
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 142) {
                @Override
                public boolean isEnabled() {
                    return activeInventory == 0 || owner.isCreative();
                }
            });
        }
        // offhand
        this.addSlot(new Slot(inventory, 40, 8, 120){
            @Override
            public void setStack(ItemStack stack) {
                onEquipStack(owner, EquipmentSlot.OFFHAND, stack, this.getStack());
                super.setStack(stack);
            }
            @Override
            public Pair<Identifier, Identifier> getBackgroundSprite() {
                return Pair.of(BLOCK_ATLAS_TEXTURE, EMPTY_OFFHAND_ARMOR_SLOT);
            }
            @Override
            public boolean isEnabled() {
                return activeInventory == 1 || owner.isCreative();
            }
        });
        // cutting input
        this.addSlot(new Slot(craftingInventory, 0, 80, 30){
            @Override
            public void setStack(ItemStack stack) {
                super.setStack(stack);
                updateCrafting();
            }
            @Override
            public boolean isEnabled() {
                return craftingMode == 1;
            }
        });
        // cutting output
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 3; y++) {
                this.addSlot(new Slot(craftingInventory, x + y * 5 + 1, x * 18 + 116, y * 18 + 20) {
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
                        return this.hasStack() && craftingMode == 1;
                    }
                });
            }
        }
        // tool inventory
        i = 50;
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 7; x++) {
                if (x < 2 && y < 2) continue;
                if (x == 0) continue;
                this.addSlot(new Slot(inventory, i, x * 18 + 8 + (x >= 2 ? 18 : 0) + (x >= 5 ? 18 : 0), y * 18 + 84) {
                    @Override
                    public boolean isEnabled() {
                        return activeInventory == 1 && !owner.isCreative();
                    }
                    @Override
                    public boolean canInsert(ItemStack stack) {
                        return TOOL_SLOT_PREDICATES.get(this.getIndex() - 50).test(stack);
                    }
                    @Override
                    public Pair<Identifier, Identifier> getBackgroundSprite() {
                        return Pair.of(BLOCK_ATLAS_TEXTURE, TOOL_INVENTORY_SLOT_TEXTURES[this.getIndex() - 50]);
                    }
                });
                i++;
            }
        }
        // tool hotbar
        for (int x = 0; x < 9; x++) {
            this.addSlot(new Slot(inventory, x + 41, x * 18 + 8, 142) {
                @Override
                public boolean isEnabled() {
                    return activeInventory == 1 && !owner.isCreative();
                }
                @Override
                public boolean canInsert(ItemStack stack) {
                    for (Predicate<ItemStack> predicate : TOOL_SLOT_PREDICATES) {
                        if (predicate.test(stack)) {
                            return true;
                        }
                    }
                    for (EquipmentSlot slot : EQUIPMENT_SLOT_ORDER) {
                        if (slot == MobEntity.getPreferredEquipmentSlot(stack)) {
                            return true;
                        }
                    }
                    return false;
                }
            });
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
     * Remove all slots
     */
    @Redirect(
            method = "<init>(Lnet/minecraft/entity/player/PlayerInventory;ZLnet/minecraft/entity/player/PlayerEntity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/PlayerScreenHandler;addSlot(Lnet/minecraft/screen/slot/Slot;)Lnet/minecraft/screen/slot/Slot;"
            )
    )
    private Slot redirect(PlayerScreenHandler instance, Slot slot) {
        return null;
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
        // inventory toggle
        if (id == 0) {
            this.activeInventory = this.activeInventory == 0 ? 1 : 0;
            return true;
        }
        // crafting toggle
        else if (id == 1) {
            this.craftingMode = this.craftingMode == 0 ? 1 : 0;
            return true;
        }
        return false;
    }
}
