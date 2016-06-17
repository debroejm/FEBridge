package com.majorpotato.febridge.client.gui.components;


import com.majorpotato.febridge.client.gui.components.events.CEventListener_OnSelected;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

public class ComponentTabs extends CustomGUIComponent {

    HashMap<String, CustomGUIComponent> tabMap = new HashMap<String, CustomGUIComponent>();

    protected String currentTab = null;
    protected FontRenderer fontRendererObj;

    public ComponentTabs(int xPos, int yPos, FontRenderer fontRendererObj) {
        super(xPos, yPos, 0, fontRendererObj.FONT_HEIGHT+8);
        this.fontRendererObj = fontRendererObj;
    }

    public void addTab(String name, CustomGUIComponent component) {
        if(tabMap.containsKey(name)) return;
        tabMap.put(name, component);
        if(currentTab == null) currentTab = name;
        width = 0;
        for(String tab : tabMap.keySet()) width += fontRendererObj.getStringWidth(tab) + 8;
    }

    public void setSelectedTab(String name) {
        if(tabMap.containsKey(name)) {
            currentTab = name;
            invokeSelected();
        }
    }

    public String getSelectedTab() {
        if(currentTab != null) return currentTab;
        else return "";
    }

    @Override
    public void mouseClicked(int xM, int yM, int which) {
        if(!isVisible()) return;
        super.mouseClicked(xM, yM, which);
        int cX = xPos;
        boolean tabClicked = false;
        for(String tab : tabMap.keySet()) {
            int length = fontRendererObj.getStringWidth(tab) + 8;
            if(xM > cX && xM < cX+length && yM > yPos && yM < yPos+height) {
                setSelectedTab(tab);
                tabClicked = true;
                break;
            }
            cX += length;
        }
        if(tabClicked) {
            playSelectSound();
            invokeSelected();
        }
        else {
            CustomGUIComponent component = tabMap.get(currentTab);
            if(component != null) component.mouseClicked(xM, yM, which);
        }
    }

    @Override
    public void mouseMovedOrUp(int xM, int yM, int which) {
        CustomGUIComponent component = tabMap.get(currentTab);
        if(component != null) component.mouseMovedOrUp(xM, yM, which);
    }

    @Override
    public void mouseClickMove(int xM, int yM, int which, long dTime) {
        CustomGUIComponent component = tabMap.get(currentTab);
        if(component != null) component.mouseClickMove(xM, yM, which, dTime);
    }

    @Override
    public void mouseWheel(int xM, int yM, int amount) {
        CustomGUIComponent component = tabMap.get(currentTab);
        if(component != null) component.mouseWheel(xM, yM, amount);
    }

    @Override
    public boolean keyTyped(char letter, int key) {
        CustomGUIComponent component = tabMap.get(currentTab);
        if(component != null) return component.keyTyped(letter, key);
        return false;
    }

    @Override
    public void draw(int xM, int yM) {
        if(!isVisible()) return;

        // Draw the selected tab's component first
        CustomGUIComponent comp = tabMap.get(currentTab);
        if(comp != null) comp.draw(xM, yM);

        int cX = xPos;
        int sX = -1;
        for(String tab : tabMap.keySet()) {
            int width = fontRendererObj.getStringWidth(tab) + 8;
            if(currentTab == null || !tab.equalsIgnoreCase(currentTab)) {
                Minecraft.getMinecraft().renderEngine.bindTexture(componentsTexture);
                GL11.glColor3f(1.0f,1.0f,1.0f);
                drawSquare(cX, yPos, width, height, 128, 0, 32, 32);
                /*
                drawTexturedModalRect(cX,                    listYPos,    128,         0,     endPart, hh);
                drawTexturedModalRect(cX+endPart+middlePart, listYPos,    160-endPart, 0,     endPart, hh);
                drawTexturedModalRect(cX,                    listYPos+hh, 128,         32-hh, endPart, hh);
                drawTexturedModalRect(cX+endPart+middlePart, listYPos+hh, 160-endPart, 32-hh, endPart, hh);
                if(middlePart > 0) {
                    drawTexturedModalRect(cX+endPart, listYPos,    130, 0,     middlePart, hh);
                    drawTexturedModalRect(cX+endPart, listYPos+hh, 130, 32-hh, middlePart, hh);
                }
                */
                fontRendererObj.drawStringWithShadow(tab, cX+4, yPos+4, -1);
            } else {
                sX = cX;
            }
            cX += width;
        }
        if(sX >= 0) {
            int width = fontRendererObj.getStringWidth(currentTab) + 8;
            Minecraft.getMinecraft().renderEngine.bindTexture(componentsTexture);
            GL11.glColor3f(1.0f,1.0f,1.0f);
            drawSquare(sX-1, yPos-1, width+2, height+2, 96, 0, 32, 32);
            /*
            drawTexturedModalRect(sX-1,            listYPos-1,  96,         0,       sEnd+1, hh+1);
            drawTexturedModalRect(sX+sEnd+sMiddle, listYPos-1,  128-sEnd-1, 0,       sEnd+1, hh+1);
            drawTexturedModalRect(sX-1,            listYPos+hh, 96,         32-hh-1, sEnd+1, hh+1);
            drawTexturedModalRect(sX+sEnd+sMiddle, listYPos+hh, 128-sEnd-1, 32-hh-1, sEnd+1, hh+1);
            if(sMiddle > 0) {
                drawTexturedModalRect(sX-1+sEnd, listYPos-1,    98, 0,       sMiddle, hh+1);
                drawTexturedModalRect(sX-1+sEnd, listYPos+hh+1, 98, 32-hh-1, sMiddle, hh+1);
            }
            */
            fontRendererObj.drawStringWithShadow(currentTab, sX+4, yPos+4, -1);
        }
    }
}
