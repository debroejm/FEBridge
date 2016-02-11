package com.majorpotato.febridge;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.misc.FECommandManager;
import com.majorpotato.febridge.client.handler.GuiHandler;
import com.majorpotato.febridge.commands.*;
import com.majorpotato.febridge.handler.*;
import com.majorpotato.febridge.init.*;
import com.majorpotato.febridge.proxy.IProxy;
import com.majorpotato.febridge.reference.Reference;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, guiFactory = Reference.GUI_FACTORY_CLASS)
public class FEBridge {

    @Mod.Instance(Reference.MOD_ID)
    public static FEBridge instance;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
    public static IProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        // Configuration, not really used except for GUI stuff....
        ConfigHandler.init(event.getSuggestedConfigurationFile());
        FMLCommonHandler.instance().bus().register(new ConfigHandler());
        proxy.registerCoinLootHandler();

        // Network Handler
        PacketHandler.init();

        // Content
        ModItems.init();
        ModBlocks.init();
        ModEntities.init();

        // Client-Side Rendering
        proxy.registerRenderThings();

    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

        // GUIs
        new GuiHandler();

        // Recipes
        ModRecipes.init();

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        proxy.registerServerLoadThings();
    }
}
