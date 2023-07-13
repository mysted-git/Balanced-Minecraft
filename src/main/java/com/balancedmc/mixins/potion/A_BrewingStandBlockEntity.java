package com.balancedmc.mixins.potion;

import net.minecraft.block.entity.BrewingStandBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BrewingStandBlockEntity.class)
public interface A_BrewingStandBlockEntity {

    @Accessor("fuel")
    public void setFuel(int fuel);

}
