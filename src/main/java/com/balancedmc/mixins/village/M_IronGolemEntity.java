package com.balancedmc.mixins.village;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SpiderNavigation;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IronGolemEntity.class)
public abstract class M_IronGolemEntity extends GolemEntity {

    private static final TrackedData<Byte> SPIDER_FLAGS;

    protected M_IronGolemEntity(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);
    }

    private double direction(double x) {
        return x == 0 ? x : x / Math.abs(x);
    }

    private static final double SPEED_IN_WATER = 0.014;

    /**
     * Iron golems cannot be pushed by water
     */

    @Override
    public boolean isPushedByFluids() {
        return false;
    }

    @Override
    public boolean isTouchingWater() {
        return false;
    }


    /**
     * Iron golems can climb walls
     */

    @Inject(method = "initDataTracker()V", at = @At("TAIL"))
    private void injected(CallbackInfo ci) {
        this.dataTracker.startTracking(SPIDER_FLAGS, (byte)0);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.world.isClient) {
            this.setClimbingWall(this.horizontalCollision);
        }
    }

    public boolean isClimbing() {
        return this.isClimbingWall();
    }

    public boolean isClimbingWall() {
        return ((Byte)this.dataTracker.get(SPIDER_FLAGS) & 1) != 0;
    }

    public void setClimbingWall(boolean climbing) {
        byte b = (Byte)this.dataTracker.get(SPIDER_FLAGS);
        if (climbing) {
            b = (byte)(b | 1);
        } else {
            b &= -2;
        }

        this.dataTracker.set(SPIDER_FLAGS, b);
    }

    public EntityNavigation createNavigation(World world) {
        return new SpiderNavigation((IronGolemEntity)(Object)this, world);
    }

    static {
        SPIDER_FLAGS = DataTracker.registerData(SpiderEntity.class, TrackedDataHandlerRegistry.BYTE);
    }
}
