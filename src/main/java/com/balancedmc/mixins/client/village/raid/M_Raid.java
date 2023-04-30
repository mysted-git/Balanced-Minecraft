package com.balancedmc.mixins.client.village.raid;

import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.Heightmap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

/**
 * Drop totems at the end of a raid
 * <p>
 * One totem dropped for every player
 * <p>
 * Totems drop at village centre
 */

@Mixin(Raid.class)
public abstract class M_Raid {

    @Final @Shadow
    private ServerWorld world;
    @Shadow
    private BlockPos center;
    private BlockPos rewardPos;
    private final ArrayList<ItemEntity> rewardItems = new ArrayList<>();

    // spawn a totem for every player
    @Inject(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;incrementStat(Lnet/minecraft/util/Identifier;)V"))
    private void injected(CallbackInfo ci) {
        ItemStack stack = new ItemStack(Items.TOTEM_OF_UNDYING);
        rewardPos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, center);
        ItemEntity itemEntity = new ItemEntity(world, rewardPos.getX(), rewardPos.getY(), rewardPos.getZ(), stack);
        itemEntity.setToDefaultPickupDelay();
        rewardItems.add(itemEntity);
        world.spawnEntity(itemEntity);
    }

    // add particle beacon
    @Inject(method = "tick()V", at = @At(value = "TAIL"))
    private void injectedTail(CallbackInfo ci) {
        if (rewardPos != null) {
            for (ItemEntity item : rewardItems) {
                if (!item.isRemoved()) {
                    world.spawnParticles(ParticleTypes.GLOW, rewardPos.getX(), rewardPos.getY(), rewardPos.getZ(), 100, 0.1, 10, 0.1, 0);
                    break;
                }
            }
        }
    }
}
