package com.majorpotato.febridge.util.cache;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@SideOnly(Side.SERVER)
public class PermCacheServer {

    private static PermCacheServer instance = new PermCacheServer();
    public static PermCacheServer instance() { return instance; }



    // ***************
    //  Internal Data
    // ***************

    private HashMap<UUID, UserCache> userCacheMap = new HashMap<UUID, UserCache>();
    @SuppressWarnings("unchecked")
    private HashMap<String, List<UUID>>[][] userListMap = new HashMap[CacheType.values().length][CategoryType.values().length];

    public PermCacheServer() {
        for(int i = 0; i < CacheType.values().length; i++) {
            for(int j = 0; j < CategoryType.values().length; j++) {
                userListMap[i][j] = new HashMap<String, List<UUID>>();
            }
        }
    }



    // ************************
    //  Watcher Getter Methods
    // ************************

    // NOTE: Methods without the <String:name> argument are for global cache types

    public boolean isWatching(UUID id, CacheType cacheType, CategoryType categoryType) { return isWatching(id, cacheType, categoryType, null); }
    public boolean isWatching(UUID id, CacheType cacheType, CategoryType categoryType, String name) {
        UserCache cache = userCacheMap.get(id);
        return cache != null && cache.isWatching(cacheType, categoryType, name);
    }

    @Nonnull
    public List<UUID> getUsersWatching(CacheType cacheType, CategoryType categoryType) { return getUsersWatching(cacheType, categoryType, null); }
    @Nonnull
    public List<UUID> getUsersWatching(CacheType cacheType, CategoryType categoryType, String name) {
        List<UUID> list = userListMap[cacheType.ordinal()][categoryType.ordinal()].get(name);
        if(list == null) {
            list = new ArrayList<UUID>();
            userListMap[cacheType.ordinal()][categoryType.ordinal()].put(name, list);
        }
        return list;
    }



    // ************************
    //  Watcher Setter Methods
    // ************************

    // NOTE: Methods without the <String:name> argument are for global cache types

    public void userIsWatching(UUID id, CacheType cacheType, CategoryType categoryType) { userIsWatching(id, cacheType, categoryType, null); }
    public void userIsWatching(UUID id, CacheType cacheType, CategoryType categoryType, String name) {
        if(!userCacheMap.containsKey(id)) userCacheMap.put(id, new UserCache());
        userCacheMap.get(id).setWatching(true, cacheType, categoryType, name);
        List<UUID> list = userListMap[cacheType.ordinal()][categoryType.ordinal()].get(name);
        if(list == null) {
            list = new ArrayList<UUID>();
            userListMap[cacheType.ordinal()][categoryType.ordinal()].put(name, list);
        }
        if(!list.contains(id)) list.add(id);
    }

    public void clearWatching(UUID id) {
        if(!userCacheMap.containsKey(id)) userCacheMap.put(id, new UserCache());
        userCacheMap.get(id).clearWatching();
        for(int i = 0; i < CacheType.values().length; i++) {
            for(int j = 0; j < CategoryType.values().length; j++) {
                HashMap<String, List<UUID>> listMap = userListMap[i][j];
                for(List<UUID> list : listMap.values()) {
                    list.remove(id);
                }
            }
        }
    }



    // *********************
    //  Data Sender Methods
    // *********************





    // ******************
    //  Data Chunk Class
    // ******************

    private static class UserCache {

        @SuppressWarnings("unchecked")
        private HashMap<String, Boolean>[][] watching = new HashMap[CacheType.values().length][CategoryType.values().length];

        public UserCache() {
            for(int i = 0; i < CacheType.values().length; i++) {
                for(int j = 0; j < CategoryType.values().length; j++) {
                    watching[i][j] = new HashMap<String, Boolean>();
                }
            }
        }

        public boolean isWatching(CacheType cacheType, CategoryType categoryType, String name) {
            Boolean result = watching[cacheType.ordinal()][categoryType.ordinal()].get(name);
            return result != null && result;
        }

        public void setWatching(boolean value, CacheType cacheType, CategoryType categoryType, String name) {
            watching[cacheType.ordinal()][categoryType.ordinal()].put(name, value);
        }

        @SuppressWarnings("unchecked")
        public void clearWatching() {
            watching = new HashMap[CacheType.values().length][CategoryType.values().length];
            for(int i = 0; i < CacheType.values().length; i++) {
                for(int j = 0; j < CategoryType.values().length; j++) {
                    watching[i][j] = new HashMap<String, Boolean>();
                }
            }
        }
    }

}
