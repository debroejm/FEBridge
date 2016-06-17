package com.majorpotato.febridge.network.packet;


import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketRequest extends BasicPacket {

    public enum RequestType {
        GROUP_CREATE,
        GROUP_PERM_SET,
        GROUP_ADD_USER,
        GROUP_REMOVE_USER,
        USER_PERM_SET
    }

    // Required Data
    RequestType type;
    String[] sData;

    @SideOnly(Side.SERVER)
    public PacketRequest() {
        super();
    }

    @SideOnly(Side.CLIENT)
    public PacketRequest(RequestType type, String[] sData) {
        this.type = type;
        this.sData = sData;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void writeData(DataOutputStream data) throws IOException {
        data.writeInt(type.ordinal());
        data.writeInt(sData.length);
        for(int i = 0; i < sData.length; i++) {
            data.writeBoolean(sData[i] == null);
            if(sData[i] != null) data.writeUTF(sData[i]);
        }
    }

    @Override
    @SideOnly(Side.SERVER)
    public void readData(DataInputStream data) throws IOException {
        type = RequestType.values()[data.readInt()];
        int sDataSize = data.readInt();
        sData = new String[sDataSize];
        for(int i = 0; i < sDataSize; i++) {
            if(data.readBoolean()) sData[i] = null;
            else sData[i] = data.readUTF();
        }

        String output = "Packet Request: ";
        for(int i = 0; i < sDataSize; i++) {
            if(sData[i] == null) output += "["+i+"|NULL]";
            else output += "["+i+"|"+sData[i]+"]";
        }
        System.out.println(output);

        switch(type) {
            case GROUP_CREATE: {
                if (!Loader.isModLoaded("ForgeEssentials") || sData.length < 1 || sData[0] == null) break;
                APIRegistry.perms.createGroup(sData[0]);
                break;
            }
            case GROUP_ADD_USER: {
                if (!Loader.isModLoaded("ForgeEssentials") || sData.length < 2 || sData[0] == null || sData[1] == null)
                    break;
                UserIdent ui = UserIdent.get(sData[1]);
                if (ui != null) APIRegistry.perms.addPlayerToGroup(ui, sData[0]);
                break;
            }
            case GROUP_REMOVE_USER: {
                if (!Loader.isModLoaded("ForgeEssentials") || sData.length < 2 || sData[0] == null || sData[1] == null)
                    break;
                UserIdent ui = UserIdent.get(sData[1]);
                if (ui != null) APIRegistry.perms.removePlayerFromGroup(ui, sData[0]);
                break;
            }
            case GROUP_PERM_SET: {
                if (!Loader.isModLoaded("ForgeEssentials") || sData.length < 3 || sData[0] == null || sData[1] == null)
                    break;
                APIRegistry.perms.setGroupPermissionProperty(sData[0], sData[1], sData[2]);
                break;
            }
            case USER_PERM_SET: {
                if (!Loader.isModLoaded("ForgeEssentials") || sData.length < 3 || sData[0] == null || sData[1] == null)
                    break;
                UserIdent ui = UserIdent.get(sData[0]);
                if (ui != null) APIRegistry.perms.setPlayerPermissionProperty(ui, sData[1], sData[2]);
                break;
            }
            default: break;
        }
    }

    @Override
    public int getID() { return PacketType.REQUEST.ordinal(); }
}
