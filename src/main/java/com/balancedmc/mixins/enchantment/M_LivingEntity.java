package com.balancedmc.mixins.enchantment;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

/** Frost walker II speed boost */

@Mixin(LivingEntity.class)
public abstract class M_LivingEntity extends Entity {

    public M_LivingEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Final @Shadow
    private static UUID SOUL_SPEED_BOOST_ID;
    UUID FROST_WALKER_BOOST_ID = SOUL_SPEED_BOOST_ID;

    @Inject(
            method = "applyMovementEffects(Lnet/minecraft/util/math/BlockPos;)V",
            at = @At("TAIL")
    )
    private void injected(BlockPos pos, CallbackInfo ci) {
        this.addFrostWalkerBoostIfNeeded();
    }

    private void addFrostWalkerBoostIfNeeded() {
        LivingEntity entity = (LivingEntity)(Object)this;
        if (!this.getLandingBlockState().isAir() && EnchantmentHelper.getEquipmentLevel(Enchantments.FROST_WALKER, entity) >= 2 && this.isOnFrostWalkerBlock()) {
            EntityAttributeInstance entityAttributeInstance = entity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            if (entityAttributeInstance == null) {
                return;
            }
            entityAttributeInstance.addTemporaryModifier(new EntityAttributeModifier(FROST_WALKER_BOOST_ID, "Frost walker boost", 0.0405, EntityAttributeModifier.Operation.ADDITION));
        }
    }

    protected boolean isOnFrostWalkerBlock() {
        return this.getWorld().getBlockState(this.getVelocityAffectingPos()).isIn(BlockTags.ICE);
    }
}
