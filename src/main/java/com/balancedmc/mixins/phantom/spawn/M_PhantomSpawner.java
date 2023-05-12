package com.balancedmc.mixins.phantom.spawn;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.spawner.PhantomSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Stop phantoms spawning in the overworld
 */
@Mixin(PhantomSpawner.class)
public abstract class M_PhantomSpawner {

    @Inject(
            method = "spawn(Lnet/minecraft/server/world/ServerWorld;ZZ)I",
            at = @At("HEAD"),
            cancellable = true
    )
    private void injected(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(0);
    }
}
