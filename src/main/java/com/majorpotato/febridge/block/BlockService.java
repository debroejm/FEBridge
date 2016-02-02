package com.majorpotato.febridge.block;


import com.majorpotato.febridge.FEBridge;
import com.majorpotato.febridge.reference.Reference;
import com.majorpotato.febridge.tileentity.TileEntityService;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

public class BlockService extends BlockContainer {

    protected IIcon[] icons = new IIcon[2];

    public BlockService() {
        super(Material.rock);
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setBlockTextureName("blockServicePersonal");
        this.setBlockName("blockService");
        this.setHardness(2.5F);
        this.setResistance(5.0F);
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
        if(isServiceAdmin(meta)) return icons[1];
        else return icons[0];
    }

    @Override
    public TileEntity createNewTileEntity(World worldObj, int metadata) {
        if(isServiceAdmin(metadata)) return new TileEntityService(true);
        else return new TileEntityService();
    }

    @Override
    public int damageDropped(int metadata) {
        return metadata;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List subItems) {
        subItems.add(new ItemStack(item, 1, 0));
        subItems.add(new ItemStack(item, 1, 1));
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack itemStack) {
        TileEntity ent = world.getTileEntity(x, y, z);
        if(ent instanceof TileEntityService && entityliving instanceof EntityPlayer) {
            ((TileEntityService)ent).setOwner((EntityPlayer)entityliving);
            FMLNetworkHandler.openGui((EntityPlayer)entityliving, FEBridge.instance, 0, world, x, y, z);
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if(!world.isRemote) {
            TileEntity tent = world.getTileEntity(x, y, z);
            if (((TileEntityService)tent).rightClick(player)) {
                world.playSoundEffect((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, "random.click", 0.3F, 0.6F);
                return true;
            } else return false;
        }
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean canProvidePower() { return true; }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity tent = world.getTileEntity(x, y, z);
        return ((tent instanceof TileEntityService) && (((TileEntityService)tent).isActive())) ? 15 : 0;
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
    public static boolean isServiceAdmin(int meta) { return (meta & 1) != 0; }
}
