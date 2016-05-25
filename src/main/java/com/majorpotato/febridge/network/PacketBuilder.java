package com.majorpotato.febridge.network;

import com.majorpotato.febridge.network.packet.*;
import com.majorpotato.febridge.tileentity.ICurrencyService;
import com.majorpotato.febridge.util.Misc;
import com.majorpotato.febridge.util.PermCache;
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
    public void requestGroups() {
        PacketRequest pkt = new PacketRequest(DataType.PERM_GROUPS);
        PermCache.instance().waitForGroups();
        PacketDispatcher.sendToServer(pkt);
    }

    @SideOnly(Side.CLIENT)
    public void requestGroupData(String groupName) {
        PacketRequest pkt = new PacketRequest(DataType.PERM_GROUP_DATA).setName(groupName);
        PermCache.instance().waitForGroupData();
        PacketDispatcher.sendToServer(pkt);
    }

    @SideOnly(Side.CLIENT)
    public void requestUsers() {
        PacketRequest pkt = new PacketRequest(DataType.PERM_USERS);
        PermCache.instance().waitForUsers();
        PacketDispatcher.sendToServer(pkt);
    }

    @SideOnly(Side.CLIENT)
    public void requestUserData(String userName) {
        PacketRequest pkt = new PacketRequest(DataType.PERM_USER_DATA).setName(userName);
        PermCache.instance().waitForUserData();
        PacketDispatcher.sendToServer(pkt);
    }

    @SideOnly(Side.SERVER)
    public void sendGroups(UUID receiverUUID, String[] groups) {
        PacketData pkt = new PacketData(DataType.PERM_GROUPS).setNameList(groups);
        PacketDispatcher.sendToPlayer(pkt, Misc.getPlayerFromUUID(receiverUUID));
    }

    @SideOnly(Side.SERVER)
    public void sendGroupData(UUID receiverUUID, String groupName, String[] groupPerms, String[] groupUsers) {
        PacketData pkt = new PacketData(DataType.PERM_GROUP_DATA).setName(groupName).setPermList(groupPerms).setNameList(groupUsers);
        PacketDispatcher.sendToPlayer(pkt, Misc.getPlayerFromUUID(receiverUUID));
    }

    @SideOnly(Side.SERVER)
    public void sendUsers(UUID receiverUUID, String[] users) {
        PacketData pkt = new PacketData(DataType.PERM_USERS).setNameList(users);
        PacketDispatcher.sendToPlayer(pkt, Misc.getPlayerFromUUID(receiverUUID));
    }

    @SideOnly(Side.SERVER)
    public void sendUserData(UUID receiverUUID, String userName, String[] userPerms, String[] userGroups) {
        PacketData pkt = new PacketData(DataType.PERM_USER_DATA).setName(userName).setPermList(userPerms).setNameList(userGroups);
        PacketDispatcher.sendToPlayer(pkt, Misc.getPlayerFromUUID(receiverUUID));
    }

    @SideOnly(Side.SERVER)
    public void commandClientToOpenGui(EntityPlayerMP player, int guiID) {
        PacketCommand pkt = new PacketCommand(PacketCommand.CommandType.OPEN_GUI).setGuiID(guiID);
        PacketDispatcher.sendToPlayer(pkt, player);
    }

}
