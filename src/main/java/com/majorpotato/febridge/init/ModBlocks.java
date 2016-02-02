package com.majorpotato.febridge.init;

import com.majorpotato.febridge.block.*;
import com.majorpotato.febridge.block.itemblock.ItemBlockService;
import com.majorpotato.febridge.block.itemblock.ItemBlockShop;
import com.majorpotato.febridge.block.itemblock.ItemBlockTollGate;
import com.majorpotato.febridge.reference.Reference;
import com.majorpotato.febridge.tileentity.TileEntityService;
import com.majorpotato.febridge.tileentity.TileEntityShop;
import com.majorpotato.febridge.tileentity.TileEntityTollGate;
import cpw.mods.fml.common.registry.GameRegistry;

@GameRegistry.ObjectHolder(Reference.MOD_ID)
public class ModBlocks {
    //public static final BlockGM blockGuiTest = new BlockGuiTest();
    public static final BlockShop blockShop = new BlockShop();
    public static final BlockService blockService = new BlockService();
    public static final BlockTollGate blockTollGate = new BlockTollGate();

    public static void init() {

        //GameRegistry.registerBlock(blockGuiTest, "blockGuiTest");
        GameRegistry.registerBlock(blockShop, ItemBlockShop.class, "blockShop");
        GameRegistry.registerBlock(blockService, ItemBlockService.class, "blockService");
        GameRegistry.registerBlock(blockTollGate, ItemBlockTollGate.class, "blockTollGate");

        GameRegistry.registerTileEntity(TileEntityShop.class, "TileEntityShop");
        GameRegistry.registerTileEntity(TileEntityService.class, "TileEntityService");
        GameRegistry.registerTileEntity(TileEntityTollGate.class, "TileEntityTollGate");

    }
}
