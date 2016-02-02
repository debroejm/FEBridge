package com.majorpotato.febridge.client.handler;


import com.majorpotato.febridge.FEBridge;
import com.majorpotato.febridge.client.container.ContainerBasic;
import com.majorpotato.febridge.client.container.ContainerShop;
import com.majorpotato.febridge.client.gui.*;
import com.majorpotato.febridge.tileentity.ICurrencyService;
import com.majorpotato.febridge.tileentity.TileEntityShop;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {

    public GuiHandler()
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(FEBridge.instance, this);
    }

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity entity = world.getTileEntity(x, y, z);
        switch(id)
        {
            case 0:
                if(entity instanceof TileEntityShop) return new ContainerShop(player.inventory, (TileEntityShop)entity);
                else if(entity instanceof ICurrencyService) return new ContainerBasic(player.inventory, x, y, z);
                else return null;
            case 1:
                if(entity instanceof TileEntityShop) return new ContainerShop(player.inventory, (TileEntityShop)entity, true);
                else if(entity instanceof ICurrencyService) return new ContainerBasic(player.inventory);
                else return null;
            default:
                return null;
        }
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity entity = world.getTileEntity(x, y, z);
        switch(id)
        {
            case 0:
            case 1:
                if(entity instanceof TileEntityShop) return new GuiShop(player, (TileEntityShop)entity);
                else if(entity instanceof ICurrencyService) return new GuiService(player, (ICurrencyService)entity);
                else return null;
            default:
                return null;
        }
    }
}
