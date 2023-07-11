package com.balancedmc.potion;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModPotions {

    public static void registerPotions() {}

    public static final Potion MINGLING;

    static {
        MINGLING = registerPotion("mingling", new Potion(new StatusEffectInstance[]{new StatusEffectInstance(ModStatusEffects.MINGLING, 0)}));
    }

    public static Potion registerPotion(String name, Potion potion) {
        return (Potion) Registry.register(Registries.POTION, name, potion);
    }
}
