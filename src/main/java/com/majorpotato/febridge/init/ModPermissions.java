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
            boolean valid = true;
            int bounty = 0;

            if(object instanceof Class) { // It Should be
                Class entityClass = (Class)object;
                try {
                    Object entity = entityClass.getConstructor(World.class).newInstance(MinecraftServer.getServer().worldServerForDimension(0));

                    if(entity instanceof IBossDisplayData) {
                        bounty = 500;
                    } else if(entity instanceof IMob) {
                        bounty = 25;
                    } else if(entity instanceof IAnimals) {
                        bounty = 5;
                    } else valid = false;

                    if(valid) {
                        Object strObj = EntityList.classToStringMapping.get(object);
                        String entityName;
                        if(strObj instanceof String) entityName = (String)strObj; else continue;
                        APIRegistry.perms.registerPermissionProperty(PERM_BOUNTY+"."+entityName, bounty+"");
                    }
                } catch(Exception ex) {
                    // Do nothing
                }
            }
        }
    }
}
