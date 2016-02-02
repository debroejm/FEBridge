package com.majorpotato.febridge.block;


import com.majorpotato.febridge.FEBridge;
import com.majorpotato.febridge.reference.Reference;
import com.majorpotato.febridge.tileentity.TileEntityService;
import com.majorpotato.febridge.tileentity.TileEntityTollGate;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

public class BlockTollGate extends BlockService {

    public BlockTollGate() {
        super();
        this.setBlockName("blockTollGate");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons[0] = iconRegister.registerIcon(Reference.MOD_ID+":blockServicePersonal");
        icons[1] = iconRegister.registerIcon(Reference.MOD_ID+":blockServiceAdmin");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if(isTollGateAdmin(meta)) return icons[1];
        else return icons[0];
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List subItems) {
        subItems.add(new ItemStack(item, 1, 0));
        subItems.add(new ItemStack(item, 1, 8));
    }

    @Override
    public int damageDropped(int metadata) {
        if(isTollGateAdmin(metadata)) return 8;
        else return 0;
    }

    @Override
    public TileEntity createNewTileEntity(World worldObj, int metadata) {
        if(isTollGateAdmin(metadata)) return new TileEntityTollGate(getDirection(metadata), true);
        else return new TileEntityTollGate(getDirection(metadata));
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
        int meta = world.getBlockMetadata(x, y, z);
        return isTollGateOpen(meta) ? null : (meta != 2 && meta != 0 ? AxisAlignedBB.getBoundingBox((double)((float)x + 0.375F), (double)y, (double)z, (double)((float)x + 0.625F), (double)((float)y + 1.5F), (double)(z + 1)) : AxisAlignedBB.getBoundingBox((double)x, (double)y, (double)((float)z + 0.375F), (double)(x + 1), (double)((float)y + 1.5F), (double)((float)z + 0.625F)));
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
    {
        int direct = getDirection(world.getBlockMetadata(x, y, z));

        if (direct != 2 && direct != 0)
        {
            this.setBlockBounds(0.375F, 0.125F, 0.0F, 0.625F, 0.875F, 1.0F);
        }
        else
        {
            this.setBlockBounds(0.0F, 0.125F, 0.375F, 1.0F, 0.875F, 0.625F);
        }
    }

    @Override
    public boolean isOpaqueCube() { return false; }

    @Override
    public boolean renderAsNormalBlock() { return false; }

    @Override
    public int getRenderType() { return -1; }

    @Override
    public boolean getBlocksMovement(IBlockAccess world, int x, int y, int z)
    {
        return isTollGateOpen(world.getBlockMetadata(x, y, z));
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack itemStack)
    {
        int direct = (MathHelper.floor_double((double)(entityliving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) % 4;
        world.setBlockMetadataWithNotify(x, y, z, direct, 2);

        TileEntity ent = world.getTileEntity(x, y, z);
        if(ent instanceof TileEntityService && entityliving instanceof EntityPlayer) {
            ((TileEntityService)ent).setOwner((EntityPlayer)entityliving);
            FMLNetworkHandler.openGui((EntityPlayer)entityliving, FEBridge.instance, 0, world, x, y, z);
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        if(world.isRemote) return false;

        TileEntity tent = world.getTileEntity(x, y, z);
        TileEntityTollGate gate;
        if(tent instanceof TileEntityTollGate) gate = (TileEntityTollGate)tent;
        else return false;

        return gate.rightClick(player);
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side)
    {
        return true;
    }


    // Replaced these from BlockService
    @Override
    public boolean canProvidePower() { return false; }
    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) { return 0; }


    // Metadata Seperator Functions
    public static boolean isTollGateOpen(int meta) { return (meta & 4) != 0; }
    public static boolean isTollGateAdmin(int meta) { return (meta & 8) != 0; }
    public static int getDirection(int meta) { return (meta & 3); }
}
