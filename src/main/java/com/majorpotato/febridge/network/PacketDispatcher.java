package com.majorpotato.febridge.network;


import com.majorpotato.febridge.handler.PacketHandler;
import com.majorpotato.febridge.network.packet.BasicPacket;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.world.WorldServer;

import java.lang.reflect.Method;

public class PacketDispatcher {

    private static final Class playerInstanceClass;
    private static final Method getOrCreateChunkWatcher;
    private static final Method sendToAllPlayersWatchingChunk;

    static {
        try {
            playerInstanceClass = PlayerManager.class.getDeclaredClasses()[0];
            getOrCreateChunkWatcher = ReflectionHelper.findMethod(PlayerManager.class, null, new String[]{"func_72690_a", "getOrCreateChunkWatcher"}, int.class, int.class, boolean.class);
            sendToAllPlayersWatchingChunk = ReflectionHelper.findMethod(playerInstanceClass, null, new String[]{"func_151251_a", "sendToAllPlayersWatchingChunk"}, Packet.class);
            getOrCreateChunkWatcher.setAccessible(true);
            sendToAllPlayersWatchingChunk.setAccessible(true);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void sendToServer(BasicPacket packet) {
        PacketHandler.INSTANCE.channel.sendToServer(packet.getPacket());
    }

    public static void sendToPlayer(BasicPacket packet, EntityPlayerMP player) {
        PacketHandler.INSTANCE.channel.sendTo(packet.getPacket(), player);
    }

    public static void sendToAll(BasicPacket packet) {
        PacketHandler.INSTANCE.channel.sendToAll(packet.getPacket());
    }

    public static void sendToAllAround(BasicPacket packet, NetworkRegistry.TargetPoint zone) {
        PacketHandler.INSTANCE.channel.sendToAllAround(packet.getPacket(), zone);
    }

    public static void sendToDimension(BasicPacket packet, int dimensionId) {
        PacketHandler.INSTANCE.channel.sendToDimension(packet.getPacket(), dimensionId);
    }

    public static void sendToWatchers(BasicPacket packet, WorldServer world, int worldX, int worldZ) {
        try {
            Object playerInstance = getOrCreateChunkWatcher.invoke(world.getPlayerManager(), worldX >> 4, worldZ >> 4, false);
            if (playerInstance != null)
                sendToAllPlayersWatchingChunk.invoke(playerInstance, (Packet) packet.getPacket());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
