package com.majorpotato.febridge.client.container;

import com.majorpotato.febridge.client.container.slots.SlotShop;
import com.majorpotato.febridge.client.gui.GuiShop;
import com.majorpotato.febridge.tileentity.TileEntityShop;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerShop extends Container {

    protected TileEntityShop shop;
    protected boolean infiniteRange;

    public ContainerShop(InventoryPlayer invPlayer, TileEntityShop shop) { this(invPlayer, shop, false); }
    public ContainerShop(InventoryPlayer invPlayer, TileEntityShop shop, boolean infiniteRange) {

        this.shop = shop;
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

        // Shop Slot
        this.addSlotToContainer(new SlotShop(shop, 0, GuiShop.SLOT_LEFT+1, GuiShop.SLOT_TOP+1));
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return infiniteRange || shop.isUseableByPlayer(player);
    }


    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {

        Slot slot = getSlot(slotID);
        if(slot != null) {
            if(slotID == 36) {
                if(!shop.isAdminService() && shop.getItemTemplate() != null) {
                    int amount = shop.getItemTemplate().getMaxStackSize();
                    if(amount > shop.getItemCount()) amount = shop.getItemCount();
                    if(amount > 0) {
                        ItemStack stack = shop.getItemTemplate().copy();
                        stack.stackSize = amount;
                        if(player.inventory.addItemStackToInventory(stack)) shop.setItemCount(shop.getItemCount()-amount);
                    }
                    slot.onSlotChanged();
                    shop.markDirty();
                }
            } else if(slotID < 36 && slot.getHasStack()) {
                if(!shop.isAdminService()) {
                    if(shop.isItemValidForSlot(0, slot.getStack())) {
                        shop.setInventorySlotContents(0, slot.getStack());
                        if(slot.getStack().stackSize < 1) slot.putStack(null);
                        slot.onSlotChanged();
                        shop.markDirty();
                    }
                } else {
                    shop.setInventorySlotContents(0, slot.getStack()); // Will just set the shop Item Type to that of the stack, without changing it.
                    shop.markDirty();
                }
            }
        }

        return null;
    }

    @Override
    public ItemStack slotClick(int slotID, int mouseMod, int actionMod, EntityPlayer player)
    {
        if(slotID == 36) { // Our special slot
            ItemStack result = null;
            Slot slot = (Slot)this.inventorySlots.get(slotID);
            if(slot != null) {
                ItemStack slotStack = slot.getStack();
                ItemStack mouseStack = player.inventory.getItemStack();

                if(slotStack != null) result = slotStack.copy();

                if(slotStack == null) { // Shop Item Not Set
                    if(mouseStack != null && slot.isItemValid(mouseStack)) {
                        if(mouseMod == 1) {
                            ItemStack single = mouseStack.copy();
                            single.stackSize = 1;
                            if(!shop.isAdminService()) mouseStack.stackSize--;
                            if (mouseStack == null || mouseStack.stackSize == 0) player.inventory.setItemStack(null);
                            shop.setInventorySlotContents(0, single);
                        } else {
                            shop.setInventorySlotContents(0, mouseStack);
                            if(!((actionMod == 3 && player.capabilities.isCreativeMode) || shop.isAdminService())) player.inventory.setItemStack(null);
                        }
                        slot.onSlotChanged();
                    }
                } else if(slot.canTakeStack(player)) {
                    if(mouseStack == null) {// Nothing Held
                        if(mouseMod == 1) player.inventory.setItemStack(slot.decrStackSize(Math.max(shop.getItemTemplate().getMaxStackSize()/2,1))); // Attempt to grab half stack
                        else if(actionMod == 3 && player.capabilities.isCreativeMode) {
                            ItemStack full = shop.getItemTemplate().copy();
                            full.stackSize = full.getMaxStackSize();
                            player.inventory.setItemStack(full);
                        } else player.inventory.setItemStack(slot.decrStackSize(64)); // Attempt to grab maximum amount
                        slot.onPickupFromSlot(player, player.inventory.getItemStack());
                    } else if(slot.isItemValid(mouseStack)) { // Attempt to Merge. Shop Code can handle this.
                        if(mouseMod == 1) { // Right-Click
                            ItemStack single = mouseStack.copy();
                            single.stackSize = 1;
                            shop.setInventorySlotContents(0, single);
                            if((single == null || single.stackSize == 0) && !(actionMod == 3 && player.capabilities.isCreativeMode)) mouseStack.stackSize--;
                            if (mouseStack == null || mouseStack.stackSize == 0) player.inventory.setItemStack(null);
                        } else {
                            if(actionMod == 3 && player.capabilities.isCreativeMode) {
                                ItemStack dummy = mouseStack.copy();
                                shop.setInventorySlotContents(0, dummy);
                            } else if(!shop.isItemValidForSlot(0, mouseStack)) { // Attempt to swap the held stack and the shop stack, if possible
                                ItemStack shopStack = shop.getItemTemplate().copy();
                                if(shop.getItemCount() <= shopStack.getMaxStackSize()) {
                                    shopStack.stackSize = shop.getItemCount();
                                    shop.setItemType(mouseStack);
                                    shop.setItemCount(mouseStack.stackSize);
                                    mouseStack = shopStack;
                                }
                            } else shop.setInventorySlotContents(0, mouseStack);
                            if (mouseStack == null || mouseStack.stackSize == 0) player.inventory.setItemStack(null);
                            else player.inventory.setItemStack(mouseStack);
                        }
                        slot.onSlotChanged();
                    }
                }

            }
            return result;
        } else return super.slotClick(slotID, mouseMod, actionMod, player);
    }

    @Override
    public boolean canDragIntoSlot(Slot slot) {
        return !(slot instanceof SlotShop);
    }
}
