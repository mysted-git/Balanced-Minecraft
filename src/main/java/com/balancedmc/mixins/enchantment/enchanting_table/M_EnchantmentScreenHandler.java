package com.balancedmc.mixins.enchantment.enchanting_table;

import net.minecraft.block.Blocks;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(EnchantmentScreenHandler.class)
public abstract class M_EnchantmentScreenHandler {

    @Shadow @Final private ScreenHandlerContext context;
    @Shadow @Final private Random random;
    private World world;
    private BlockPos pos;

    /**
     * Enchanted books in chiseled bookshelves influence enchantments given by enchanting table
     * <p>
     * For each enchanted book:<br>
     * - An enchantment is removed from the result<br>
     * - A random enchantment from the book is added to the result, with a level less than or equal to the book's
     */
    @Inject(
            method = "generateEnchantments(Lnet/minecraft/item/ItemStack;II)Ljava/util/List;",
            at = @At("RETURN"),
            cancellable = true
    )
    private void injected(ItemStack stack, int slot, int level, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
        this.context.run((world, pos) -> {
            this.world = world;
            this.pos = pos;
        });
        List<EnchantmentLevelEntry> result = cir.getReturnValue();

        for (BlockPos offset : EnchantingTableBlock.POWER_PROVIDER_OFFSETS) {
            if (this.world.getBlockState(this.pos.add(offset)).isOf(Blocks.CHISELED_BOOKSHELF)) {
                if (world.getBlockEntity(this.pos.add(offset)) instanceof ChiseledBookshelfBlockEntity blockEntity) {
                    for (int i = 0; i < blockEntity.size(); i++) {
                        if (blockEntity.getStack(i).isOf(Items.ENCHANTED_BOOK)) {
                            Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(blockEntity.getStack(i));
                            // get a list of all enchantments which:
                            // are compatible with the item
                            // are not already in the result
                            List<Enchantment> validEnchantments = enchantments.keySet().stream().filter((enchantment -> {
                                for (EnchantmentLevelEntry entry : result) {
                                    if (entry.enchantment == enchantment) return false;
                                }
                                return stack.isOf(Items.BOOK) || enchantment.isAcceptableItem(stack);
                            })).toList();
                            if (validEnchantments.isEmpty()) continue;
                            // remove enchantments from the result if they are incompatible with the chosen enchantment
                            // if no enchantments were removed, remove a random one
                            Enchantment enchantment = validEnchantments.get(this.random.nextInt(validEnchantments.size()));
                            if (!result.removeIf(entry -> !entry.enchantment.canCombine(enchantment))) {
                                result.remove(this.random.nextInt(result.size()));
                            }
                            // add the chosen enchantment to the result at a random level
                            result.add(new EnchantmentLevelEntry(enchantment, this.random.nextInt(enchantments.get(enchantment)) + 1));
                        }
                    }
                }
            }
        }
        cir.setReturnValue(result);
    }
}
