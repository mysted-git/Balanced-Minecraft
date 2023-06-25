package com.balancedmc.mixins.dragon;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.feature.EndSpikeFeature;
import net.minecraft.world.gen.feature.EndSpikeFeatureConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EndSpikeFeature.class)
public abstract class M_EndSpikeFeature {

    /**
     * Spawn shulkers on end spikes
     */
    @Inject(
            method = "generateSpike(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/util/math/random/Random;Lnet/minecraft/world/gen/feature/EndSpikeFeatureConfig;Lnet/minecraft/world/gen/feature/EndSpikeFeature$Spike;)V",
            at = @At("TAIL")
    )
    private void injected(ServerWorldAccess world, Random random, EndSpikeFeatureConfig config, EndSpikeFeature.Spike spike, CallbackInfo ci) {
        int r = spike.getRadius() + 1;
        List<ShulkerEntity> entities = world.getEntitiesByClass(ShulkerEntity.class, new Box(spike.getCenterX() - r, world.getBottomY(), spike.getCenterZ() - r, spike.getCenterX() + r, spike.getHeight() + 10, spike.getCenterZ() + r), (ShulkerEntity) -> true);
        for (ShulkerEntity entity : entities) {
            entity.discard();
        }
        for (Direction direction : Direction.values()) {
            if (direction.getAxis().isVertical()) continue;
            int x = spike.getCenterX() + (direction.getOffsetX() * r);
            int z = spike.getCenterZ() + (direction.getOffsetZ() * r);
            int top = world.getTopY(Heightmap.Type.MOTION_BLOCKING, x, z);
            int y = (int) (Math.random() * (spike.getHeight() - top)) + top;
            ShulkerEntity shulker = EntityType.SHULKER.create(world.toServerWorld());
            shulker.setPosition(x, y, z);
            world.spawnEntity(shulker);
        }
    }
}
