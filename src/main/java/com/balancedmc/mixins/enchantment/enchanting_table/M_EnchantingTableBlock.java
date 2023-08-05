package com.balancedmc.mixins.enchantment.enchanting_table;

import net.minecraft.block.Blocks;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EnchantingTableBlock.class)
public abstract class M_EnchantingTableBlock {


    /**
     * @author HB0P
     * @reason Chiseled bookshelves provide enchantment table power if they contain an enchanted book
     */
    @Overwrite
    public static boolean canAccessPowerProvider(World world, BlockPos tablePos, BlockPos providerOffset) {
        BlockPos pos = tablePos.add(providerOffset);
        if (!world.getBlockState(tablePos.add(providerOffset.getX() / 2, providerOffset.getY(), providerOffset.getZ() / 2)).isIn(BlockTags.ENCHANTMENT_POWER_TRANSMITTER)) {
            return false;
        }
        else if (world.getBlockState(pos).isOf(Blocks.CHISELED_BOOKSHELF)) {
            if (world.getBlockEntity(pos) instanceof ChiseledBookshelfBlockEntity blockEntity) {
                for (int i = 0; i < blockEntity.size(); i++) {
                    if (blockEntity.getStack(i).isOf(Items.ENCHANTED_BOOK)) {
                        return true;
                    }
                }
            }
            return false;
        }
        else {
            return world.getBlockState(pos).isIn(BlockTags.ENCHANTMENT_POWER_PROVIDER);
        }
    }
}
