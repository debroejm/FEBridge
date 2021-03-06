package com.majorpotato.febridge.network.packet;


import com.majorpotato.febridge.reference.Reference;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import io.netty.buffer.Unpooled;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class BasicPacket {

    public final static String CHANNEL_NAME = Reference.MOD_ID+"_CHNL";

    public enum PacketType {
        // Currently only one packet type, but can be expanded
        SERVICE_CHANGE,
        CACHE_REQUEST,
        CACHE_DATA,
        COMMAND,
        REQUEST
    }

    public FMLProxyPacket getPacket() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(bytes);
        try {
            data.writeByte(getID());
            writeData(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new FMLProxyPacket(Unpooled.wrappedBuffer(bytes.toByteArray()), CHANNEL_NAME);
    }

    public abstract void writeData(DataOutputStream data) throws IOException;

    public abstract void readData(DataInputStream data) throws IOException;

    public abstract int getID();

    @Override
    public String toString() { return getClass().getSimpleName(); }
}
