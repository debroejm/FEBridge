package com.majorpotato.febridge.init;


import com.majorpotato.febridge.FEBridge;
import com.majorpotato.febridge.entity.EntityCoin;
import cpw.mods.fml.common.registry.EntityRegistry;

public class ModEntities {

    public static void init() {
        EntityRegistry.registerModEntity(EntityCoin.class, "Coin", 0, FEBridge.instance, 160, 20, false);
    }
}
