package com.majorpotato.febridge.init;


import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.economy.ModuleEconomy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.permission.PermissionLevel;

@SideOnly(Side.SERVER)
public class ModPermissions {

    public static final String PERM_SERVICES = ModuleEconomy.PERM + ".services";
    public static final String PERM_SERVICES_ADMIN = PERM_SERVICES + ".admin";
    public static final String PERM_SERVICES_BUY = PERM_SERVICES + ".buy";
    public static final String PERM_SERVICES_SELL = PERM_SERVICES + ".sell";
    public static final String PERM_BOUNTY = ModuleEconomy.PERM + ".bounty";

    public static void init() {
        registerServiceNodes();
        registerEntityNodes();
    }

    private static void registerServiceNodes() {
        APIRegistry.perms.registerPermission(PERM_SERVICES_ADMIN, PermissionLevel.FALSE, "Modifying Admin and other Player Services");
        APIRegistry.perms.registerPermission(PERM_SERVICES_BUY, PermissionLevel.TRUE, "Buying from Services");
        APIRegistry.perms.registerPermission(PERM_SERVICES_SELL, PermissionLevel.TRUE, "Selling to Services");
    }

    // In all honesty, this should probably be in the core FE
    private static void registerEntityNodes() {
        for(Object object : EntityList.classToStringMapping.keySet()) {
            if(object instanceof Class) { // It Should be
                Class entityClass = (Class)object;

                int bounty = 0;
                if(IBossDisplayData.class.isAssignableFrom(entityClass)) {
                    bounty = 1000;
                } else if(IMob.class.isAssignableFrom(entityClass)) {
                    bounty = 25;
                } else if(IAnimals.class.isAssignableFrom(entityClass)) {
                    bounty = 5;
                } else continue;

                Object strObj = EntityList.classToStringMapping.get(object);
                if(strObj instanceof String) APIRegistry.perms.registerPermissionProperty(PERM_BOUNTY+"."+((String)strObj), bounty+"");
            }
        }
    }
}
