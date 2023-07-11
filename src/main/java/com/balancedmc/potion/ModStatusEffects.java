package com.balancedmc.potion;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModStatusEffects {

    public static void registerStatusEffects() {}

    public static final StatusEffect MINGLING;

    static {
        MINGLING = register("mingling", new ModStatusEffect(StatusEffectCategory.NEUTRAL, 0xf9faef));
    }

    private static StatusEffect register(String id, StatusEffect entry) {
        return (StatusEffect) Registry.register(Registries.STATUS_EFFECT, new Identifier("balancedmc", id), entry);
    }

}
