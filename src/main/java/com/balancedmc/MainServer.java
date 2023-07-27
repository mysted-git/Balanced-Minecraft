package com.balancedmc;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

public class MainServer implements DedicatedServerModInitializer {

    public static Identifier VERSION_VERIFIER_PACKET_ID = new Identifier(Main.MOD_ID, "version_verifier");
    static final Set<ServerPlayerEntity> verifiedPlayers = new HashSet<>();

    @Override
    public void onInitializeServer() {
        ServerPlayNetworking.registerGlobalReceiver(VERSION_VERIFIER_PACKET_ID, (server, player, handler, buf, responseSender) -> {
            String clientVersion = buf.readString();
            if (clientVersion.equals(Main.MOD_VERSION)) {
                verifiedPlayers.add(player);
            }
            else {
                verifiedPlayers.remove(player);
                player.sendMessage(Text.of("§cYou have an out of date mod version (" + clientVersion + ") - you need to update to version " + Main.MOD_VERSION));
            }
        });

        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("checkclientversions").executes(context -> {
                for (ServerPlayerEntity player : context.getSource().getServer().getPlayerManager().getPlayerList()) {
                    if (MainServer.verifiedPlayers.contains(player)) {
                        context.getSource().sendMessage(Text.literal("§a" + player.getName().getString() + " has the latest version"));
                    }
                    else {
                        context.getSource().sendMessage(Text.literal("§c" + player.getName().getString() + " has an old version"));
                    }
                }
                return 1;
            }));
        }));
    }
}
