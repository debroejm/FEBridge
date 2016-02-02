package com.majorpotato.febridge.rendering.model;


import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelTollGate extends ModelBase {

    ModelRenderer LeftDoor;
    ModelRenderer RightDoor;

    public ModelTollGate()
    {
        textureWidth = 32;
        textureHeight = 32;

        LeftDoor = new ModelRenderer(this, 0, 0);
        LeftDoor.addBox(-1F, 0F, -1F, 8, 12, 4);
        LeftDoor.setRotationPoint(-7F, 10F, -1F);
        LeftDoor.setTextureSize(32, 32);
        LeftDoor.mirror = true;
        setRotation(LeftDoor, 0F, 0F, 0F);
        RightDoor = new ModelRenderer(this, 0, 0);
        RightDoor.addBox(-1F, 0F, -3F, 8, 12, 4);
        RightDoor.setRotationPoint(7F, 10F, -1F);
        RightDoor.setTextureSize(32, 32);
        RightDoor.mirror = true;
        setRotation(RightDoor, 0F, 3.141593F, 0F);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(entity, f, f1, f2, f3, f4, f5);
        LeftDoor.render(f5);
        RightDoor.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void setRotationAngles(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
    }

    public void setOpen(float amount) {
        setRotation(RightDoor, 0.0F, 3.141593F+amount, 0.0F);
        setRotation(LeftDoor, 0.0F, -amount, 0.0F);
    }
}
