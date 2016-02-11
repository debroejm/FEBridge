package com.majorpotato.febridge.network.packet;


import com.majorpotato.febridge.tileentity.ICurrencyService;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketServiceChange extends BasicPacket {

    protected ICurrencyService service;

    public PacketServiceChange() { super(); }
    public PacketServiceChange(ICurrencyService service) {
        this.service = service;
    }

    @Override
    public void writeData(DataOutputStream data) throws IOException {
        data.writeInt(service.getXCoord());
        data.writeInt(service.getYCoord());
        data.writeInt(service.getZCoord());
        data.writeInt(service.getWorld().provider.dimensionId);
        data.writeInt(service.getBuyPrice());
        data.writeInt(service.getSellPrice());
    }

    @Override
    public void readData(DataInputStream data) throws IOException {
        int x = data.readInt();
        int y = data.readInt();
        int z = data.readInt();
        World world = DimensionManager.getWorld(data.readInt());
        int buyPrice = data.readInt();
        int sellPrice = data.readInt();
        if(world != null) {
            TileEntity tent = world.getTileEntity(x, y, z);
            if(tent instanceof ICurrencyService) {
                ((ICurrencyService)tent).setBuyPrice(buyPrice);
                ((ICurrencyService)tent).setSellPrice(sellPrice);
            }
        }
    }

    @Override
    public int getID() { return PacketType.SERVICE_CHANGE.ordinal(); }
}
