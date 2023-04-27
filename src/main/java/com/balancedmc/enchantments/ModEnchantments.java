package com.balancedmc.enchantments;

import com.balancedmc.Main;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEnchantments {

    public static Enchantment UNDEAD_PROTECTION = new MobProtectionEnchantment(EntityGroup.UNDEAD);
    public static Enchantment ARTHROPOD_PROTECTION = new MobProtectionEnchantment(EntityGroup.ARTHROPOD);
    public static Enchantment AQUATIC_PROTECTION = new MobProtectionEnchantment(EntityGroup.AQUATIC);
    public static Enchantment ILLAGER_PROTECTION = new MobProtectionEnchantment(EntityGroup.ILLAGER, new EntityType[]{EntityType.VEX, EntityType.WITCH});

    public static void registerEnchantments() {
        Registry.register(Registries.ENCHANTMENT, new Identifier(Main.MOD_ID, "undead_protection"), UNDEAD_PROTECTION);
        Registry.register(Registries.ENCHANTMENT, new Identifier(Main.MOD_ID, "arthropod_protection"), ARTHROPOD_PROTECTION);
        Registry.register(Registries.ENCHANTMENT, new Identifier(Main.MOD_ID, "aquatic_protection"), AQUATIC_PROTECTION);
        Registry.register(Registries.ENCHANTMENT, new Identifier(Main.MOD_ID, "illager_protection"), ILLAGER_PROTECTION);
    }
}
