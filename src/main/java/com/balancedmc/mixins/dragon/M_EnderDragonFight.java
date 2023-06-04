package com.balancedmc.mixins.dragon;

import com.balancedmc.Main;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.Heightmap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EnderDragonFight.class)
public abstract class M_EnderDragonFight {

    @Shadow @Final private ServerWorld world;
    @Shadow @Final private ServerBossBar bossBar;
    @Shadow private boolean dragonKilled;

    /**
     * 0 = destroying end crystals<br>
     * 1 = spawn phantoms<br>
     * 2 = spawn illusioners
     */
    private int stage = 0;

    @Inject(
            method = "crystalDestroyed(Lnet/minecraft/entity/decoration/EndCrystalEntity;Lnet/minecraft/entity/damage/DamageSource;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/boss/dragon/EnderDragonFight;countAliveCrystals()V",
                    shift = At.Shift.AFTER
            )
    )
    private void injected(EndCrystalEntity enderCrystal, DamageSource source, CallbackInfo ci) {
        EnderDragonFight fight = (EnderDragonFight)(Object)this;
        // detect when all crystals destroyed
        if (stage < 1 && fight.getAliveEndCrystals() == 0) {
            stage = 1;
        }
    }

    @Inject(
            method = "tick()V",
            at = @At("TAIL")
    )
    private void injected(CallbackInfo ci) {
        // show stage on boss bar
        this.bossBar.setName(Text.of(this.bossBar.getName().getString().replaceAll(" - Stage \\d", "") + " - Stage " + (stage + 1)));
        // reset stage if no dragon
        if (dragonKilled) {
            stage = 0;
        }
        if (stage == 1) {
            // spawn phantoms
            if (this.world.getEntitiesByClass(PhantomEntity.class, new Box(-50, 0, -50, 50, 255, 50), (PhantomEntity) -> true).size() < 20) {
                double angle = (Math.random() * 360);
                double dist = (Math.random() * 40);
                int x = (int) (dist * Math.sin(angle));
                int z = (int) (dist * Math.cos(angle));
                int y = this.world.getTopY(Heightmap.Type.MOTION_BLOCKING, x, z);
                EntityType.PHANTOM.spawn(world, new BlockPos(x, y, z), SpawnReason.NATURAL);
            }
        }
        if (!dragonKilled && stage < 2 && this.bossBar.getPercent() != 0 && this.bossBar.getPercent() <= 0.5) {
            stage = 2;
            // spawn illusioners
            for (int i = 0; i < 10; i++) {
                double angle = Math.toRadians(i * 36);
                int x = (int) (42 * Math.sin(angle));
                int z = (int) (42 * Math.cos(angle));
                int y = this.world.getTopY(Heightmap.Type.MOTION_BLOCKING, x, z);
                IllusionerEntity entity = EntityType.ILLUSIONER.spawn(world, new BlockPos(x, y, z), SpawnReason.NATURAL);
                entity.setPersistent();
            }
        }
    }

    @Inject(
            method = "<init>(Lnet/minecraft/server/world/ServerWorld;JLnet/minecraft/nbt/NbtCompound;)V",
            at = @At("TAIL")
    )
    private void injected(ServerWorld world, long gatewaysSeed, NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("Stage")) {
            stage = nbt.getInt("Stage");
        }
    }

    @Inject(
            method = "toNbt()Lnet/minecraft/nbt/NbtCompound;",
            at = @At("TAIL"),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void injected(CallbackInfoReturnable<NbtCompound> cir, NbtCompound nbtCompound) {
        nbtCompound.putInt("Stage", stage);
    }
}