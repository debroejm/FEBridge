package com.majorpotato.febridge.block.itemblock;


import com.majorpotato.febridge.block.BlockTollGate;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemBlockTollGate extends ItemBlockWithMetadata {

    public ItemBlockTollGate(Block block) { super(block, block); }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return this.getUnlocalizedName() + ((BlockTollGate.isTollGateAdmin(stack.getItemDamage())) ? "Admin" : "Personal");
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
        int direct = (MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) % 4;
        return super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, BlockTollGate.isTollGateAdmin(metadata) ? direct | 8 : direct);
    }
}
