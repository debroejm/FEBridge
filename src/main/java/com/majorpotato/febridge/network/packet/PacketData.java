package com.majorpotato.febridge.network.packet;


import com.majorpotato.febridge.util.PermCache;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class PacketData extends BasicPacket {

    // Required Data
    private DataType type;

    // Optional Data; based off of the Request type
    private String name;
    private String[] nameList;
    private String[] permList;

    @SideOnly(Side.CLIENT)
    public PacketData() { super(); }

    @SideOnly(Side.SERVER)
    public PacketData(DataType type) {
        this.type = type;
    }

    @SideOnly(Side.SERVER)
    public PacketData setName(String name) {
        this.name = name;
        return this;
    }

    @SideOnly(Side.SERVER)
    public PacketData setNameList(String[] nameList) {
        this.nameList = nameList;
        return this;
    }

    @SideOnly(Side.SERVER)
    public PacketData setPermList(String[] permList) {
        this.permList = permList;
        return this;
    }

    @Override
    @SideOnly(Side.SERVER)
    public void writeData(DataOutputStream data) throws IOException {
        data.writeInt(type.ordinal());
        switch(type) {
            case PERM_GROUPS:
            case PERM_USERS:
                data.writeInt(nameList.length);
                for(int i = 0; i < nameList.length; i++)
                    data.writeUTF(nameList[i]);
                break;
            case PERM_GROUP_DATA:
            case PERM_USER_DATA:
                data.writeUTF(name);
                data.writeInt(permList.length);
                for(int i = 0; i < permList.length; i++)
                    data.writeUTF(permList[i]);
                data.writeInt(nameList.length);
                for(int i = 0; i < nameList.length; i++)
                    data.writeUTF(nameList[i]);
                break;
            default: break;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void readData(DataInputStream data) throws IOException {
        type = DataType.values()[data.readInt()];
        int l;
        switch(type) {
            case PERM_GROUPS:
                l = data.readInt();
                nameList = new String[l];
                for(int i = 0; i < l; i++)
                    nameList[i] = data.readUTF();
                PermCache.instance().setGroups(nameList);
                break;
            case PERM_GROUP_DATA:
                name = data.readUTF();
                l = data.readInt();
                permList = new String[l];
                for(int i = 0; i < l; i++)
                    permList[i] = data.readUTF();
                l = data.readInt();
                nameList = new String[l];
                for(int i = 0; i < l; i++)
                    nameList[i] = data.readUTF();
                PermCache.instance().setGroupData(permList, nameList, name);
                break;
            case PERM_USERS:
                l = data.readInt();
                nameList = new String[l];
                for(int i = 0; i < l; i++)
                    nameList[i] = data.readUTF();
                PermCache.instance().setUsers(nameList);
                break;
            case PERM_USER_DATA:
                name = data.readUTF();
                l = data.readInt();
                permList = new String[l];
                for(int i = 0; i < l; i++)
                    permList[i] = data.readUTF();
                l = data.readInt();
                nameList = new String[l];
                for(int i = 0; i < l; i++)
                    nameList[i] = data.readUTF();
                PermCache.instance().setUserData(permList, nameList, name);
                break;
            default: break;
        }
    }

    @Override
    public int getID() { return PacketType.DATA.ordinal(); }
}
