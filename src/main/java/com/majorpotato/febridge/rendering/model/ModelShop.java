package com.majorpotato.febridge.rendering.model;


import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelShop extends ModelBase
{
    //fields
    ModelRenderer bottom;

    public ModelShop()
    {
        textureWidth = 64;
        textureHeight = 32;

        bottom = new ModelRenderer(this, 0, 0);
        bottom.addBox(-8F, 8F, -8F, 16, 8, 16);
        bottom.setRotationPoint(0F, 8F, 0F);
        bottom.setTextureSize(64, 32);
        bottom.mirror = true;
        setRotation(bottom, 0F, 0F, 0F);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        bottom.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
    {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
    }

}
