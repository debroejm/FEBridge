package com.majorpotato.febridge.handler;


import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.economy.ModuleEconomy;
import com.majorpotato.febridge.entity.EntityCoin;
import com.majorpotato.febridge.init.ModPermissions;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

@SideOnly(Side.SERVER)
public class CoinLootHandler {

    @SubscribeEvent
    public void onEntitySlain(LivingDeathEvent event) {
        if(event.entity.worldObj.isRemote) return;
        // ~----- DROP PLAYER CURRENCY -----~
        if(event.entity instanceof EntityPlayer) {
            int coinValue;
            try {
                double deathToll = Double.parseDouble(APIRegistry.perms.getGlobalPermissionProperty(ModuleEconomy.PERM_DEATHTOLL));
                if(deathToll < 1.0) coinValue = (int)(APIRegistry.economy.getWallet(UserIdent.get((EntityPlayer)event.entity)).get()*deathToll);
                else coinValue = (int)deathToll;
            } catch(Exception ex) { coinValue = 0; }
            spawnCoinLootInWorld(event.entity.worldObj, event.entity.posX, event.entity.posY, event.entity.posZ, coinValue);
        }
        // ~----- DROP MOB BOUNTY -----~
        else if(event.source.getSourceOfDamage() instanceof EntityPlayer) {
            int coinValue;
            try {
                coinValue = (int)Double.parseDouble(APIRegistry.perms.getGlobalPermissionProperty(ModPermissions.PERM_BOUNTY + "." + EntityList.getEntityString(event.entity)));
            } catch(Exception ex) { coinValue = 0; }
            spawnCoinLootInWorld(event.entity.worldObj, event.entity.posX, event.entity.posY, event.entity.posZ, coinValue);
        }
    }

    public static void spawnCoinLootInWorld(World world, double xPos, double yPos, double zPos, int value) {
        int fidgetFactor = (int)(((Math.random() - 0.5f)/10.0f)*value);
        int valueLeft = value+fidgetFactor;
        while(valueLeft > 0) {
            int subValue = (int)(value / (Math.random()*7.0f+3));
            if(subValue > 50) subValue = 45 + (int)(Math.random()*11.0f);
            if(subValue > valueLeft) subValue = valueLeft;
            EntityCoin coin = new EntityCoin(world, xPos, yPos, zPos, subValue);
            world.spawnEntityInWorld(coin);
            valueLeft -= subValue;
        }
    }
}
