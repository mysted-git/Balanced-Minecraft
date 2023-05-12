package com.balancedmc.entity;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.biome.BiomeKeys;

public class EntitySpawns {

    public static void registerEntitySpawns() {
        // phantoms spawn in the end
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(BiomeKeys.END_HIGHLANDS), SpawnGroup.MONSTER, EntityType.PHANTOM, 1, 1, 1);
    }
}
