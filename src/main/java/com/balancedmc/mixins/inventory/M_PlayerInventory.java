package com.balancedmc.mixins.inventory;

import com.balancedmc.MainClient;
import com.google.common.collect.ImmutableList;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Increase size of the player's inventory to include the tool inventory
 */
@Mixin(PlayerInventory.class)
public abstract class M_PlayerInventory {

    @Shadow @Final public DefaultedList<ItemStack> main;
    @Shadow @Final public DefaultedList<ItemStack> armor;
    @Shadow @Final public DefaultedList<ItemStack> offHand;
    public final DefaultedList<ItemStack> tools = DefaultedList.ofSize(25, ItemStack.EMPTY);
    @Shadow @Final public final List<DefaultedList<ItemStack>> combinedInventory = ImmutableList.of(this.main, this.armor, this.offHand, this.tools);

    @Inject(
            method = "size()I",
            at = @At("RETURN"),
            cancellable = true
    )
    private void injected(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(cir.getReturnValue() + this.tools.size());
    }

    private DefaultedList<ItemStack> getActiveInventory() {
        if (MainClient.activeHotbar == 0) {
            return this.main;
        }
        else {
            return this.tools;
        }
    }

    @Redirect(
            method = "getMainHandStack()Lnet/minecraft/item/ItemStack;",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/player/PlayerInventory;main:Lnet/minecraft/util/collection/DefaultedList;"
            )
    )
    private DefaultedList<ItemStack> redirect_getMainHandStack(PlayerInventory inventory) {
        return this.getActiveInventory();
    }

    @Redirect(
            method = "getBlockBreakingSpeed(Lnet/minecraft/block/BlockState;)F",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/player/PlayerInventory;main:Lnet/minecraft/util/collection/DefaultedList;"
            )
    )
    private DefaultedList<ItemStack> redirect_getBlockBreakingSpeed(PlayerInventory instance) {
        return this.getActiveInventory();
    }

    @Inject(
            method = "writeNbt(Lnet/minecraft/nbt/NbtList;)Lnet/minecraft/nbt/NbtList;",
            at = @At("TAIL")
    )
    private void injected(NbtList nbtList, CallbackInfoReturnable<NbtList> cir) {
        for (int i = 0; i < this.tools.size(); ++i) {
            if (this.tools.get(i).isEmpty()) continue;
            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.putByte("Slot", (byte)(i + 50));
            this.tools.get(i).writeNbt(nbtCompound);
            nbtList.add(nbtCompound);
        }
    }

    /**
     * @author HB0P
     * @reason Read tool inventory data from NBT
     */
    @Overwrite
    public void readNbt(NbtList nbtList) {
        this.main.clear();
        this.tools.clear();
        this.armor.clear();
        this.offHand.clear();
        for (int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            int j = nbtCompound.getByte("Slot") & 0xFF;
            ItemStack itemStack = ItemStack.fromNbt(nbtCompound);
            if (itemStack.isEmpty()) continue;
            if (j >= 0 && j < this.main.size()) {
                this.main.set(j, itemStack);
                continue;
            }
            if (j >= 50 && j < this.tools.size() + 50) {
                this.tools.set(j - 50, itemStack);
                continue;
            }
            if (j >= 100 && j < this.armor.size() + 100) {
                this.armor.set(j - 100, itemStack);
                continue;
            }
            if (j < 150 || j >= this.offHand.size() + 150) continue;
            this.offHand.set(j - 150, itemStack);
        }
    }
}
