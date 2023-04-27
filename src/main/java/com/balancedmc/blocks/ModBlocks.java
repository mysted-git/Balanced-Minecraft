package com.balancedmc.blocks;

import com.balancedmc.Main;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static final Block CONDUIT_SHELL = new ConduitShellBlock(FabricBlockSettings.of(Material.GLASS, MapColor.DIAMOND_BLUE).strength(3.0F).nonOpaque());

    public static void registerBlocks() {
        Registry.register(Registries.BLOCK, new Identifier(Main.MOD_ID, "conduit_shell"), CONDUIT_SHELL);
        Registry.register(Registries.ITEM, new Identifier(Main.MOD_ID, "conduit_shell"), new BlockItem(CONDUIT_SHELL, new FabricItemSettings()));
    }
}
