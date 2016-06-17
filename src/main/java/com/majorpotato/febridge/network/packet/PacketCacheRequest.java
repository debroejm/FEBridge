package com.majorpotato.febridge.network.packet;


import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.GroupEntry;
import com.forgeessentials.api.permissions.Zone;
import com.majorpotato.febridge.network.PacketBuilder;
import com.majorpotato.febridge.util.cache.CacheType;
import com.majorpotato.febridge.util.cache.CategoryType;
import com.majorpotato.febridge.util.cache.PermCacheServer;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.UUID;

public class PacketCacheRequest extends BasicPacket {

    // Required Data
    private UUID senderUUID;
    private boolean clearWatching = false;
    private CacheType cacheType;
    private CategoryType categoryType;
    private String name;

    @SideOnly(Side.SERVER)
    public PacketCacheRequest() { super(); }

    @SideOnly(Side.CLIENT)
    public PacketCacheRequest(CacheType cacheType, CategoryType categoryType, String name) {
        this(false, cacheType, categoryType, name);
    }

    @SideOnly(Side.CLIENT)
    public PacketCacheRequest(boolean clearWatching, CacheType cacheType, CategoryType categoryType, String name) {
        this.clearWatching = clearWatching;
        this.cacheType = cacheType;
        this.categoryType = categoryType;
        this.name = name;
        senderUUID = Minecraft.getMinecraft().thePlayer.getPersistentID();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void writeData(DataOutputStream data) throws IOException {
        data.writeUTF(senderUUID.toString());
        data.writeBoolean(clearWatching);
        if(!clearWatching) {
            data.writeInt(cacheType.ordinal());
            data.writeInt(categoryType.ordinal());
            data.writeBoolean(name == null);
            if (name != null) data.writeUTF(name);
        }
    }

    @Override
    @SideOnly(Side.SERVER)
    public void readData(DataInputStream data) throws IOException {
        senderUUID = UUID.fromString(data.readUTF());
        clearWatching = data.readBoolean();
        if(clearWatching) {
            PermCacheServer.instance().clearWatching(senderUUID);
            return;
        }
        cacheType = CacheType.values()[data.readInt()];
        categoryType = CategoryType.values()[data.readInt()];
        if(data.readBoolean()) name = null;
        else name = data.readUTF();

        if(!Loader.isModLoaded("ForgeEssentials")) {
            PacketBuilder.instance().sendCacheData(senderUUID, cacheType, categoryType, name, null, null);
            return;
        }

        String[] data1 = null;
        String[] data2 = null;

        switch(cacheType) {
            case GLOBAL_LISTING:
                switch(categoryType) {
                    case GROUP:
                        data1 = APIRegistry.perms.getServerZone().getGroups().toArray(new String[0]);
                        break;
                    case USER:
                        UserIdent[] users = APIRegistry.perms.getServerZone().getKnownPlayers().toArray(new UserIdent[0]);
                        data1 = new String[users.length];
                        for(int i = 0; i < users.length; i++) data1[i] = users[i].getUsername();
                        break;
                    case ZONE:
                        Zone[] zones = APIRegistry.perms.getServerZone().getZones().toArray(new Zone[0]);
                        data1 = new String[zones.length];
                        for(int i = 0; i < zones.length; i++) data1[i] = zones[i].getName();
                        break;
                    default: break;
                }
                break;
            case PERMISSIONS:
                switch(categoryType) {
                    case GROUP: {
                        Zone.PermissionList perms = APIRegistry.perms.getServerZone().getGroupPermissions(name);
                        if (perms == null) break;
                        data1 = new String[perms.size()];
                        data2 = new String[perms.size()];
                        int i = 0;
                        for (Map.Entry<String, String> perm : perms.entrySet()) {
                            data1[i] = perm.getKey();
                            data2[i] = perm.getValue();
                            i++;
                        }
                    }
                    case USER: {
                        Zone.PermissionList perms = APIRegistry.perms.getServerZone().getPlayerPermissions(UserIdent.get(name));
                        if (perms == null) break;
                        data1 = new String[perms.size()];
                        data2 = new String[perms.size()];
                        int i = 0;
                        for (Map.Entry<String, String> perm : perms.entrySet()) {
                            data1[i] = perm.getKey();
                            data2[i] = perm.getValue();
                            i++;
                        }
                    }
                    // TODO ZONE PERMISSIONS
                    default: break;
                }
                break;
            case LOCAL_LISTING_GROUP:
                switch(categoryType) {
                    case USER:
                        SortedSet<GroupEntry> groupSet = APIRegistry.perms.getPlayerGroups(UserIdent.get(name));
                        if(groupSet == null) break;
                        data1 = new String[groupSet.size()];
                        int i = 0;
                        for (GroupEntry group : groupSet) {
                            data1[i] = group.getGroup();
                            i++;
                        }
                        break;
                    default: break;
                }
                break;
            case LOCAL_LISTING_USER:
                switch(categoryType) {
                    case GROUP:
                        Set<UserIdent> playerSet = APIRegistry.perms.getServerZone().getGroupPlayers().get(name);
                        if(playerSet == null) break;
                        data1 = new String[playerSet.size()];
                        int i = 0;
                        for (UserIdent player : playerSet) {
                            data1[i] = player.getUsername();
                            i++;
                        }
                        break;
                    default: break;
                }
                break;
            default: break;
        }

        PacketBuilder.instance().sendCacheData(senderUUID, cacheType, categoryType, name, data1, data2);
    }

    @Override
    public int getID() { return PacketType.CACHE_REQUEST.ordinal(); }
}
