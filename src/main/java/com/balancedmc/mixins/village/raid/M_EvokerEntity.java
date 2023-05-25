package com.balancedmc.mixins.village.raid;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.SpellcastingIllagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Prevent evokers in raids dropping totems
 * <p>
 * Only evokers from structures (mansions) can drop totems
 */

@Mixin(EvokerEntity.class)
public abstract class M_EvokerEntity extends SpellcastingIllagerEntity {

    protected M_EvokerEntity(EntityType<? extends SpellcastingIllagerEntity> entityType, World world) {
        super(entityType, world);
    }

    private boolean fromMansion;

    @Override
    public ItemEntity dropStack(ItemStack stack) {
        if (!this.fromMansion && stack.isOf(Items.TOTEM_OF_UNDYING)) {
            return null;
        }
        else {
            return super.dropStack(stack);
        }
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.fromMansion = spawnReason == SpawnReason.STRUCTURE;
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.fromMansion = nbt.getBoolean("FromMansion");
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("FromMansion", this.fromMansion);
    }
}
