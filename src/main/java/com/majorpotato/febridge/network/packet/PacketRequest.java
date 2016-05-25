package com.majorpotato.febridge.network.packet;


import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.GroupEntry;
import com.forgeessentials.api.permissions.Zone;
import com.majorpotato.febridge.network.PacketBuilder;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.SortedSet;
import java.util.UUID;

public class PacketRequest extends BasicPacket {

    // Required Data
    private UUID senderUUID;
    private DataType type;

    // Optional Data; based off of the Request type
    private String name;

    @SideOnly(Side.SERVER)
    public PacketRequest() { super(); }

    @SideOnly(Side.CLIENT)
    public PacketRequest(DataType type) {
        this.type = type;
        senderUUID = Minecraft.getMinecraft().thePlayer.getPersistentID();
    }

    @SideOnly(Side.CLIENT)
    public PacketRequest setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void writeData(DataOutputStream data) throws IOException {
        data.writeUTF(senderUUID.toString());
        data.writeInt(type.ordinal());
        switch(type) {
            case PERM_GROUP_DATA:
            case PERM_USER_DATA:
                data.writeUTF(name);
                break;
            default: break;
        }
    }

    @Override
    @SideOnly(Side.SERVER)
    public void readData(DataInputStream data) throws IOException {
        senderUUID = UUID.fromString(data.readUTF());
        type = DataType.values()[data.readInt()];
        switch(type) {
            case PERM_GROUPS:
                if(Loader.isModLoaded("ForgeEssentials")) {
                    String[] groups = new String[APIRegistry.perms.getServerZone().getGroups().size()];
                    groups = APIRegistry.perms.getServerZone().getGroups().toArray(groups);
                    PacketBuilder.instance().sendGroups(senderUUID, groups);
                } else PacketBuilder.instance().sendGroups(senderUUID, new String[]{});
                break;
            case PERM_GROUP_DATA:
                name = data.readUTF();
                if(Loader.isModLoaded("ForgeEssentials")) {
                    Zone.PermissionList perms = APIRegistry.perms.getServerZone().getGroupPermissions(name);
                    Set<UserIdent> groupPlayers = APIRegistry.perms.getServerZone().getGroupPlayers().get(name);
                    if(perms == null) PacketBuilder.instance().sendGroupData(senderUUID, name, new String[]{}, new String[]{});
                    else {
                        String[] permArray = new String[perms.size()];
                        permArray = perms.toList().toArray(permArray);
                        UserIdent[] groupPlayerArray = new UserIdent[groupPlayers.size()];
                        groupPlayerArray = groupPlayers.toArray(groupPlayerArray);
                        String[] playerArray = new String[groupPlayerArray.length];
                        for(int i = 0; i < groupPlayerArray.length; i++)
                            playerArray[i] = groupPlayerArray[i].getUsername();
                        PacketBuilder.instance().sendGroupData(senderUUID, name, permArray, playerArray);
                    }
                } else PacketBuilder.instance().sendGroupData(senderUUID, name, new String[]{}, new String[]{});
                break;
            case PERM_USERS:
                if(Loader.isModLoaded("ForgeEssentials")) {
                    Set<UserIdent> knownPlayers = APIRegistry.perms.getServerZone().getKnownPlayers();
                    UserIdent[] knownPlayerArray = new UserIdent[knownPlayers.size()];
                    knownPlayerArray = knownPlayers.toArray(knownPlayerArray);
                    String[] playerArray = new String[knownPlayerArray.length];
                    for(int i = 0; i < knownPlayerArray.length; i++)
                        playerArray[i] = knownPlayerArray[i].getUsername();
                    PacketBuilder.instance().sendUsers(senderUUID, playerArray);
                } else PacketBuilder.instance().sendUsers(senderUUID, new String[]{});
                break;
            case PERM_USER_DATA:
                name = data.readUTF();
                if(Loader.isModLoaded("ForgeEssentials")) {
                    Zone.PermissionList perms = APIRegistry.perms.getServerZone().getPlayerPermissions(UserIdent.get(name));
                    SortedSet<GroupEntry> groups = APIRegistry.perms.getServerZone().getPlayerGroups(UserIdent.get(name));
                    String[] permArray;
                    String[] groupArray;
                    if(perms == null) permArray = new String[]{};
                    else permArray = perms.toList().toArray(new String[0]);
                    if(groups == null) groupArray = new String[]{};
                    else {
                        GroupEntry[] entryArray = groups.toArray(new GroupEntry[0]);
                        groupArray = new String[entryArray.length];
                        for(int i = 0; i < entryArray.length; i++) groupArray[i] = entryArray[i].getGroup();
                    }
                    PacketBuilder.instance().sendUserData(senderUUID, name, permArray, groupArray);
                } else PacketBuilder.instance().sendUserData(senderUUID, name, new String[]{}, new String[]{});
                break;
            default: break;
        }
    }

    @Override
    public int getID() { return PacketType.REQUEST.ordinal(); }
}
