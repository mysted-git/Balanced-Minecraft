package com.balancedmc.mixins.client.conduit;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.OceanMonumentGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.math.BlockBox;
import net.minecraft.world.StructureWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

/**
 * A conduit spawns in ocean monuments<br>
 * It replaces the gold blocks in the centre
 */
@Mixin(OceanMonumentGenerator.CoreRoom.class)
public abstract class M_OceanMonumentGenerator extends StructurePiece {

    public M_OceanMonumentGenerator(StructurePieceType type, NbtCompound nbt) {
        super(type, nbt);
    }

    @Redirect(
            method = "generate(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/util/math/random/Random;Lnet/minecraft/util/math/BlockBox;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/util/math/BlockPos;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/structure/OceanMonumentGenerator$CoreRoom;fillWithOutline(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/util/math/BlockBox;IIIIIILnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;Z)V"
            ),
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/block/Blocks;GOLD_BLOCK:Lnet/minecraft/block/Block;"),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/structure/OceanMonumentGenerator$CoreRoom;SEA_LANTERN:Lnet/minecraft/block/BlockState;")
            )
    )
    private void redirect(OceanMonumentGenerator.CoreRoom instance, StructureWorldAccess world, BlockBox box, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockState outline, BlockState inside, boolean cantReplaceAir) {
        this.addBlock(world, Blocks.CONDUIT.getDefaultState(), minX, minY, minZ, box);
    }
}
