package com.majorpotato.febridge.network;

import com.majorpotato.febridge.network.packet.*;
import com.majorpotato.febridge.tileentity.ICurrencyService;
import com.majorpotato.febridge.util.Misc;
import com.majorpotato.febridge.util.cache.CacheType;
import com.majorpotato.febridge.util.cache.CategoryType;
import com.majorpotato.febridge.util.cache.PermCacheClient;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.UUID;

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

    @SideOnly(Side.CLIENT)
    public void requestCacheData(CacheType cacheType, CategoryType categoryType, String name) {
        PacketCacheRequest pkt = new PacketCacheRequest(cacheType, categoryType, name);
        PacketDispatcher.sendToServer(pkt);
    }

    @SideOnly(Side.CLIENT)
    public void requestCacheClear() {
        PacketCacheRequest pkt = new PacketCacheRequest(true, null, null, null);
        PacketDispatcher.sendToServer(pkt);
    }

    @SideOnly(Side.SERVER)
    public void sendCacheData(UUID playerID, CacheType cacheType, CategoryType categoryType, String name, String[] data1) {
        sendCacheData(playerID, cacheType, categoryType, name, data1, null);
    }

    @SideOnly(Side.SERVER)
    public void sendCacheData(UUID playerID, CacheType cacheType, CategoryType categoryType, String name, String[] data1, String[] data2) {
        PacketCacheData pkt = new PacketCacheData(cacheType, categoryType, name, data1, data2);
        PacketDispatcher.sendToPlayer(pkt, Misc.getPlayerFromUUID(playerID));
    }

    @SideOnly(Side.SERVER)
    public void commandClientToOpenGui(EntityPlayerMP player, int guiID) {
        PacketCommand pkt = new PacketCommand(PacketCommand.CommandType.OPEN_GUI).setGuiID(guiID);
        PacketDispatcher.sendToPlayer(pkt, player);
    }

    @SideOnly(Side.CLIENT)
    public void requestServerAction(PacketRequest.RequestType type, String ... sData) {
        PacketRequest pkt = new PacketRequest(type, sData);
        PacketDispatcher.sendToServer(pkt);
    }

}
