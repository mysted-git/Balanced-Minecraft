package com.balancedmc.mixins;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

/**
 * Reinforced deepslate cannot be mined
 */
@Mixin(Blocks.class)
public abstract class M_Blocks {

    @Redirect(
            method = "<clinit>",
            at = @At(
                    value = "NEW",
                    target = "Lnet/minecraft/block/Block;*",
                    ordinal = 0
            ),
            slice = @Slice(
                    from = @At(
                            value = "CONSTANT",
                            args= {"stringValue=reinforced_deepslate"},
                            ordinal = 0
                    )
            )
    )
    private static Block redirect(AbstractBlock.Settings settings) {
        return new Block(settings.hardness(-1));
    }
}
