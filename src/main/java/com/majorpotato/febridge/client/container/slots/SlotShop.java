package com.majorpotato.febridge.client.container.slots;


import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotShop extends Slot {

    public SlotShop(IInventory inv, int slot, int x, int y) {
        super(inv, slot, x, y);
    }

    @Override
    public void putStack(ItemStack stack) {
        this.onSlotChanged();
    }

}
