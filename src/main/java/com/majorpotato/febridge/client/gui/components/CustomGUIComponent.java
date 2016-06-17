package com.majorpotato.febridge.client.gui.components;


import com.majorpotato.febridge.client.gui.components.events.CEventListener;
import com.majorpotato.febridge.client.gui.components.events.CEventListener_OnChanged;
import com.majorpotato.febridge.client.gui.components.events.CEventListener_OnSelected;
import com.majorpotato.febridge.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;

public abstract class CustomGUIComponent extends Gui {

    // ******************
    //  Static Variables
    // ******************

    public static final ResourceLocation componentsTexture = new ResourceLocation(Reference.MOD_ID.toLowerCase(), "textures/gui/EditorGUIFramework.png");
    public static final int TOOLTIP_COLOR = 0xDD555555;
    public static final int HOVER_COLOR = 0x20FFFFFF;
    public static final int SELECT_COLOR = 0x50FFFFFF;
    public static final int SELECT_TEXT_COLOR = 0xFFFFFB00;



    // *********************
    //  Visibility Controls
    // *********************

    protected boolean visible = true;
    public final void show() { visible = true; }
    public final void hide() { visible = false; }
    public final boolean isVisible() { return visible; }



    // ***************
    //  Overwritables
    // ***************

    public boolean isSelected() { return false; }
    public void mouseMovedOrUp(int xM, int yM, int which) {}
    public void mouseClickMove(int xM, int yM, int which, long dTime) {}
    public void mouseWheel(int xM, int yM, int amount) {}
    public boolean keyTyped(char letter, int key) { return false; }



    // **************
    //  Context Menu
    // **************

    protected ComponentContextMenu contextMenu = null;
    public void setContextMenu(ComponentContextMenu contextMenu) { this.contextMenu = contextMenu; }

    public void mouseClicked(int xM, int yM, int which) {
        if(which == 1 && contextMenu != null && xM > xPos && xM < xPos+width && yM > yPos && yM < yPos+height) {
            contextMenu.setPosition(xM, yM);
            contextMenu.show();
        }
    }



    // *************
    //  Draw Method
    // *************

    public abstract void draw(int xM, int yM);



    // *********************
    //  Positional Controls
    // *********************

    protected int xPos, yPos;
    protected int width, height;
    protected int screenWidth, screenHeight;
    public final int getXPos() { return xPos; }
    public final int getYPos() { return yPos; }
    public final int getWidth() { return width; }
    public final int getHeight() { return height; }

    public CustomGUIComponent(int xPos, int yPos, int width, int height) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.height = height;
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
        this.screenWidth = sr.getScaledWidth();
        this.screenHeight = sr.getScaledHeight();
    }



    // ****************
    //  Helper Methods
    // ****************

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

    protected final void playSelectSound() {
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
    }



    // ****************
    //  Listener Logic
    // ****************

    protected ArrayList<CEventListener_OnChanged> listeners_changed = new ArrayList<CEventListener_OnChanged>();
    protected ArrayList<CEventListener_OnSelected> listeners_selected = new ArrayList<CEventListener_OnSelected>();

    public void addEventListener(CEventListener listener) {

        // EVENT: On Changed
        if(listener instanceof CEventListener_OnChanged) {
            if(!listeners_changed.contains(listener)) listeners_changed.add((CEventListener_OnChanged)listener);
        }

        // EVENT: On Selected
        if(listener instanceof CEventListener_OnSelected) {
            if(!listeners_selected.contains(listener)) listeners_selected.add((CEventListener_OnSelected)listener);
        }

    }

    protected final void invokeChanged() {
        for(CEventListener_OnChanged listener : listeners_changed) listener.onComponentChanged(this);
    }

    protected final void invokeSelected() {
        for(CEventListener_OnSelected listener : listeners_selected) listener.onComponentSelected(this);
    }
}
