package com.majorpotato.febridge.network;

import com.majorpotato.febridge.network.packet.PacketServiceChange;
import com.majorpotato.febridge.tileentity.ICurrencyService;

public class PacketBuilder {

    private static PacketBuilder instance;

    public static PacketBuilder instance() {
        if (instance == null)
            instance = new PacketBuilder();
        return instance;
    }

    private PacketBuilder() {

    }

    // Tell the server to change a certain shop's data
    public void sendServiceDataChangePacket(ICurrencyService service) {
        PacketServiceChange pkt = new PacketServiceChange(service);
        PacketDispatcher.sendToServer(pkt);
    }

}
