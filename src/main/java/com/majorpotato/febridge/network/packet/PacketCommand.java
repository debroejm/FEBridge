package com.majorpotato.febridge.network.packet;


import com.majorpotato.febridge.FEBridge;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketCommand extends BasicPacket {

    public enum CommandType {
        OPEN_GUI
    }

    // Required Data
    private CommandType type;

    // Optional Data; based off of the Request type
    private int guiID;

    @SideOnly(Side.CLIENT)
    public PacketCommand() { super(); }

    @SideOnly(Side.SERVER)
    public PacketCommand(CommandType type) {
        this.type = type;
    }

    @SideOnly(Side.SERVER)
    public PacketCommand setGuiID(int guiID) {
        this.guiID = guiID;
        return this;
    }

    @Override
    @SideOnly(Side.SERVER)
    public void writeData(DataOutputStream data) throws IOException {
        data.writeInt(type.ordinal());
        switch(type) {
            case OPEN_GUI:
                data.writeInt(guiID);
                break;
            default: break;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void readData(DataInputStream data) throws IOException {
        type = CommandType.values()[data.readInt()];
        switch(type) {
            case OPEN_GUI:
                FMLNetworkHandler.openGui(Minecraft.getMinecraft().thePlayer, FEBridge.instance, data.readInt(), Minecraft.getMinecraft().thePlayer.getEntityWorld(), 0, 0, 0);
                break;
            default: break;
        }
    }

    @Override
    public int getID() { return PacketType.COMMAND.ordinal(); }
}
