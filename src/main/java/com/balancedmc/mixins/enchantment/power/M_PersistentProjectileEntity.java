package com.balancedmc.mixins.enchantment.power;

import com.balancedmc.Main;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Power deals less damage to all mobs EXCEPT undead and arthropods<br>
 * Piercing deals extra damage to illagers
 */
@Mixin(PersistentProjectileEntity.class)
public abstract class M_PersistentProjectileEntity {

    @Shadow public abstract byte getPierceLevel();

    private double powerDamage;

    /**
     * Add power and impaling damage when the projectile hits an entity
     */
    @Redirect(
            method = "onEntityHit(Lnet/minecraft/util/hit/EntityHitResult;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
            )
    )
    private boolean redirect(Entity entity, DamageSource source, float amount) {
        if (entity instanceof LivingEntity livingEntity) {
            if (livingEntity.getGroup() == EntityGroup.UNDEAD || livingEntity.getGroup() == EntityGroup.ARTHROPOD) {
                amount += powerDamage;
            }
            else {
                amount += powerDamage * 0.8;
            }
            if (livingEntity.getGroup() == EntityGroup.ILLAGER) {
                amount += getPierceLevel() * 2;
            }
        }
        return entity.damage(source, amount);
    }

    /**
     * Store power damage which may be applied
     */
    @Inject(
            method = "setDamage(D)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void injected(double damage, CallbackInfo ci) {
        if (damage < 0) {
            this.powerDamage = -damage;
            ci.cancel();
        }
    }

    /**
     * Remove old method of dealing with power
     */
    @Redirect(
            method = "applyEnchantmentEffects(Lnet/minecraft/entity/LivingEntity;F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;setDamage(D)V",
                    ordinal = 1
            )
    )
    private void redirect(PersistentProjectileEntity projectile, double damage) {
        projectile.setDamage(-damage);
    }
}
