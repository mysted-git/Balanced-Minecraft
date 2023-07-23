package com.balancedmc.mixins.transport.minecart;

import com.balancedmc.Main;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RailBlock;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(MinecartEntity.class)
public abstract class M_MinecartEntity extends AbstractMinecartEntity {

    protected M_MinecartEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected double getMaxSpeed() {
        List<Entity> passengers = this.getPassengerList();
        if (passengers.size() > 0 && passengers.get(0) instanceof PlayerEntity) {
            BlockState state = this.getWorld().getBlockState(this.getBlockPos());
            if (state.isIn(BlockTags.RAILS)) {
                if (state.isOf(Blocks.RAIL) && isRailCurved(state.get(RailBlock.SHAPE))) {
                    // curved rail
                    return super.getMaxSpeed();
                }
                else {
                    // straight rail
                    return super.getMaxSpeed() * 2.5;
                }
            }
        }
        // off-rail, uphill, or no player
        return super.getMaxSpeed();
    }

    private boolean isRailCurved(RailShape shape) {
        return shape == RailShape.NORTH_EAST || shape == RailShape.NORTH_WEST || shape == RailShape.SOUTH_EAST || shape == RailShape.SOUTH_WEST;
    }
}
