package com.majorpotato.febridge.tileentity;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.UUID;

public interface ICurrencyService {

    int getBuyPrice();
    int getSellPrice();
    void setBuyPrice(int value);
    void setSellPrice(int value);
    boolean useBuyPrice();
    boolean useSellPrice();

    UUID getOwner();
    void setOwner(EntityPlayer player);
    void setOwner(UUID uuid);

    boolean isAdminService();

    boolean leftClick(EntityPlayer player);
    boolean rightClick(EntityPlayer player);

    String getServiceName();

    void markDirty();

    int getXCoord();
    int getYCoord();
    int getZCoord();

    World getWorld();
}
