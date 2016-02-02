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
            int meta = service.getWorldObj().getBlockMetadata(service.getXCoord(), service.getYCoord(), service.getZCoord());
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

    /*
    // Copied and tweaked from RenderBlocks
    public boolean renderTollGate(TileEntityTollGate tollGate)
    {
        if(tollGate.isAdminService()) Minecraft.getMinecraft().renderEngine.bindTexture(tServiceAdmin);
        else Minecraft.getMinecraft().renderEngine.bindTexture(tServicePersonal);

        RenderBlocks rb = RenderBlocks.getInstance();
        rb.blockAccess = tollGate.getWorldObj();
        boolean flag = true;
        int metadata = tollGate.getWorldObj().getBlockMetadata(tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
        boolean gateOpen = BlockTollGate.isTollGateOpen(metadata);
        int direct = BlockDirectional.getDirection(metadata);
        float f = 0.375F;
        float f1 = 0.5625F;
        float f2 = 0.75F;
        float f3 = 0.9375F;
        float f4 = 0.3125F;
        float f5 = 1.0F;

        if ((direct == 2 || direct == 0) && tollGate.getWorldObj().getBlock(tollGate.xCoord - 1, tollGate.yCoord, tollGate.zCoord) == Blocks.cobblestone_wall && tollGate.getWorldObj().getBlock(tollGate.xCoord + 1, tollGate.yCoord, tollGate.zCoord) == Blocks.cobblestone_wall || (direct == 3 || direct == 1) && tollGate.getWorldObj().getBlock(tollGate.xCoord, tollGate.yCoord, tollGate.zCoord - 1) == Blocks.cobblestone_wall && tollGate.getWorldObj().getBlock(tollGate.xCoord, tollGate.yCoord, tollGate.zCoord + 1) == Blocks.cobblestone_wall)
        {
            f -= 0.1875F;
            f1 -= 0.1875F;
            f2 -= 0.1875F;
            f3 -= 0.1875F;
            f4 -= 0.1875F;
            f5 -= 0.1875F;
        }

        rb.renderAllFaces = true;
        float f6;
        float f7;
        float f8;
        float f9;

        if (direct != 3 && direct != 1)
        {
            f6 = 0.0F;
            f7 = 0.125F;
            f8 = 0.4375F;
            f9 = 0.5625F;
            rb.setRenderBounds((double)f6, (double)f4, (double)f8, (double)f7, (double)f5, (double)f9);
            rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
            f6 = 0.875F;
            f7 = 1.0F;
            rb.setRenderBounds((double)f6, (double)f4, (double)f8, (double)f7, (double)f5, (double)f9);
            rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
        }
        else
        {
            rb.uvRotateTop = 1;
            f6 = 0.4375F;
            f7 = 0.5625F;
            f8 = 0.0F;
            f9 = 0.125F;
            rb.setRenderBounds((double)f6, (double)f4, (double)f8, (double)f7, (double)f5, (double)f9);
            rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
            f8 = 0.875F;
            f9 = 1.0F;
            rb.setRenderBounds((double)f6, (double)f4, (double)f8, (double)f7, (double)f5, (double)f9);
            rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
            rb.uvRotateTop = 0;
        }

        if (gateOpen)
        {
            if (direct == 2 || direct == 0)
            {
                rb.uvRotateTop = 1;
            }

            float f10;
            float f11;
            float f12;

            if (direct == 3)
            {
                f6 = 0.0F;
                f7 = 0.125F;
                f8 = 0.875F;
                f9 = 1.0F;
                f10 = 0.5625F;
                f11 = 0.8125F;
                f12 = 0.9375F;
                rb.setRenderBounds(0.8125D, (double)f, 0.0D, 0.9375D, (double)f3, 0.125D);
                rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
                rb.setRenderBounds(0.8125D, (double)f, 0.875D, 0.9375D, (double)f3, 1.0D);
                rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
                rb.setRenderBounds(0.5625D, (double)f, 0.0D, 0.8125D, (double)f1, 0.125D);
                rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
                rb.setRenderBounds(0.5625D, (double)f, 0.875D, 0.8125D, (double)f1, 1.0D);
                rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
                rb.setRenderBounds(0.5625D, (double)f2, 0.0D, 0.8125D, (double)f3, 0.125D);
                rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
                rb.setRenderBounds(0.5625D, (double)f2, 0.875D, 0.8125D, (double)f3, 1.0D);
                rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
            }
            else if (direct == 1)
            {
                f6 = 0.0F;
                f7 = 0.125F;
                f8 = 0.875F;
                f9 = 1.0F;
                f10 = 0.0625F;
                f11 = 0.1875F;
                f12 = 0.4375F;
                rb.setRenderBounds(0.0625D, (double)f, 0.0D, 0.1875D, (double)f3, 0.125D);
                rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
                rb.setRenderBounds(0.0625D, (double)f, 0.875D, 0.1875D, (double)f3, 1.0D);
                rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
                rb.setRenderBounds(0.1875D, (double)f, 0.0D, 0.4375D, (double)f1, 0.125D);
                rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
                rb.setRenderBounds(0.1875D, (double)f, 0.875D, 0.4375D, (double)f1, 1.0D);
                rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
                rb.setRenderBounds(0.1875D, (double)f2, 0.0D, 0.4375D, (double)f3, 0.125D);
                rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
                rb.setRenderBounds(0.1875D, (double)f2, 0.875D, 0.4375D, (double)f3, 1.0D);
                rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
            }
            else if (direct == 0)
            {
                f6 = 0.0F;
                f7 = 0.125F;
                f8 = 0.875F;
                f9 = 1.0F;
                f10 = 0.5625F;
                f11 = 0.8125F;
                f12 = 0.9375F;
                rb.setRenderBounds(0.0D, (double)f, 0.8125D, 0.125D, (double)f3, 0.9375D);
                rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
                rb.setRenderBounds(0.875D, (double)f, 0.8125D, 1.0D, (double)f3, 0.9375D);
                rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
                rb.setRenderBounds(0.0D, (double)f, 0.5625D, 0.125D, (double)f1, 0.8125D);
                rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
                rb.setRenderBounds(0.875D, (double)f, 0.5625D, 1.0D, (double)f1, 0.8125D);
                rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
                rb.setRenderBounds(0.0D, (double)f2, 0.5625D, 0.125D, (double)f3, 0.8125D);
                rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
                rb.setRenderBounds(0.875D, (double)f2, 0.5625D, 1.0D, (double)f3, 0.8125D);
                rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
            }
            else if (direct == 2)
            {
                f6 = 0.0F;
                f7 = 0.125F;
                f8 = 0.875F;
                f9 = 1.0F;
                f10 = 0.0625F;
                f11 = 0.1875F;
                f12 = 0.4375F;
                rb.setRenderBounds(0.0D, (double)f, 0.0625D, 0.125D, (double)f3, 0.1875D);
                rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
                rb.setRenderBounds(0.875D, (double)f, 0.0625D, 1.0D, (double)f3, 0.1875D);
                rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
                rb.setRenderBounds(0.0D, (double)f, 0.1875D, 0.125D, (double)f1, 0.4375D);
                rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
                rb.setRenderBounds(0.875D, (double)f, 0.1875D, 1.0D, (double)f1, 0.4375D);
                rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
                rb.setRenderBounds(0.0D, (double)f2, 0.1875D, 0.125D, (double)f3, 0.4375D);
                rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
                rb.setRenderBounds(0.875D, (double)f2, 0.1875D, 1.0D, (double)f3, 0.4375D);
                rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
            }
        }
        else if (direct != 3 && direct != 1)
        {
            f6 = 0.375F;
            f7 = 0.5F;
            f8 = 0.4375F;
            f9 = 0.5625F;
            rb.setRenderBounds((double)f6, (double)f, (double)f8, (double)f7, (double)f3, (double)f9);
            rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
            f6 = 0.5F;
            f7 = 0.625F;
            rb.setRenderBounds((double)f6, (double)f, (double)f8, (double)f7, (double)f3, (double)f9);
            rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
            f6 = 0.625F;
            f7 = 0.875F;
            rb.setRenderBounds((double)f6, (double)f, (double)f8, (double)f7, (double)f1, (double)f9);
            rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
            rb.setRenderBounds((double)f6, (double)f2, (double)f8, (double)f7, (double)f3, (double)f9);
            rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
            f6 = 0.125F;
            f7 = 0.375F;
            rb.setRenderBounds((double)f6, (double)f, (double)f8, (double)f7, (double)f1, (double)f9);
            rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
            rb.setRenderBounds((double)f6, (double)f2, (double)f8, (double)f7, (double)f3, (double)f9);
            rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
        }
        else
        {
            rb.uvRotateTop = 1;
            f6 = 0.4375F;
            f7 = 0.5625F;
            f8 = 0.375F;
            f9 = 0.5F;
            rb.setRenderBounds((double)f6, (double)f, (double)f8, (double)f7, (double)f3, (double)f9);
            rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
            f8 = 0.5F;
            f9 = 0.625F;
            rb.setRenderBounds((double)f6, (double)f, (double)f8, (double)f7, (double)f3, (double)f9);
            rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
            f8 = 0.625F;
            f9 = 0.875F;
            rb.setRenderBounds((double)f6, (double)f, (double)f8, (double)f7, (double)f1, (double)f9);
            rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
            rb.setRenderBounds((double)f6, (double)f2, (double)f8, (double)f7, (double)f3, (double)f9);
            rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
            f8 = 0.125F;
            f9 = 0.375F;
            rb.setRenderBounds((double)f6, (double)f, (double)f8, (double)f7, (double)f1, (double)f9);
            rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
            rb.setRenderBounds((double)f6, (double)f2, (double)f8, (double)f7, (double)f3, (double)f9);
            rb.renderStandardBlock(tollGate.blockType, tollGate.xCoord, tollGate.yCoord, tollGate.zCoord);
        }

        rb.renderAllFaces = false;
        rb.uvRotateTop = 0;
        rb.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
        rb.blockAccess = null; // Return to how it was before we started this
        return flag;
    }
    */
}
