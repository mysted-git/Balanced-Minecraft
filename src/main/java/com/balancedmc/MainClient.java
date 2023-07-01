package com.balancedmc;

import com.balancedmc.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.ShulkerEntityRenderer;

public class MainClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.SENTRY_SHULKER, ShulkerEntityRenderer::new);
    }
}
