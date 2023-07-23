package com.balancedmc.structure_tags;

import com.balancedmc.Main;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.Structure;

public class ModStructureTags {
    public static final TagKey<Structure> ON_DEEP_DARK_EXPLORER_MAPS = TagKey.of(RegistryKeys.STRUCTURE, new Identifier(Main.MOD_ID, "on_deep_dark_explorer_maps"));
    public static final TagKey<Structure> ON_DESERT_EXPLORER_MAPS = TagKey.of(RegistryKeys.STRUCTURE, new Identifier(Main.MOD_ID, "on_desert_explorer_maps"));
    public static final TagKey<Structure> ON_JUNGLE_EXPLORER_MAPS = TagKey.of(RegistryKeys.STRUCTURE, new Identifier(Main.MOD_ID, "on_jungle_explorer_maps"));
    public static final TagKey<Structure> ON_TUNDRA_EXPLORER_MAPS = TagKey.of(RegistryKeys.STRUCTURE, new Identifier(Main.MOD_ID, "on_tundra_explorer_maps"));
}
