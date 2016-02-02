package com.majorpotato.febridge.rendering;


import com.majorpotato.febridge.entity.EntityCoin;
import com.majorpotato.febridge.reference.Reference;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RenderCoin extends Render {

    private static final ResourceLocation coinTextures = new ResourceLocation(Reference.MOD_ID+":textures/entity/coinSheet.png");

    public RenderCoin() {
        this.shadowSize = 0.15F;
        this.shadowOpaque = 0.75F;
    }

    @Override
    public void doRender(Entity entity, double xPos, double yPos, double zPos, float f, float f1) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)xPos, (float)yPos, (float)zPos);
        this.bindEntityTexture(entity);
        int i = ((EntityCoin)entity).getTextureByCoin();
        float f2 = (float)(i % 4 * 16) / 64.0F;
        float f3 = (float)(i % 4 * 16 + 16) / 64.0F;
        float f4 = (float)(i / 4 * 16) / 64.0F;
        float f5 = (float)(i / 4 * 16 + 16) / 64.0F;
        float f6 = 1.0F;
        float f7 = 0.5F;
        float f8 = 0.25F;
        int j = entity.getBrightnessForRender(f1);
        int k = j % 65536;
        int l = j / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)k / 1.0F, (float)l / 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(0.3F, 0.3F, 0.3F);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_I(0xFFFFFF, 128);
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        tessellator.addVertexWithUV((double)(0.0F - f7), (double)(0.0F - f8), 0.0D, (double)f2, (double)f5);
        tessellator.addVertexWithUV((double)(f6 - f7), (double)(0.0F - f8), 0.0D, (double)f3, (double)f5);
        tessellator.addVertexWithUV((double)(f6 - f7), (double)(1.0F - f8), 0.0D, (double)f3, (double)f4);
        tessellator.addVertexWithUV((double)(0.0F - f7), (double)(1.0F - f8), 0.0D, (double)f2, (double)f4);
        tessellator.draw();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) { return coinTextures; }
}
