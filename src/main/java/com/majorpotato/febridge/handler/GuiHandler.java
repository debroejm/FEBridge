package com.majorpotato.febridge.handler;


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

    public static final int GUI_SHOP = 0;
    public static final int GUI_SHOP_UNBOUND = 1;
    public static final int GUI_PERM_EDITOR = 2;

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
            case GUI_SHOP:
                if(entity instanceof TileEntityShop) return new ContainerShop(player.inventory, (TileEntityShop)entity);
                else if(entity instanceof ICurrencyService) return new ContainerBasic(player.inventory, x, y, z);
                else return null;
            case GUI_SHOP_UNBOUND:
                if(entity instanceof TileEntityShop) return new ContainerShop(player.inventory, (TileEntityShop)entity, true);
                else if(entity instanceof ICurrencyService) return new ContainerBasic(player.inventory);
                else return null;
            case GUI_PERM_EDITOR:
                return null;
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
            case GUI_SHOP:
            case GUI_SHOP_UNBOUND:
                if(entity instanceof TileEntityShop) return new GuiShop(player, (TileEntityShop)entity);
                else if(entity instanceof ICurrencyService) return new GuiService(player, (ICurrencyService)entity);
                else return null;
            case GUI_PERM_EDITOR:
                return new GuiPermEditor(player);
            default:
                return null;
        }
    }
}
