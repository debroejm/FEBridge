package com.majorpotato.febridge.rendering;


import com.majorpotato.febridge.init.ModBlocks;
import com.majorpotato.febridge.rendering.model.ModelShop;
import com.majorpotato.febridge.rendering.model.ModelTollGate;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class GexItemRenderer implements IItemRenderer {

    private ModelShop mShop = new ModelShop();
    private ModelTollGate mTollGate = new ModelTollGate();

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) { return true; }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) { return true; }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        boolean shop = false;
        boolean gate = false;
        if(item.isItemEqual(new ItemStack(ModBlocks.blockShop, 1, 0))) { Minecraft.getMinecraft().renderEngine.bindTexture(ServiceRenderer.tShopPersonal); shop = true; }
        else if(item.isItemEqual(new ItemStack(ModBlocks.blockShop, 1, 8))) { Minecraft.getMinecraft().renderEngine.bindTexture(ServiceRenderer.tShopAdmin); shop = true; }
        else if(item.isItemEqual(new ItemStack(ModBlocks.blockTollGate, 1, 0))) { Minecraft.getMinecraft().renderEngine.bindTexture(ServiceRenderer.tTollGatePersonal); gate = true; }
        else if(item.isItemEqual(new ItemStack(ModBlocks.blockTollGate, 1, 8))) { Minecraft.getMinecraft().renderEngine.bindTexture(ServiceRenderer.tTollGateAdmin); gate = true; }

        GL11.glPushMatrix();
        GL11.glScalef(0.8F, 0.8F, 0.8F);
        GL11.glTranslatef(0.0F, 1.0F, 0.0F);
        if (type == ItemRenderType.EQUIPPED)
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        else if (type == ItemRenderType.EQUIPPED_FIRST_PERSON)
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        if(shop) {
            mShop.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
        } else if(gate) {
            mTollGate.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
        }
        GL11.glPopMatrix();
    }
}
