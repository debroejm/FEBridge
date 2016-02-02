package com.majorpotato.febridge.tileentity;


import com.majorpotato.febridge.block.BlockTollGate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityTollGate extends TileEntityService {

    protected ForgeDirection direct;

    public TileEntityTollGate() { this(0, false); }
    public TileEntityTollGate(int side) { this(side, false); }
    public TileEntityTollGate(int side, boolean adminService) {
        super(adminService);
        this.direct = ForgeDirection.getOrientation(side);
    }

    @Override
    protected void activate(EntityPlayer player) {
        int metadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);

        int j1 = (MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) % 4;
        int k1 = BlockTollGate.getDirection(metadata);

        if (k1 == (j1 + 2) % 4)
        {
            metadata = j1;
        }

        worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, metadata | 4, 2);
        worldObj.playAuxSFX(1003, xCoord, yCoord, zCoord, 0);
    }

    @Override
    protected void deactivate() {
        int metadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
        worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, metadata & -5, 2);
        worldObj.playAuxSFX(1003, xCoord, yCoord, zCoord, 0);
    }

    @Override
    public String getServiceName() { return new ItemStack(blockType, 1, adminService ? 8 : 0).getDisplayName(); }

    public ForgeDirection getDirection() { return direct; }
}
