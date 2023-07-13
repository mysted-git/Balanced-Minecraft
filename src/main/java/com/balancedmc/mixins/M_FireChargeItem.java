package com.balancedmc.mixins;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.FireChargeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Throwable fire charges
 */
@Mixin(FireChargeItem.class)
public abstract class M_FireChargeItem extends Item {

    public M_FireChargeItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        user.getWorld().syncWorldEvent(WorldEvents.BLAZE_SHOOTS, user.getBlockPos(), 0);
        user.getItemCooldownManager().set(this, 20);
        if (!world.isClient) {
            float yaw = user.getHeadYaw();
            float pitch = user.getPitch();
            double vx = -MathHelper.sin(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));
            double vy = -MathHelper.sin(pitch * ((float)Math.PI / 180));
            double vz = MathHelper.cos(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));
            vx = user.getRandom().nextTriangular(vx, 0.11485000000000001);
            vz = user.getRandom().nextTriangular(vz, 0.11485000000000001);
            SmallFireballEntity smallFireballEntity = new SmallFireballEntity(user.getWorld(), user, vx, vy, vz);
            smallFireballEntity.setPosition(smallFireballEntity.getX(), smallFireballEntity.getY() + user.getEyeHeight(user.getPose()), smallFireballEntity.getZ());
            user.getWorld().spawnEntity(smallFireballEntity);
        }
        if (!user.getAbilities().creativeMode) {
            stack.decrement(1);
        }
        return TypedActionResult.consume(user.getStackInHand(hand));
    }
}
