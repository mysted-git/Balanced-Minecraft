package com.balancedmc.mixins.enchantment.power;

import com.balancedmc.Main;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.BowItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BowItem.class)
public abstract class M_BowItem {

    /**
     * Remove the effect of power<br>
     * This is now handled when an entity is damaged, in order to have different effects for different entity types
     */
    @Redirect(
            method = "onStoppedUsing(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;setDamage(D)V"
            )
    )
    private void redirect(PersistentProjectileEntity projectile, double damage) {
        Main.log("Setting damage B", damage);
        projectile.setDamage(-damage);
    }
}
