package com.majorpotato.febridge.util.cache;

import com.majorpotato.febridge.network.PacketBuilder;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.HashMap;

@SideOnly(Side.CLIENT)
public class PermCacheClient {

    private static PermCacheClient instance = new PermCacheClient();
    public static PermCacheClient instance() { return instance; }

    // ***************
    //  Internal Data
    // ***************

    @SuppressWarnings("unchecked")
    private HashMap<String, CacheData>[][] cacheDataMap = new HashMap[CacheType.values().length][CategoryType.values().length];

    @SuppressWarnings("unchecked")
    private HashMap<String, Boolean>[][] requestMap = new HashMap[CacheType.values().length][CategoryType.values().length];

    public PermCacheClient() {
        clear();
    }



    // ****************
    //  Update Methods
    // ****************

    // NOTE: Methods without the <String:name> argument are for global cache types

    public void update(CacheType cacheType, CategoryType categoryType, String name, String[] listing) {
        cacheDataMap[cacheType.ordinal()][categoryType.ordinal()].put(name, new CacheDataList(listing));
        requestMap[cacheType.ordinal()][categoryType.ordinal()].put(name, false);
    }

    public void update(CacheType cacheType, CategoryType categoryType, String name, String[] listing, String[] values) {
        cacheDataMap[cacheType.ordinal()][categoryType.ordinal()].put(name, new CacheDataPermissions(listing, values));
        requestMap[cacheType.ordinal()][categoryType.ordinal()].put(name, false);
    }

    public void clear(CacheType cacheType, CategoryType categoryType, String name) {
        cacheDataMap[cacheType.ordinal()][categoryType.ordinal()].remove(name);
        requestMap[cacheType.ordinal()][categoryType.ordinal()].remove(name);
    }

    @SuppressWarnings("unchecked")
    public void clear() {
        cacheDataMap = new HashMap[CacheType.values().length][CategoryType.values().length];
        requestMap = new HashMap[CacheType.values().length][CategoryType.values().length];
        for(int i = 0; i < CacheType.values().length; i++) {
            for(int j = 0; j < CategoryType.values().length; j++) {
                cacheDataMap[i][j] = new HashMap<String, CacheData>();
                requestMap[i][j] = new HashMap<String, Boolean>();
            }
        }
    }



    // ***************
    //  Query Methods
    // ***************

    public CacheData get(CacheType cacheType, CategoryType categoryType, String name) {
        Boolean request = requestMap[cacheType.ordinal()][categoryType.ordinal()].get(name);
        if(request == null) {
            request(cacheType, categoryType, name);
            return null;
        }
        return request ? null : cacheDataMap[cacheType.ordinal()][categoryType.ordinal()].get(name);
    }



    // *****************
    //  Request Methods
    // *****************

    public void request(CacheType cacheType, CategoryType categoryType, String name) {
        PacketBuilder.instance().requestCacheData(cacheType, categoryType, name);
        requestMap[cacheType.ordinal()][categoryType.ordinal()].put(name, true);
    }



    // *************************
    //  Auto-Completion Methods
    // *************************

    /*
    public void trimOptions(String text, ArrayList<String> possibilities) {
        for(int i = 0; i < possibilities.size(); i++) {
            String current = possibilities.get(i);
            if(current.length() < text.length() || !current.substring(0,text.length()).equals(text)) {
                possibilities.remove(i);
                i--;
            }
        }
    }
    */

    private CacheData getOptions(String filter, CacheData cache) {
        String[] options = null;
        String[] values = null;
        if(cache instanceof CacheDataList) options = ((CacheDataList)cache).list;
        else if(cache instanceof CacheDataPermissions) {
            options = ((CacheDataPermissions)cache).permissions;
            values = ((CacheDataPermissions)cache).values;
        } else return null;
        if(options == null) options = new String[0];
        ArrayList<String> validOptions = new ArrayList<String>();
        ArrayList<String> validValues = new ArrayList<String>();
        for(int i = 0; i < options.length; i++) {
            if(options[i] != null && filter != null && options[i].toLowerCase().contains(filter.toLowerCase())) {
                validOptions.add(options[i]);
                if(values != null) validValues.add(values[i]);
            }
        }
        String[] finalOptions = validOptions.toArray(new String[0]);
        String[] finalValues = validValues.toArray(new String[0]);
        if(values != null) return new CacheDataPermissions(finalOptions, finalValues);
        else return new CacheDataList(finalOptions);
    }

    public CacheData filter(CacheType cacheType, CategoryType categoryType, String name, String filter) {
        return getOptions(filter, get(cacheType, categoryType, name));
    }

    // ******************
    //  Data Chunk Class
    // ******************

    public abstract static class CacheData {

    }

    public static class CacheDataList extends CacheData {
        public CacheDataList(String[] list) { this.list = list; }
        public String[] list;
    }

    public static class CacheDataPermissions extends CacheData {
        public CacheDataPermissions(String[] permissions, String[] values) { this.permissions = permissions; this.values = values; }
        public String[] permissions;
        public String[] values;
    }

}
