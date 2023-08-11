package com.balancedmc;

import com.balancedmc.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.ShulkerEntityRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

public class MainClient implements ClientModInitializer {

    public static MainClient instance;

    private static KeyBinding toggleHotbarKey;
    public static int activeHotbar = 0;

    private KeyBinding useSpyglassKey;
    public boolean isUsingSpyglass;
    private ItemStack spyglassInUse;

    @Override
    public void onInitializeClient() {
        instance = this;

        EntityRendererRegistry.register(ModEntities.SENTRY_SHULKER, ShulkerEntityRenderer::new);

        ClientPlayConnectionEvents.JOIN.register((networkHandler, packetSender, client) -> {
            packetSender.sendPacket(MainServer.VERSION_VERIFIER_PACKET_ID, PacketByteBufs.create().writeString(Main.MOD_VERSION));
        });

        toggleHotbarKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.balancedmc.toggle_hotbar",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_ALT,
                "key.categories.inventory"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleHotbarKey.wasPressed()) {
                activeHotbar = activeHotbar == 0 ? 1 : 0;
            }
        });

        useSpyglassKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.balancedmc.use_spyglass",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_Z,
                "key.categories.misc"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            while (this.useSpyglassKey.wasPressed() && !this.isUsingSpyglass) {
                if (this.spyglassInUse == null) {
                    this.spyglassInUse = client.player.getInventory().getStack(57);
                    if (this.spyglassInUse.isEmpty()) this.spyglassInUse = null;
                }
                if (this.spyglassInUse != null) {
                    this.isUsingSpyglass = true;
                    this.spyglassInUse.use(client.world, client.player, Hand.MAIN_HAND);
                }
            }
            if (this.isUsingSpyglass && !this.useSpyglassKey.wasPressed() && !this.useSpyglassKey.isPressed()
                    || (this.spyglassInUse != null && this.spyglassInUse.isEmpty()
                    && !client.player.getInventory().contains(this.spyglassInUse))) {
                spyglassInUse.onStoppedUsing(client.world, client.player, 0);
                isUsingSpyglass = false;
                spyglassInUse = null;
            }
        });
    }
}
