package com.majorpotato.febridge.block;

import com.majorpotato.febridge.FEBridge;
import com.majorpotato.febridge.reference.Reference;
import com.majorpotato.febridge.tileentity.TileEntityShop;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

import static net.minecraftforge.common.util.ForgeDirection.*;
import static net.minecraftforge.common.util.ForgeDirection.EAST;

public class BlockShop extends BlockContainer {

    public BlockShop() {
        super(Material.rock);
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setBlockTextureName("blockServicePersonal");
        this.setBlockName("blockShop");
        this.setHardness(2.5F);
        this.setResistance(5.0F);
    }

    @Override
    public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side) {
        ForgeDirection dir = ForgeDirection.getOrientation(side);
        return (dir == DOWN  && world.isSideSolid(x, y + 1, z, DOWN )) ||
                (dir == UP    && world.isSideSolid(x, y - 1, z, UP   )) ||
                (dir == NORTH && world.isSideSolid(x, y, z + 1, NORTH)) ||
                (dir == SOUTH && world.isSideSolid(x, y, z - 1, SOUTH)) ||
                (dir == WEST  && world.isSideSolid(x + 1, y, z, WEST )) ||
                (dir == EAST  && world.isSideSolid(x - 1, y, z, EAST ));
    }

    @Override
    public TileEntity createNewTileEntity(World worldObj, int metadata) {
        if(isShopAdmin(metadata)) return new TileEntityShop(getDirection(metadata), true);
        else return new TileEntityShop(metadata);
    }

    @Override
    public int damageDropped(int metadata) {
        if(isShopAdmin(metadata)) return 8;
        else return 0;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List subItems) {
        subItems.add(new ItemStack(item, 1, 0));
        subItems.add(new ItemStack(item, 1, 8));
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack itemStack) {
        TileEntity ent = world.getTileEntity(x, y, z);
        if(ent instanceof TileEntityShop && entityliving instanceof EntityPlayer) {
            ((TileEntityShop)ent).setOwner((EntityPlayer)entityliving);
            FMLNetworkHandler.openGui((EntityPlayer)entityliving, FEBridge.instance, 0, world, x, y, z);
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if(!world.isRemote) {
            TileEntity tent = world.getTileEntity(x, y, z);
            return ((TileEntityShop)tent).rightClick(player);
        }
        return false;
    }

    @Override
    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
        if(!world.isRemote) {
            TileEntity tent = world.getTileEntity(x, y, z);
            ((TileEntityShop)tent).leftClick(player);
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        if(world.isRemote) return;
        TileEntity tent = world.getTileEntity(x, y, z);
        ((TileEntityShop)tent).dropStacks();
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        int metadata = world.getBlockMetadata(x, y, z);
        int direct = getDirection(metadata);
        ForgeDirection direction = ForgeDirection.getOrientation(direct).getOpposite();

        if(!world.isSideSolid(x+direction.offsetX, y+direction.offsetY, z+direction.offsetZ, direction.getOpposite())) {
            dropBlockAsItem(world, x, y, z, metadata, 0);
            world.setBlockToAir(x, y, z);
        }
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public String getUnlocalizedName()
    {
        return String.format("tile.%s%s", Reference.MOD_ID.toLowerCase() + ":", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }

    protected String getUnwrappedUnlocalizedName(String unlocalizedName)
    {
        return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
    }

    // Metadata Seperator Functions
    public static boolean isShopAdmin(int meta) { return (meta & 8) != 0; }
    public static int getDirection(int meta) {
        if(isShopAdmin(meta)) return meta-8;
        else return meta;
    }
}
