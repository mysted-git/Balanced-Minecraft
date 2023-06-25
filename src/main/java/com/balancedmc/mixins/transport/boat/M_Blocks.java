package com.balancedmc.mixins.transport.boat;

import com.balancedmc.blocks.BlueIceBlock;
import com.balancedmc.blocks.PackedIceBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.TransparentBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

/**
 * Packed ice and blue ice now use custom classes<br>
 * This allows them to melt
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
                            args= {"stringValue=packed_ice"},
                            ordinal = 0
                    )
            )
    )
    private static Block packedIce(AbstractBlock.Settings settings) {
        return new PackedIceBlock(settings.ticksRandomly());
    }

    @Redirect(
            method = "<clinit>",
            at = @At(
                    value = "NEW",
                    target = "Lnet/minecraft/block/TransparentBlock;*",
                    ordinal = 0
            ),
            slice = @Slice(
                    from = @At(
                            value = "CONSTANT",
                            args= {"stringValue=blue_ice"},
                            ordinal = 0
                    )
            )
    )
    private static TransparentBlock blueIce(AbstractBlock.Settings settings) {
        return new BlueIceBlock(settings.ticksRandomly());
    }
}
