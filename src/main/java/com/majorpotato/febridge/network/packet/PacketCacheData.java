package com.majorpotato.febridge.network.packet;


import com.majorpotato.febridge.util.cache.CacheType;
import com.majorpotato.febridge.util.cache.CategoryType;
import com.majorpotato.febridge.util.cache.PermCacheClient;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketCacheData extends BasicPacket {

    // Required Data
    private CacheType cacheType;
    private CategoryType categoryType;
    String name;
    String[] data1;
    String[] data2;

    @SideOnly(Side.CLIENT)
    public PacketCacheData() { super(); }

    @SideOnly(Side.SERVER)
    public PacketCacheData(CacheType cacheType, CategoryType categoryType, String name, String[] data1, String[] data2) {
        this.cacheType = cacheType;
        this.categoryType = categoryType;
        this.name = name;
        this.data1 = data1;
        this.data2 = data2;
    }

    @Override
    @SideOnly(Side.SERVER)
    public void writeData(DataOutputStream data) throws IOException {
        data.writeInt(cacheType.ordinal());
        data.writeInt(categoryType.ordinal());
        data.writeBoolean(name == null);
        if(name != null) data.writeUTF(name);
        if(data1 == null) data.writeInt(-1);
        else {
            data.writeInt(data1.length);
            for(String datum : data1) data.writeUTF(datum);
        }
        if(data2 == null) data.writeInt(-1);
        else {
            data.writeInt(data2.length);
            for(String datum : data2) data.writeUTF(datum);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void readData(DataInputStream data) throws IOException {
        cacheType = CacheType.values()[data.readInt()];
        categoryType = CategoryType.values()[data.readInt()];
        if(data.readBoolean()) name = null;
        else name = data.readUTF();
        int count;
        count = data.readInt();
        if(count < 0) data1 = null;
        else {
            data1 = new String[count];
            for(int i = 0; i < count; i++) data1[i] = data.readUTF();
        }
        count = data.readInt();
        if(count < 0) data2 = null;
        else {
            data2 = new String[count];
            for(int i = 0; i < count; i++) data2[i] = data.readUTF();
        }

        if(data2 == null) PermCacheClient.instance().update(cacheType, categoryType, name, data1);
        else PermCacheClient.instance().update(cacheType, categoryType, name, data1, data2);
    }

    @Override
    public int getID() { return PacketType.CACHE_DATA.ordinal(); }
}
