package com.majorpotato.febridge.block.itemblock;


import com.majorpotato.febridge.block.BlockShop;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemBlockShop extends ItemBlockWithMetadata {

    public ItemBlockShop(Block block) {
        super(block, block);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return this.getUnlocalizedName() + (BlockShop.isShopAdmin(stack.getItemDamage()) ? "Admin" : "Personal");
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
        int newMeta = side;
        if(BlockShop.isShopAdmin(metadata)) newMeta = 8+side;
        return super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, newMeta);
    }
}
