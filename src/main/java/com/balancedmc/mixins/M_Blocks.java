package com.balancedmc.mixins;

import net.minecraft.block.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Blocks.class)
public abstract class M_Blocks {

    /**
     * Reinforced deepslate cannot be mined
     */
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
    private static Block reinforced_deepslate(AbstractBlock.Settings settings) {
        return new Block(settings.hardness(-1));
    }

    /**
     * Pink petals are replaceable
     */
    @Redirect(
            method = "<clinit>",
            at = @At(
                    value = "NEW",
                    target = "Lnet/minecraft/block/FlowerbedBlock;*",
                    ordinal = 0
            ),
            slice = @Slice(
                    from = @At(
                            value = "CONSTANT",
                            args= {"stringValue=pink_petals"},
                            ordinal = 0
                    )
            )
    )
    private static FlowerbedBlock pink_petals(AbstractBlock.Settings settings) {
        return new FlowerbedBlock(settings.replaceable());
    }

    /**
     * Nether portals can be broken
     */
    @Redirect(
            method = "<clinit>",
            at = @At(
                    value = "NEW",
                    target = "Lnet/minecraft/block/NetherPortalBlock;*",
                    ordinal = 0
            ),
            slice = @Slice(
                    from = @At(
                            value = "CONSTANT",
                            args= {"stringValue=nether_portal"},
                            ordinal = 0
                    )
            )
    )
    private static NetherPortalBlock nether_portal(AbstractBlock.Settings settings) {
        return new NetherPortalBlock(settings.strength(0.2f));
    }
}
