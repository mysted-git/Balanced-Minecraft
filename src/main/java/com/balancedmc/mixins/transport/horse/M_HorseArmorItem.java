package com.balancedmc.mixins.transport.horse;

import net.minecraft.item.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Horse armour can be enchanted on an enchantment table
 */
@Mixin(HorseArmorItem.class)
public abstract class M_HorseArmorItem extends Item {

    @Shadow @Final private int bonus;

    public M_HorseArmorItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantability() {
        ArmorMaterial material;
        if (this.bonus == 3) {
            material = ArmorMaterials.LEATHER;
        }
        else if (this.bonus == 5) {
            material = ArmorMaterials.IRON;
        }
        else if (this.bonus == 7) {
            material = ArmorMaterials.GOLD;
        }
        else if (this.bonus == 11) {
            material = ArmorMaterials.DIAMOND;
        }
        else {
            return 0;
        }
        return material.getEnchantability();
    }
}
