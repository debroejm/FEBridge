package com.majorpotato.febridge.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;

import java.util.List;
import java.util.UUID;

public class Misc {

    @SideOnly(Side.SERVER)
    public static EntityPlayerMP getPlayerFromUUID(UUID uuid) {
        if(uuid == null) return null;
        List<EntityPlayerMP> players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
        for(EntityPlayerMP player : players) {
            if(player.getPersistentID().equals(uuid)) return player;
        }
        return null;
    }
}
