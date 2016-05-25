package com.majorpotato.febridge.proxy;

import com.forgeessentials.core.misc.FECommandManager;
import com.majorpotato.febridge.FEBridge;
import com.majorpotato.febridge.commands.CommandCoinLoot;
import com.majorpotato.febridge.commands.CommandPermEditor;
import com.majorpotato.febridge.commands.CommandService;
import com.majorpotato.febridge.handler.CoinLootHandler;
import com.majorpotato.febridge.init.ModPermissions;
import net.minecraftforge.common.MinecraftForge;

public class ServerProxy extends CommonProxy {

    @Override
    public void registerRenderThings() {
        // NOOP
    }

    @Override
    public void registerCoinLootHandler() {
        MinecraftForge.EVENT_BUS.register(new CoinLootHandler());
    }

    @Override
    public void registerServerLoadThings() {
        // Commands
        FECommandManager.registerCommand(new CommandService());
        FECommandManager.registerCommand(new CommandCoinLoot());
        FECommandManager.registerCommand(new CommandPermEditor());

        // Permission Nodes
        ModPermissions.init();

    }
}
