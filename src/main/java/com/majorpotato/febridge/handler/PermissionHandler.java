package com.majorpotato.febridge.handler;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.GroupEntry;
import com.forgeessentials.api.permissions.PermissionEvent;
import com.forgeessentials.api.permissions.Zone;
import com.majorpotato.febridge.network.PacketBuilder;
import com.majorpotato.febridge.util.cache.CacheType;
import com.majorpotato.febridge.util.cache.CategoryType;
import com.majorpotato.febridge.util.cache.PermCacheServer;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.*;

@SideOnly(Side.SERVER)
public class PermissionHandler {

    @SubscribeEvent
    public void User_ModifyPermission(PermissionEvent.User.ModifyPermission event) {
        List<UUID> users = PermCacheServer.instance().getUsersWatching(CacheType.PERMISSIONS, CategoryType.USER, event.ident.getUsername());
        if(!users.isEmpty()) {
            Zone.PermissionList perms = APIRegistry.perms.getServerZone().getPlayerPermissions(event.ident);
            if (perms == null) return;
            String[] data1 = new String[perms.size()];
            String[] data2 = new String[perms.size()];
            int i = 0;
            for (Map.Entry<String, String> perm : perms.entrySet()) {
                data1[i] = perm.getKey();
                data2[i] = perm.getValue();
                i++;
            }
            for (UUID user : users) PacketBuilder.instance().sendCacheData(user, CacheType.PERMISSIONS, CategoryType.USER, event.ident.getUsername(), data1, data2);
        }
    }

    @SubscribeEvent
    public void User_ModifyGroups(PermissionEvent.User.ModifyGroups event) {
        List<UUID> users = PermCacheServer.instance().getUsersWatching(CacheType.LOCAL_LISTING_GROUP, CategoryType.USER, event.ident.getUsername());
        if(!users.isEmpty()) {
            SortedSet<GroupEntry> groupSet = APIRegistry.perms.getPlayerGroups(event.ident);
            if(groupSet != null) {
                String[] data1 = new String[groupSet.size()];
                int i = 0;
                for (GroupEntry group : groupSet) {
                    data1[i] = group.getGroup();
                    i++;
                }
                for (UUID user : users) PacketBuilder.instance().sendCacheData(user, CacheType.LOCAL_LISTING_GROUP, CategoryType.USER, event.ident.getUsername(), data1);
            }
        }
        users = PermCacheServer.instance().getUsersWatching(CacheType.LOCAL_LISTING_USER, CategoryType.GROUP, event.group);
        if(!users.isEmpty()) {
            Set<UserIdent> userSet = APIRegistry.perms.getServerZone().getGroupPlayers().get(event.group);
            if(userSet != null) {
                String[] data1 = new String[userSet.size()];
                int i = 0;
                for (UserIdent user : userSet) {
                    data1[i] = user.getUsername();
                    i++;
                }
                for (UUID user : users) PacketBuilder.instance().sendCacheData(user, CacheType.LOCAL_LISTING_USER, CategoryType.GROUP, event.group, data1);
            }
        }
    }

    @SubscribeEvent
    public void Group_ModifyPermission(PermissionEvent.Group.ModifyPermission event) {
        List<UUID> users = PermCacheServer.instance().getUsersWatching(CacheType.PERMISSIONS, CategoryType.GROUP, event.group);
        if(!users.isEmpty()) {
            Zone.PermissionList perms = APIRegistry.perms.getServerZone().getGroupPermissions(event.group);
            if (perms == null) return;
            String[] data1 = new String[perms.size()];
            String[] data2 = new String[perms.size()];
            int i = 0;
            for (Map.Entry<String, String> perm : perms.entrySet()) {
                data1[i] = perm.getKey();
                data2[i] = perm.getValue();
                i++;
            }
            for (UUID user : users) PacketBuilder.instance().sendCacheData(user, CacheType.PERMISSIONS, CategoryType.GROUP, event.group, data1, data2);
        }
    }

    @SubscribeEvent
    public void Group_Create(PermissionEvent.Group.Create event) {
        List<UUID> users = PermCacheServer.instance().getUsersWatching(CacheType.GLOBAL_LISTING, CategoryType.GROUP, null);
        if(!users.isEmpty()) {
            String[] data1 = APIRegistry.perms.getServerZone().getGroups().toArray(new String[0]);
            for(UUID user : users) PacketBuilder.instance().sendCacheData(user, CacheType.GLOBAL_LISTING, CategoryType.GROUP, null, data1);
        }
    }

    @SubscribeEvent
    public void Group_Delete(PermissionEvent.Group.Delete event) {
        List<UUID> users = PermCacheServer.instance().getUsersWatching(CacheType.GLOBAL_LISTING, CategoryType.GROUP, null);
        if(!users.isEmpty()) {
            String[] data1 = APIRegistry.perms.getServerZone().getGroups().toArray(new String[0]);
            for(UUID user : users) PacketBuilder.instance().sendCacheData(user, CacheType.GLOBAL_LISTING, CategoryType.GROUP, null, data1);
        }
    }

    // TODO create event for user joining server
}
