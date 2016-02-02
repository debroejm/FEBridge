package com.majorpotato.febridge.block.itemblock;


import com.majorpotato.febridge.block.BlockService;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;

public class ItemBlockService extends ItemBlockWithMetadata {

    public ItemBlockService(Block block) { super(block, block); }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return this.getUnlocalizedName() + ((BlockService.isServiceAdmin(stack.getItemDamage())) ? "Admin" : "Personal");
    }
}
