package com.majorpotato.febridge.tileentity;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.economy.Wallet;
import com.majorpotato.febridge.FEBridge;
import com.majorpotato.febridge.init.ModPermissions;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.UUID;

public class TileEntityShop extends TileEntity implements IInventory, ICurrencyService {

    // Determine Item Specifics
    private ItemStack itemTemplate;
    private int itemCount;

    // Who owns this Shop?
    private UUID owner;

    // Shop Information
    private int buyPrice;
    private int sellPrice;

    // Is this shop and Admin Shop (not player owned)?
    private boolean adminShop;

    // Which direction this shop is attached to
    private ForgeDirection direction;

    public TileEntityShop() { this(0, false); }
    public TileEntityShop(int side) { this(side, false); }
    public TileEntityShop(int side, boolean adminShop) {
        itemTemplate = null;
        itemCount = 0;
        buyPrice = 10;
        sellPrice = 10;
        owner = null;
        this.adminShop = adminShop;
        this.direction = ForgeDirection.getOrientation(side);
    }

    public void setItemType(ItemStack template) {
        if(template == null) {
            itemTemplate = null;
            itemCount = 0;
        } else {
            itemTemplate = template.copy();
            itemTemplate.stackSize = 1;
        }
    }

    @Override
    public boolean leftClick(EntityPlayer player) {
        if(worldObj.isRemote) return false;

        if(!APIRegistry.perms.checkPermission(player, ModPermissions.PERM_SERVICES_BUY)) {
            player.addChatMessage(new ChatComponentText("§e§oYou don't have permission to buy from Services."));
            return true;
        }

        if(owner == null) setOwner(player);
        if(itemTemplate == null || (itemCount < 1 && !adminShop)) return false;

        // ~--- OWNER ---~
        if(!adminShop && owner.equals(player.getPersistentID())) {
            return false;
        }
        // ~--- BUYER ---~
        else {
            if(buyPrice < 1) return false;

            Wallet playerWallet = APIRegistry.economy.getWallet(UserIdent.get(player));
            if(playerWallet == null) return false;

            // ~--- ADMIN SHOP ---~
            if(adminShop) {
                // ~--- SINGLE WITHDRAW ---~
                if(player.isSneaking()) {
                    if(playerWallet.covers(buyPrice)) {
                        ItemStack result = itemTemplate.copy();
                        result.stackSize = 1;
                        if(player.inventory.addItemStackToInventory(result)) {
                            playerWallet.withdraw(buyPrice);
                            player.addChatMessage(new ChatComponentText("§e§oYou have paid " + buyPrice + " " + APIRegistry.economy.currency(2)+" ("+playerWallet.get()+" Remaining)"));
                        }
                    } else {
                        player.addChatMessage(new ChatComponentText("§e§oYou don't have enough "+APIRegistry.economy.currency(2)+" to Pay For this Item."));
                        return true;
                    }
                }
                // ~--- ALL WITHDRAW ---~
                else {
                    if(playerWallet.covers(buyPrice * itemTemplate.getMaxStackSize())) {
                        ItemStack result = itemTemplate.copy();
                        result.stackSize = itemTemplate.getMaxStackSize();
                        if (player.inventory.addItemStackToInventory(result)) {
                            playerWallet.withdraw(buyPrice * itemTemplate.getMaxStackSize());
                            player.addChatMessage(new ChatComponentText("§e§oYou have paid " + (buyPrice * itemTemplate.getMaxStackSize()) + " " + APIRegistry.economy.currency(2)+" ("+playerWallet.get()+" Remaining)"));
                        }
                    } else {
                        player.addChatMessage(new ChatComponentText("§e§oYou don't have enough "+APIRegistry.economy.currency(2)+" to Pay For "+itemTemplate.getMaxStackSize()+" of this Item."));
                        return true;
                    }
                }
            }
            // ~--- PLAYER SHOP ---~
            else {
                Wallet ownerWallet = APIRegistry.economy.getWallet(UserIdent.get(owner));
                if(ownerWallet == null) return false;

                // ~--- SINGLE WITHDRAW ---~
                if(player.isSneaking()) {
                    if(playerWallet.covers(buyPrice)) {
                        ItemStack result = itemTemplate.copy();
                        result.stackSize = 1;
                        if(player.inventory.addItemStackToInventory(result)) {
                            playerWallet.withdraw(buyPrice);
                            player.addChatMessage(new ChatComponentText("§e§oYou have paid " + buyPrice + " " + APIRegistry.economy.currency(2)+" ("+playerWallet.get()+" Remaining)"));
                            ownerWallet.add(buyPrice);
                            itemCount--;
                        }
                    } else {
                        player.addChatMessage(new ChatComponentText("§e§oYou don't have enough "+APIRegistry.economy.currency(2)+" to Pay For this Item."));
                        return true;
                    }
                }
                // ~--- ALL WITHDRAW ---~
                else {
                    int withdrawAmount = itemTemplate.getMaxStackSize();
                    if(withdrawAmount > itemCount) withdrawAmount = itemCount;
                    ItemStack result = itemTemplate.copy();
                    result.stackSize = withdrawAmount;

                    if(playerWallet.covers(buyPrice * withdrawAmount)) {
                        if (player.inventory.addItemStackToInventory(result)) {
                            playerWallet.withdraw(buyPrice * withdrawAmount);
                            player.addChatMessage(new ChatComponentText("§e§oYou have paid " + (buyPrice * itemTemplate.getMaxStackSize()) + " " + APIRegistry.economy.currency(2)+" ("+playerWallet.get()+" Remaining)"));
                            ownerWallet.add(buyPrice * withdrawAmount);
                            itemCount -= withdrawAmount;
                        }
                    } else {
                        player.addChatMessage(new ChatComponentText("§e§oYou don't have enough "+APIRegistry.economy.currency(2)+" to Pay For "+itemTemplate.getMaxStackSize()+" of this Item."));
                        return true;
                    }
                }
            }
            markDirty();
            return true;
        }
    }

    @Override
    public boolean rightClick(EntityPlayer player) {
        if(worldObj.isRemote) return false;

        if(!APIRegistry.perms.checkPermission(player, ModPermissions.PERM_SERVICES_SELL)) {
            player.addChatMessage(new ChatComponentText("§e§oYou don't have permission to sell to Services."));
            return true;
        }

        if(owner == null) setOwner(player);
        ItemStack depositItem = player.getHeldItem();
        if(!adminShop && owner.equals(player.getPersistentID())) {
            FMLNetworkHandler.openGui(player, FEBridge.instance, 0, worldObj, xCoord, yCoord, zCoord);
            return true;
        }
        if(depositItem == null) return false;
        // ~--- SELLER ---~
        else {
            if(itemTemplate == null) return false;
            if(sellPrice < 1) return false;

            // Must be the same type for depositing
            if(!(depositItem.isItemEqual(itemTemplate) && ItemStack.areItemStackTagsEqual(depositItem, itemTemplate))) return false;

            Wallet playerWallet = APIRegistry.economy.getWallet(UserIdent.get(player));

            // ~--- ADMIN SHOP ---~
            if(adminShop) {
                // ~--- SINGLE DEPOSIT ---~
                if(player.isSneaking()) {
                    if(depositItem.stackSize > 0) {
                        depositItem.stackSize--;
                        playerWallet.add(sellPrice);
                        player.addChatMessage(new ChatComponentText("§e§oYou have been paid " + sellPrice + " " + APIRegistry.economy.currency(2)+" (Balance: "+playerWallet.get()+")"));
                    } else return false; // <- Should Never Happen
                }
                // ~--- ALL DEPOSIT ---~
                else {
                    playerWallet.add(sellPrice*depositItem.stackSize);
                    player.addChatMessage(new ChatComponentText("§e§oYou have been paid " + (sellPrice*depositItem.stackSize) + " " + APIRegistry.economy.currency(2)+" (Balance: "+playerWallet.get()+")"));
                    depositItem.stackSize = 0;
                }
            }
            // ~--- PLAYER SHOP ---~
            else {
                UserIdent ownerIdent = UserIdent.get(owner);
                if(ownerIdent == null) return false;
                Wallet ownerWallet = APIRegistry.economy.getWallet(ownerIdent);
                if(ownerWallet == null) return false; // Probably shouldn't happen, but doesn't hurt to be safe

                // ~--- SINGLE DEPOSIT ---~
                if(player.isSneaking()) {
                    if(ownerWallet.covers(sellPrice)) {
                        if (depositItem.stackSize > 0) {
                            playerWallet.add(sellPrice);
                            player.addChatMessage(new ChatComponentText("§e§oYou have been paid " + sellPrice + " " + APIRegistry.economy.currency(2)+" (Balance: "+playerWallet.get()+")"));
                            ownerWallet.withdraw(sellPrice);
                            itemCount++;
                            depositItem.stackSize--;
                        } else return false;
                    } else {
                        player.addChatMessage(new ChatComponentText("§e§oThe Owner of this Shop Doesn't have enough "+APIRegistry.economy.currency(2)+" to Pay You."));
                        return true;
                    }
                }
                // ~--- ALL DEPOSIT ---~
                else {
                    if(ownerWallet.covers(sellPrice*depositItem.stackSize)) {
                        playerWallet.add(sellPrice * depositItem.stackSize);
                        player.addChatMessage(new ChatComponentText("§e§oYou have been paid " + (sellPrice*depositItem.stackSize) + " " + APIRegistry.economy.currency(2)+" (Balance: "+playerWallet.get()+")"));
                        ownerWallet.withdraw(sellPrice * depositItem.stackSize);
                        itemCount += depositItem.stackSize;
                        depositItem.stackSize = 0;
                    } else {
                        player.addChatMessage(new ChatComponentText("§e§oThe Owner of this Shop Doesn't have enough "+APIRegistry.economy.currency(2)+" to Pay You For "+depositItem.stackSize+" of this Item."));
                        return true;
                    }
                }
            }

            return true;
        }
    }

    public void dropStacks() {
        while(itemCount > 0) {
            ItemStack dropStack;
            if(itemCount >= itemTemplate.getMaxStackSize()) {
                dropStack = itemTemplate.copy();
                dropStack.stackSize = dropStack.getMaxStackSize();
                itemCount -= dropStack.getMaxStackSize();
            } else {
                dropStack = itemTemplate.copy();
                dropStack.stackSize = itemCount;
                itemCount = 0;
            }

            EntityItem dropItems = new EntityItem(worldObj, xCoord + Math.random() - 0.5f, yCoord + Math.random() - 0.5f, zCoord + Math.random() - 0.5f, dropStack);
            worldObj.spawnEntityInWorld(dropItems);
        }
    }

    public boolean isEmpty() { return (itemCount < 0); }

    public boolean isItemSet() { return itemTemplate != null; }
    public ItemStack getItemTemplate() { return itemTemplate; }
    public int getItemCount() { return itemCount; }
    public void setItemCount(int amount) { itemCount = amount; }

    @Override
    public int getBuyPrice() { return buyPrice; }
    @Override
    public void setBuyPrice(int price) { buyPrice = price; }
    @Override
    public int getSellPrice() { return sellPrice; }
    @Override
    public void setSellPrice(int price) { sellPrice = price; }
    @Override
    public boolean useBuyPrice() { return true; }
    @Override
    public boolean useSellPrice() { return true; }
    @Override
    public boolean isAdminService() { return adminShop; }
    @Override
    public UUID getOwner() { return owner; }
    @Override
    public void setOwner(EntityPlayer player) { setOwner(player.getPersistentID()); }
    @Override
    public void setOwner(UUID uuid) {
        if(!adminShop) owner = uuid;
        else owner = null;
    }
    @Override
    public String getServiceName() { return new ItemStack(blockType, 1, adminShop ? 8 : 0).getDisplayName(); }
    @Override
    public int getXCoord() { return xCoord; }
    @Override
    public int getYCoord() { return yCoord; }
    @Override
    public int getZCoord() { return zCoord; }

    public ForgeDirection getDirection() { return direction; }



    // ==============
    // | IInventory |
    // ==============

    @Override
    public int getSizeInventory() { return 1; }

    @Override
    public ItemStack getStackInSlot(int i) {
        if(itemTemplate == null) return null;
        return itemTemplate.copy();
        /*
        if(itemTemplate == null) return null;
        int amount = itemTemplate.getMaxStackSize();
        if(itemCount < amount) amount = itemCount;
        if(amount == 0) amount = 1;
        ItemStack result = itemTemplate.copy();
        result.stackSize = amount;
        return result;
        */
    }

    @Override
    public ItemStack decrStackSize(int slot, int count) {
        if(itemCount < 1 || itemTemplate == null || adminShop) return null;

        int amount = itemTemplate.getMaxStackSize();
        if(itemCount < amount) amount = itemCount;
        ItemStack result = itemTemplate.copy();
        result.stackSize = amount;

        if(result.stackSize <= count) {
            itemCount -= result.stackSize;
            return result;
        } else {
            itemCount -= count;
            result.stackSize = count;
            return result;
        }
    }

    // Not sure if this will mess things up
    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        ItemStack itemstack = getStackInSlot(slot);
        if(itemstack != null)
        {
            setInventorySlotContents(slot, null);
        }
        return itemstack;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        //Thread.dumpStack();
        if(stack == null) return;
        if(adminShop) {
            setItemType(stack);
            itemCount = 0;
        } else {
            if (itemTemplate == null) {
                setItemType(stack);
                itemCount = stack.stackSize;
                stack.stackSize = 0;
            } else if (itemTemplate.isItemEqual(stack) && itemTemplate.getItemDamage() == stack.getItemDamage() && ItemStack.areItemStackTagsEqual(itemTemplate, stack)) {
                itemCount += stack.stackSize;
                stack.stackSize = 0;
            }
        }
    }

    @Override
    public int getInventoryStackLimit() { return 64; }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return itemTemplate == null || adminShop || (itemTemplate.isItemEqual(stack) && itemTemplate.getItemDamage() == stack.getItemDamage() && ItemStack.areItemStackTagsEqual(itemTemplate, stack));
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    @Override
    public String getInventoryName() {
        return getServiceName();
    }

    @Override
    public boolean hasCustomInventoryName() { return true; }

    @Override
    public void markDirty() {
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64;
    }



    // =======================
    // | Data Saving/Syncing |
    // =======================

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        compound.setBoolean("shopAdminShop", adminShop);
        compound.setInteger("shopRotation", direction.ordinal());
        compound.setInteger("shopBuyPrice", buyPrice);
        compound.setInteger("shopSellPrice", sellPrice);
        if(owner != null) {
            compound.setString("shopOwnerUUID", owner.toString());
            //System.out.println("Writing: "+owner.toString());
        } else {
            compound.setString("shopOwnerUUID", "-1");
            //System.out.println("Writing: No Owner!");
        }
        compound.setInteger("shopItemCount", itemCount);
        if(itemTemplate != null) {
            NBTTagCompound itemTemplateStackCompound = new NBTTagCompound();
            itemTemplate.writeToNBT(itemTemplateStackCompound);
            compound.setTag("shopItemTemplate", itemTemplateStackCompound);
            compound.setBoolean("shopItemExists", true);
        } else compound.setBoolean("shopItemExists", false);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        adminShop = compound.getBoolean("shopAdminShop");
        int side = compound.getInteger("shopRotation");
        if(side > -1 && side < ForgeDirection.values().length) direction = ForgeDirection.values()[side];
        else direction = ForgeDirection.DOWN;
        buyPrice = compound.getInteger("shopBuyPrice");
        sellPrice = compound.getInteger("shopSellPrice");
        String uuidStr = compound.getString("shopOwnerUUID");
        if(!uuidStr.equals("-1")) {
            try { owner = UUID.fromString(uuidStr); }
            catch(Exception ex) { owner = null; System.out.println("Could not get Owner from saved UUID String"); }
        } else {
            owner = null;
        }
        itemCount = compound.getInteger("shopItemCount");
        boolean exists = compound.getBoolean("shopItemExists");
        if(exists) itemTemplate = ItemStack.loadItemStackFromNBT((NBTTagCompound)compound.getTag("shopItemTemplate"));
        else itemTemplate = null;
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound compound = new NBTTagCompound();
        this.writeToNBT(compound);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.func_148857_g());
    }
}
