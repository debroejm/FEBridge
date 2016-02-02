package com.majorpotato.febridge.handler;


import com.majorpotato.febridge.network.packet.BasicPacket;
import com.majorpotato.febridge.network.packet.PacketServiceChange;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class PacketHandler {


    private static final BasicPacket.PacketType[] packetTypes = BasicPacket.PacketType.values();
    public static final PacketHandler INSTANCE = new PacketHandler();
    public final FMLEventChannel channel;

    private PacketHandler() {
        channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(BasicPacket.CHANNEL_NAME);
        channel.register(this);
    }

    public static void init() {
        // NOOP
    }

    @SubscribeEvent
    public void onPacket(FMLNetworkEvent.ServerCustomPacketEvent event) {
        byte[] data = new byte[event.packet.payload().readableBytes()];
        event.packet.payload().readBytes(data);

        onPacketData(data, ((NetHandlerPlayServer) event.handler).playerEntity);
    }

    @SubscribeEvent
    public void onPacket(FMLNetworkEvent.ClientCustomPacketEvent event) {
        byte[] data = new byte[event.packet.payload().readableBytes()];
        event.packet.payload().readBytes(data);

        System.out.println("PacketHandler Packet Received");

        onPacketData(data, null);
    }

    public void onPacketData(byte[] bData, EntityPlayerMP player) {
        DataInputStream data = new DataInputStream(new ByteArrayInputStream(bData));
        try {
            BasicPacket pkt;

            byte packetID = data.readByte();

            if (packetID < 0)
                return;

            BasicPacket.PacketType type = packetTypes[packetID];
            switch (type) {
                case SERVICE_CHANGE:
                    pkt = new PacketServiceChange();
                    break;
                default:
                    System.out.println("Packet is Unknown");
                    return;
            }
            if (pkt != null)
                pkt.readData(data);
        } catch (IOException e) {
            System.out.println("Exception in PacketHandler.onPacketData: {0} "+e);
        }
    }

}
