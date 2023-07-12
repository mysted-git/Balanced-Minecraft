package com.balancedmc.enchantments;

import com.balancedmc.Main;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ModEnchantments {

    public static Enchantment UNDEAD_PROTECTION = new MobProtectionEnchantment(entity -> entity.getGroup() == EntityGroup.UNDEAD);
    public static Enchantment ARTHROPOD_PROTECTION = new MobProtectionEnchantment(entity -> entity.getGroup() == EntityGroup.ARTHROPOD);
    public static Enchantment AQUATIC_PROTECTION = new MobProtectionEnchantment(entity -> entity.getGroup() == EntityGroup.AQUATIC);
    public static Enchantment ILLAGER_PROTECTION = new MobProtectionEnchantment(entity -> entity.getGroup() == EntityGroup.ILLAGER);
    public static Enchantment BANE_OF_THE_AQUATIC = new DamageEnchantment(Enchantment.Rarity.UNCOMMON, 3, EquipmentSlot.MAINHAND);
    public static Enchantment BANE_OF_ILLAGERS = new DamageEnchantment(Enchantment.Rarity.UNCOMMON, 4, EquipmentSlot.MAINHAND);

    public static void registerEnchantments() {
        Registry.register(Registries.ENCHANTMENT, new Identifier(Main.MOD_ID, "undead_protection"), UNDEAD_PROTECTION);
        Registry.register(Registries.ENCHANTMENT, new Identifier(Main.MOD_ID, "arthropod_protection"), ARTHROPOD_PROTECTION);
        Registry.register(Registries.ENCHANTMENT, new Identifier(Main.MOD_ID, "aquatic_protection"), AQUATIC_PROTECTION);
        Registry.register(Registries.ENCHANTMENT, new Identifier(Main.MOD_ID, "illager_protection"), ILLAGER_PROTECTION);

        Registry.register(Registries.ENCHANTMENT, new Identifier(Main.MOD_ID, "bane_of_the_aquatic"), BANE_OF_THE_AQUATIC);
        Registry.register(Registries.ENCHANTMENT, new Identifier(Main.MOD_ID, "bane_of_illagers"), BANE_OF_ILLAGERS);

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
