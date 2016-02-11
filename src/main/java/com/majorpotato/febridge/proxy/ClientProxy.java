package com.majorpotato.febridge.proxy;

import com.majorpotato.febridge.entity.EntityCoin;
import com.majorpotato.febridge.init.ModBlocks;
import com.majorpotato.febridge.rendering.GexItemRenderer;
import com.majorpotato.febridge.rendering.RenderCoin;
import com.majorpotato.febridge.rendering.ServiceRenderer;
import com.majorpotato.febridge.tileentity.TileEntityService;
import com.majorpotato.febridge.tileentity.TileEntityShop;
import com.majorpotato.febridge.tileentity.TileEntityTollGate;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy {

    @Override
    public void registerRenderThings() {

        TileEntitySpecialRenderer renderService = new ServiceRenderer();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityShop.class, renderService);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityService.class, renderService);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTollGate.class, renderService);

        IItemRenderer renderServiceItems = new GexItemRenderer();
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.blockShop), renderServiceItems);
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.blockTollGate), renderServiceItems);

        RenderingRegistry.registerEntityRenderingHandler(EntityCoin.class, new RenderCoin());
    }

    @Override
    public void registerCoinLootHandler() {
        // NOOP
    }

    @Override
    public void registerServerLoadThings() {
        // NOOP
    }
}
