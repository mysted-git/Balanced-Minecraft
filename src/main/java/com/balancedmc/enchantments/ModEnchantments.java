package com.balancedmc.enchantments;

import com.balancedmc.Main;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ModEnchantments {

    public static Enchantment UNDEAD_PROTECTION = new MobProtectionEnchantment(EntityGroup.UNDEAD);
    public static Enchantment ARTHROPOD_PROTECTION = new MobProtectionEnchantment(EntityGroup.ARTHROPOD);
    public static Enchantment AQUATIC_PROTECTION = new MobProtectionEnchantment(EntityGroup.AQUATIC, new EntityType[]{EntityType.SLIME});
    public static Enchantment ILLAGER_PROTECTION = new MobProtectionEnchantment(EntityGroup.ILLAGER, new EntityType[]{EntityType.VEX, EntityType.WITCH});

    public static void registerEnchantments() {
        Registry.register(Registries.ENCHANTMENT, new Identifier(Main.MOD_ID, "undead_protection"), UNDEAD_PROTECTION);
        Registry.register(Registries.ENCHANTMENT, new Identifier(Main.MOD_ID, "arthropod_protection"), ARTHROPOD_PROTECTION);
        Registry.register(Registries.ENCHANTMENT, new Identifier(Main.MOD_ID, "aquatic_protection"), AQUATIC_PROTECTION);
        Registry.register(Registries.ENCHANTMENT, new Identifier(Main.MOD_ID, "illager_protection"), ILLAGER_PROTECTION);

        registerBooks();
    }

    private static void registerBooks() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content ->
                content.addAll(new ArrayList<>(List.of(
                        EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(Enchantments.FIRE_PROTECTION, 5)),
                        EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(Enchantments.FEATHER_FALLING, 5)),
                        EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(Enchantments.BLAST_PROTECTION, 5)),
                        EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(Enchantments.PROJECTILE_PROTECTION, 5))
                )))
        );
    }
}
