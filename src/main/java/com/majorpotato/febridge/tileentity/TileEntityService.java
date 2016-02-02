package com.majorpotato.febridge.tileentity;


import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.economy.Wallet;
import com.majorpotato.febridge.FEBridge;
import com.majorpotato.febridge.init.ModPermissions;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;

import java.util.UUID;

public class TileEntityService extends TileEntity implements ICurrencyService {

    // Who owns this Shop?
    protected UUID owner;

    // Shop Information
    protected int buyPrice = 0;
    protected int sellPrice = 0;

    // Is this shop and Admin Shop (not player owned)?
    protected boolean adminService = false;

    protected int activeTicks = 0;
    public boolean isActive() { return activeTicks > 0; }

    public TileEntityService() { this(false); }
    public TileEntityService(boolean adminService) {
        this.adminService = adminService;
    }

    protected void activate(EntityPlayer player) {
        worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, blockType);
    }

    protected void deactivate() {
        worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, blockType);
    }

    @Override
    public void updateEntity() {
        if(activeTicks > 0) {
            activeTicks--;
            if(activeTicks < 1) deactivate();
        }
    }

    @Override
    public boolean leftClick(EntityPlayer player) {
        return rightClick(player); // Performs same function
    }

    @Override
    public boolean rightClick(EntityPlayer player) {
        if(worldObj.isRemote) return false;

        if(!APIRegistry.perms.checkPermission(player, ModPermissions.PERM_SERVICES_BUY)) {
            player.addChatMessage(new ChatComponentText("§e§oYou don't have permission to buy from Services."));
            return true;
        }

        if(owner == null) setOwner(player);
        if(!adminService && owner.equals(player.getPersistentID())) {
            FMLNetworkHandler.openGui(player, FEBridge.instance, 0, worldObj, xCoord, yCoord, zCoord);
            return true;
        }

        if(buyPrice < 1) return false; // Means this service is de-activated. Wouldn't know why, but whatever...

        Wallet userWallet = APIRegistry.economy.getWallet(UserIdent.get(player));

        if(userWallet.covers(buyPrice)) {
            userWallet.withdraw(buyPrice);
            player.addChatMessage(new ChatComponentText("§e§oYou have paid " + buyPrice + " " + APIRegistry.economy.currency(2)+" ("+userWallet.get()+" Remaining)"));
            if(!adminService) APIRegistry.economy.getWallet(UserIdent.get(owner)).add(buyPrice);
            activeTicks = 60;
            activate(player);
        } else {
            player.addChatMessage(new ChatComponentText("§e§oYou don't have enough "+APIRegistry.economy.currency(2)+" to Pay For this Service."));
        }

        return true;
    }

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
    public boolean useSellPrice() { return false; }
    @Override
    public boolean isAdminService() { return adminService; }
    @Override
    public UUID getOwner() { return owner; }
    @Override
    public void setOwner(EntityPlayer player) { setOwner(player.getPersistentID()); }
    @Override
    public void setOwner(UUID uuid) {
        if(!adminService) owner = uuid;
        else owner = null;
    }
    @Override
    public String getServiceName() { return new ItemStack(blockType, 1, adminService ? 1 : 0).getDisplayName(); }
    @Override
    public int getXCoord() { return xCoord; }
    @Override
    public int getYCoord() { return yCoord; }
    @Override
    public int getZCoord() { return zCoord; }



    // =======================
    // | Data Saving/Syncing |
    // =======================

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        compound.setBoolean("serviceAdmin", adminService);
        compound.setInteger("serviceBuyPrice", buyPrice);
        compound.setInteger("serviceSellPrice", sellPrice);
        if(owner != null) {
            compound.setString("serviceOwnerUUID", owner.toString());
        } else {
            compound.setString("serviceOwnerUUID", "-1");
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        adminService = compound.getBoolean("serviceAdmin");
        buyPrice = compound.getInteger("serviceBuyPrice");
        sellPrice = compound.getInteger("serviceSellPrice");
        String uuidStr = compound.getString("serviceOwnerUUID");
        if(!uuidStr.equals("-1")) {
            try { owner = UUID.fromString(uuidStr); }
            catch(Exception ex) { owner = null; }
        } else {
            owner = null;
        }
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
