package com.majorpotato.febridge.rendering;


import com.majorpotato.febridge.block.BlockTollGate;
import com.majorpotato.febridge.reference.Reference;
import com.majorpotato.febridge.rendering.model.ModelShop;
import com.majorpotato.febridge.rendering.model.ModelTollGate;
import com.majorpotato.febridge.tileentity.ICurrencyService;
import com.majorpotato.febridge.tileentity.TileEntityShop;
import com.majorpotato.febridge.tileentity.TileEntityTollGate;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ServiceRenderer extends TileEntitySpecialRenderer {

    // Models
    private final ModelShop mShop = new ModelShop();
    private final ModelTollGate mTollGate = new ModelTollGate();

    // Textures
    public static final ResourceLocation tShopPersonal = new ResourceLocation(Reference.MOD_ID+":textures/models/ShopPersonal.png");
    public static final ResourceLocation tShopAdmin = new ResourceLocation(Reference.MOD_ID+":textures/models/ShopAdmin.png");
    public static final ResourceLocation tTollGatePersonal = new ResourceLocation(Reference.MOD_ID+":textures/models/TollGatePersonal.png");
    public static final ResourceLocation tTollGateAdmin = new ResourceLocation(Reference.MOD_ID+":textures/models/TollGateAdmin.png");

    // Misc
    private EntityItem displayItem;


    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float scale) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);

        adjustLightFixture(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord, te.getBlockType());

        if(te instanceof ICurrencyService) renderService((ICurrencyService)te);

        GL11.glPopMatrix();
    }

    private void adjustLightFixture(World world, int i, int j, int k, Block block) {
        Tessellator tess = Tessellator.instance;
        float brightness = block.getLightValue(world, i, j, k);
        int skyLight = world.getLightBrightnessForSkyBlocks(i, j, k, 0);
        int modulousModifier = skyLight % 65536;
        int divModifier = skyLight / 65536;
        tess.setColorOpaque_F(brightness, brightness, brightness);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) modulousModifier, divModifier);
    }

    private void renderService(ICurrencyService service)
    {
        Minecraft mc = Minecraft.getMinecraft();
        MovingObjectPosition rayTrace = mc.renderViewEntity.rayTrace(200, 1.0F);

        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        float distance = (float)Math.sqrt(Math.pow(player.posX-service.getXCoord()+0.5F, 2)+Math.pow(player.posY-service.getYCoord()+0.5F, 2)+Math.pow(player.posZ-service.getZCoord()+0.5F, 2));

        float scale = 1.5F - 1.5F * Math.max(distance - 10.0F, 0.0F) / 10.0F;

        GL11.glPushMatrix();

        if(service instanceof TileEntityShop) {
            TileEntityShop shop = (TileEntityShop)service;
            ForgeDirection direction = shop.getDirection();

            GL11.glTranslatef(0.0f, -1.0f, 0.0f);

            // ~----- ITEM RENDERING -----~
            if(shop.isItemSet() && distance < 20.0) {
                GL11.glPushMatrix();
                if (displayItem == null)
                    displayItem = new EntityItem(Minecraft.getMinecraft().theWorld, 0D, 0D, 0D, shop.getItemTemplate());
                else
                    displayItem.setEntityItemStack(shop.getItemTemplate());
                displayItem.hoverStart = 0.0F;
                RenderItem.renderInFrame = true;
                GL11.glScalef(scale, scale, scale);
                GL11.glTranslatef(direction.offsetX*0.3f, direction.offsetY*0.3F-0.2F, direction.offsetZ*0.3F);
                GL11.glRotatef(Minecraft.getMinecraft().theWorld.getTotalWorldTime() % 60 * 6.0F, 0.0F, 1.0F, 0.0F);
                RenderManager.instance.renderEntityWithPosYaw(displayItem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
                RenderItem.renderInFrame = false;
                GL11.glPopMatrix();
            }

            // ~----- SHOP RENDERING -----~
            GL11.glPushMatrix();
            if(shop.isAdminService()) Minecraft.getMinecraft().renderEngine.bindTexture(tShopAdmin);
            else Minecraft.getMinecraft().renderEngine.bindTexture(tShopPersonal);
            switch(direction) {
                case UP:
                    GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
                    break;
                case NORTH:
                    GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
                    break;
                case SOUTH:
                    GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
                    break;
                case EAST:
                    GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
                    GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
                    break;
                case WEST:
                    GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
                    GL11.glRotatef(-90.0f, 0.0f, 0.0f, 1.0f);
                    break;
                default: break;
            }
            GL11.glTranslatef(0.0f, -1.0f, 0.0f);

            mShop.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
            GL11.glPopMatrix();
        } else if(service instanceof TileEntityTollGate) {
            GL11.glPushMatrix();
            if(service.isAdminService()) Minecraft.getMinecraft().renderEngine.bindTexture(tTollGateAdmin);
            else Minecraft.getMinecraft().renderEngine.bindTexture(tTollGatePersonal);
            int meta = service.getWorld().getBlockMetadata(service.getXCoord(), service.getYCoord(), service.getZCoord());
            if(BlockTollGate.isTollGateOpen(meta)) mTollGate.setOpen(1.571F);
            else mTollGate.setOpen(0.0F);
            GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
            GL11.glRotatef(90.0f*BlockTollGate.getDirection(meta), 0.0f, 1.0f, 0.0f);
            mTollGate.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
            GL11.glPopMatrix();
        }

        // ~----- IN-WORLD TEXT -----~
        if(distance < 20.0 && rayTrace != null
                && rayTrace.blockX == service.getXCoord() && rayTrace.blockY == service.getYCoord() && rayTrace.blockZ == service.getZCoord()
                && (!(service instanceof TileEntityShop) || ((TileEntityShop) service).isItemSet())) renderTextEffect(service, scale, rayTrace.sideHit);

        GL11.glPopMatrix();
    }

    private void renderTextEffect(ICurrencyService service, float scale, int side) {
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;

        GL11.glPushMatrix();

        FontRenderer font = this.func_147498_b();
        float fSc = scale / 90.0F;

        // ~----- SHOP ROTATION (Moves Freely) -----~
        if(service instanceof TileEntityShop) {
            ForgeDirection direction = ((TileEntityShop)service).getDirection();
            GL11.glTranslatef(direction.offsetX*0.0f,direction.offsetY*0.90f-0.05f,direction.offsetZ*0.0f);
            GL11.glRotatef((player.getRotationYawHead()+180.0F)*-1.0F, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(0.0F, 0.0f, 0.8F);
        }
        // ~----- OTHER ROTATION (Attached to Block Faces) -----~
        else {
            GL11.glTranslatef(0.0f, -1.1f, 0.0f);
            ForgeDirection sideDirection = ForgeDirection.getOrientation(side);
            switch(sideDirection) {
                case EAST:
                    GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                    break;
                case WEST:
                    GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                    break;
                case NORTH:
                    GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                    break;
                case SOUTH:
                    break;
                default:
                    GL11.glPopMatrix(); // Gotta return the stack to neutral
                    return;
            }
            /*if(service instanceof TileEntityTollGate) {
                ForgeDirection gateDirection = ((TileEntityTollGate)service).getDirection();
                if(sideDirection == gateDirection || sideDirection == gateDirection.getOpposite()) GL11.glTranslatef(0.0F, 0.0F, 0.2F);
                else {
                    GL11.glPopMatrix(); // Gotta return the stack to neutral
                    return;
                }
            } else */GL11.glTranslatef(0.0F, 0.0F, 0.6F);
        }

        GL11.glScalef(fSc, -fSc, fSc);
        GL11.glNormal3f(0.0F, 0.0F, -1.0F * fSc);
        //GL11.glDepthMask(false);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        // Render each line only if that information is in use
        if(service.useBuyPrice() && service.getBuyPrice() > 0) {
            String buyText = "Buy: " + service.getBuyPrice();
            font.drawString(buyText, -font.getStringWidth(buyText) / 2, -15, Color.white.getRGB(), false);
        }
        if(service.useSellPrice() && service.getSellPrice() > 0) {
            String sellText = "Sell: " + service.getSellPrice();
            font.drawString(sellText, -font.getStringWidth(sellText) / 2, -5, Color.white.getRGB(), false);
        }
        if(service instanceof TileEntityShop && !service.isAdminService()) {
            String stockText = "Stock: " + ((TileEntityShop)service).getItemCount();
            font.drawString(stockText, -font.getStringWidth(stockText) / 2, -25, Color.white.getRGB(), false);
        }
        //GL11.glDepthMask(true);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        GL11.glPopMatrix();
    }
}
