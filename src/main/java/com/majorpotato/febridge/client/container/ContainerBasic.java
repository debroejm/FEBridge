package com.majorpotato.febridge.client.container;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerBasic extends Container {

    protected int xCoord, yCoord, zCoord;
    protected boolean infiniteRange;

    public ContainerBasic(InventoryPlayer invPlayer) { this(invPlayer, 0, 0, 0, true); }
    public ContainerBasic(InventoryPlayer invPlayer, int xCoord, int yCoord, int zCoord) { this(invPlayer, xCoord, yCoord, zCoord, false); }
    protected ContainerBasic(InventoryPlayer invPlayer, int xCoord, int yCoord, int zCoord, boolean infiniteRange) {

        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.zCoord = zCoord;
        this.infiniteRange = infiniteRange;

        // Hotbar
        for(int x = 0; x < 9; x++) {
            this.addSlotToContainer(new Slot(invPlayer,x,8+x*18,142));
        }

        // Player Inventory
        for(int y = 0; y < 3; y++) {
            for(int x = 0; x < 9; x++) {
                this.addSlotToContainer(new Slot(invPlayer, 9+x+y*9, 8+x*18, 84+y*18));
            }
        }

        // No More. There's really no good reason to use this Container in a GUI, but eh, why not.
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return infiniteRange || player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64;
    }

    // Copied from ContainerPlayer, changed to only use the general inventory (not armor or crafting)
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slodID)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(slodID);

        if (slot != null && slot.getHasStack())
        {
            ItemStack slotStack = slot.getStack();
            itemstack = slotStack.copy();

            if (slodID >= 0 && slodID < 27)
            {
                if (!this.mergeItemStack(slotStack, 27, 36, false))
                {
                    return null;
                }
            }
            else if (slodID >= 27 && slodID < 36)
            {
                if (!this.mergeItemStack(slotStack, 0, 27, false))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(slotStack, 0, 27, false))
            {
                return null;
            }

            if (slotStack.stackSize == 0)
            {
                slot.putStack(null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (slotStack.stackSize == itemstack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(player, slotStack);
        }

        return itemstack;
    }
}
