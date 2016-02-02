package com.majorpotato.febridge.entity;


import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityCoin extends Entity {

    public int coinAge;
    public int coinValue;

    private int coinHealth = 5;
    private EntityPlayer closestPlayer;

    public EntityCoin(World world, double xPos, double yPos, double zPos, int coinValue) {
        super(world);
        this.setSize(0.5F, 0.5F);
        this.yOffset = this.height / 2.0F;
        this.setPosition(xPos, yPos, zPos);
        this.rotationYaw = (float)(Math.random() * 360.0D);
        this.motionX = (double)((float)(Math.random() * 0.20000000298023224D - 0.10000000149011612D) * 2.0F);
        this.motionY = (double)((float)(Math.random() * 0.2D) * 2.0F);
        this.motionZ = (double)((float)(Math.random() * 0.20000000298023224D - 0.10000000149011612D) * 2.0F);
        if(world.isRemote) this.coinValue = -1;
        else this.coinValue = coinValue;
    }

    public EntityCoin(World world) {
        super(world);
        this.setSize(0.25F, 0.25F);
        this.yOffset = this.height / 2.0F;
    }

    @Override
    protected boolean canTriggerWalking() { return false; }

    @Override
    protected void entityInit() {
        DataWatcher dw = this.getDataWatcher();
        dw.addObject(20, coinValue);
    }

    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender(float p_70070_1_) // ~----- A LOT OF THIS IS COPIED FROM ENTITYXPORB, FOR SIMPLICITY -----~
    {
        float f1 = 0.5F;

        if (f1 < 0.0F)
        {
            f1 = 0.0F;
        }

        if (f1 > 1.0F)
        {
            f1 = 1.0F;
        }

        int i = super.getBrightnessForRender(p_70070_1_);
        int j = i & 255;
        int k = i >> 16 & 255;
        j += (int)(f1 * 15.0F * 16.0F);

        if (j > 240)
        {
            j = 240;
        }

        return j | k << 16;
    }

    @Override
    public void onUpdate() { // ~----- A LOT OF THIS IS COPIED FROM ENTITYXPORB, FOR SIMPLICITY -----~
        super.onUpdate();

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY -= 0.029999999329447746D;

        if (this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)).getMaterial() == Material.lava)
        {
            this.motionY = 0.20000000298023224D;
            this.motionX = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
            this.motionZ = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
            this.playSound("random.fizz", 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
        }

        this.func_145771_j(this.posX, (this.boundingBox.minY + this.boundingBox.maxY) / 2.0D, this.posZ);
        double d0 = 8.0D;

        if (this.closestPlayer == null || this.closestPlayer.getDistanceSqToEntity(this) > d0 * d0)
        {
            this.closestPlayer = this.worldObj.getClosestPlayerToEntity(this, d0);
        }

        if (this.closestPlayer != null)
        {
            double d1 = (this.closestPlayer.posX - this.posX) / d0;
            double d2 = (this.closestPlayer.posY + (double)this.closestPlayer.getEyeHeight() - this.posY) / d0;
            double d3 = (this.closestPlayer.posZ - this.posZ) / d0;
            double d4 = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
            double d5 = 1.0D - d4;

            if (d5 > 0.0D)
            {
                d5 *= d5;
                this.motionX += d1 / d4 * d5 * 0.1D;
                this.motionY += d2 / d4 * d5 * 0.1D;
                this.motionZ += d3 / d4 * d5 * 0.1D;
            }
        }

        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        float f = 0.98F;

        if (this.onGround)
        {
            f = this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ)).slipperiness * 0.98F;
        }

        this.motionX *= (double)f;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= (double)f;

        if (this.onGround)
        {
            this.motionY *= -0.8999999761581421D;
        }

        ++this.coinAge;

        if(this.coinAge >= 6000) this.setDead();
    }

    @Override
    public boolean handleWaterMovement()
    {
        return this.worldObj.handleMaterialAcceleration(this.boundingBox, Material.water, this);
    }

    @Override
    protected void dealFireDamage(int amountDamage)
    {
        this.attackEntityFrom(DamageSource.inFire, (float)amountDamage);
    }

    public boolean attackEntityFrom(DamageSource source, float damageAmount)
    {
        if (this.isEntityInvulnerable())
        {
            return false;
        }
        else
        {
            this.setBeenAttacked();
            this.coinHealth = (int)((float)this.coinHealth - damageAmount);

            if (this.coinHealth <= 0)
            {
                this.setDead();
            }

            return false;
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setShort("Health", (short)(byte)this.coinHealth);
        compound.setShort("Age", (short)this.coinAge);
        compound.setShort("Value", (short)this.getCoinValue());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        this.coinHealth = compound.getShort("Health") & 255;
        this.coinAge = compound.getShort("Age");
        this.coinValue = compound.getShort("Value");
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer player) {
        if(!this.worldObj.isRemote) {
            APIRegistry.economy.getWallet(UserIdent.get(player)).add(coinValue);
            this.setDead();
        }
    }

    public int getCoinValue() {
        if(coinValue == -1) coinValue = getDataWatcher().getWatchableObjectInt(20);
        return this.coinValue;
    }

    // Theres probably an easier way to do this, but I'm being lazy
    @SideOnly(Side.CLIENT)
    public int getTextureByCoin() {
        int coinValue = getCoinValue();
        if(coinValue < 2) return 1;
        if(coinValue < 4) return 2;
        if(coinValue < 8) return 3;
        if(coinValue < 16) return 4;
        if(coinValue < 32) return 5;
        if(coinValue < 64) return 6;
        if(coinValue < 128) return 7;
        if(coinValue < 256) return 8;
        if(coinValue < 512) return 9;
        else return 10;
    }

    @Override
    public boolean canAttackWithItem() { return false; }
}
