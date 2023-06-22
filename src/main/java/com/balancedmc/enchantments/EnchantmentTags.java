package com.balancedmc.enchantments;

import com.balancedmc.Main;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class EnchantmentTags {

    public static final TagKey<Block> FROST_WALKER_ADJACENT = TagKey.of(RegistryKeys.BLOCK, new Identifier(Main.MOD_ID, "frost_walker_adjacent"));

}
