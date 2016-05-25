package com.majorpotato.febridge.client.gui.components;


import com.majorpotato.febridge.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public abstract class CustomGUIComponent extends Gui {

    public static final ResourceLocation componentsTexture = new ResourceLocation(Reference.MOD_ID.toLowerCase(), "textures/gui/EditorGUIFramework.png");

    public final void show() { visible = true; }
    public final void hide() { visible = false; }
    public final boolean isVisible() { return visible; }

    public abstract void draw();
    public abstract void mouseClicked(int xM, int yM, int which);
    public abstract boolean keyTyped(char letter, int key);
    
    protected final void drawSquare(int xPos, int yPos, int width, int height, int texX, int texY, int texWidth, int texHeight) {
        int texWMid = texWidth * 3 / 4;
        int texHMid = texHeight * 3 / 4;
        int texWQuart = texWidth / 4;
        int texHQuart = texHeight / 4;
        int texW8 = texWidth / 8;
        int texH8 = texHeight / 8;

        int endWidth = width / 2;
        if(endWidth > texWMid) endWidth = texWMid;
        int midWSize = width - (endWidth*2);
        int midWCount = midWSize / texWMid;
        midWSize = midWSize % texWMid;
        if(midWCount == 0) midWSize = 0;

        int endHeight = height / 2;
        if(endHeight > texHMid) endHeight = texHMid;
        int midHSize = height - (endHeight*2);
        int midHCount = midHSize / texHMid;
        midHSize = midHSize % texHMid;
        if(midHCount == 0) midHSize = 0;

        // Draw Corners
        drawTexturedModalRect(xPos, yPos, texX, texY, endWidth, endHeight);
        drawTexturedModalRect(xPos+endWidth+midWCount*texWMid+midWSize, yPos, texX+texWQuart, texY, endWidth, endHeight);
        drawTexturedModalRect(xPos, yPos+endHeight+midHCount*texHMid+midHSize, texX, texY+texHQuart, endWidth, endHeight);
        drawTexturedModalRect(xPos+endWidth+midWCount*texWMid+midWSize, yPos+endHeight+midHCount*texHMid+midHSize, texX+texWQuart, texY+texHQuart, endWidth, endHeight);

        // Draw Top/Bottom Edges
        for(int i = 0; i < midWCount; i++) {
            drawTexturedModalRect(xPos+endWidth+i*texWMid, yPos, texX+texW8, texY, texWMid, endHeight);
            drawTexturedModalRect(xPos+endWidth+i*texWMid, yPos+endHeight+midHCount*texHMid, texX+texW8, texY+texH8, texWMid, midHSize);
            drawTexturedModalRect(xPos+endWidth+i*texWMid, yPos+endHeight+midHCount*texHMid+midHSize, texX+texW8, texY+texHeight-endHeight, texWMid, endHeight);
        }
        drawTexturedModalRect(xPos+endWidth+midWCount*texWMid, yPos, texX+texW8, texY, midWSize, endHeight);
        drawTexturedModalRect(xPos+endWidth+midWCount*texWMid, yPos+endHeight+midHCount*texHMid+midHSize, texX+texW8, texY+texHeight-endHeight, midWSize, endHeight);

        // Draw Left/Right Edges
        for(int i = 0; i < midHCount; i++) {
            drawTexturedModalRect(xPos, yPos+endHeight+i*texHMid, texX, texY+texH8, endWidth, texHMid);
            drawTexturedModalRect(xPos+endWidth+midWCount*texWMid, yPos+endHeight+i*texHMid, texX+texW8, texY+texH8, midWSize, texHMid);
            drawTexturedModalRect(xPos+endWidth+midWCount*texWMid+midWSize, yPos+endHeight+i*texHMid, texX+texWidth-endWidth, texY+texH8, endWidth, texHMid);
        }
        drawTexturedModalRect(xPos, yPos+endHeight+midHCount*texHMid, texX, texY+texH8, endWidth, midHSize);
        drawTexturedModalRect(xPos+endWidth+midWCount*texWMid+midWSize, yPos+endHeight+midHCount*texHMid, texX+texWidth-endWidth, texY+texH8, endWidth, midHSize);

        // Draw Mid Sections
        for(int x = 0; x < midWCount; x++) {
            for(int y = 0; y < midHCount; y++) {
                drawTexturedModalRect(xPos+endWidth+x*texWMid, yPos+endHeight+y*texHMid, texX+texW8, texY+texH8, texWMid, texHMid);
            }
        }

        drawTexturedModalRect(xPos+endWidth+midWCount*texWMid, yPos+endHeight+midHCount*texHMid, texX+texW8, texY+texH8, midWSize, midHSize);
    }

    private boolean visible = true;
}
