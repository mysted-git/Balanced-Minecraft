package com.balancedmc;

import com.balancedmc.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.render.entity.ShulkerEntityRenderer;

public class MainClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.SENTRY_SHULKER, ShulkerEntityRenderer::new);

        ClientPlayConnectionEvents.JOIN.register((networkHandler, packetSender, client) -> {
            packetSender.sendPacket(MainServer.VERSION_VERIFIER_PACKET_ID, PacketByteBufs.create().writeString(Main.MOD_VERSION));
        });
    }
}
