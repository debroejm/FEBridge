package com.majorpotato.febridge.client.gui.components;


import com.majorpotato.febridge.client.gui.components.events.CEventListener_OnSelected;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

public class TabControl extends CustomGUIComponentSelectable {

    HashMap<String, CustomGUIComponent> tabMap = new HashMap<String, CustomGUIComponent>();

    protected int xPos, yPos;
    protected String currentTab = null;
    protected FontRenderer fontRendererObj;

    public TabControl(int xPos, int yPos, FontRenderer fontRendererObj) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.fontRendererObj = fontRendererObj;
    }

    public void addTab(String name, CustomGUIComponent component) {
        if(tabMap.containsKey(name)) return;
        tabMap.put(name, component);
        if(currentTab == null) currentTab = name;
    }

    public void setSelectedTab(String name) {
        if(tabMap.containsKey(name)) currentTab = name;
    }

    public String getSelectedTab() {
        if(currentTab != null) return currentTab;
        else return "";
    }

    @Override
    public void mouseClicked(int xM, int yM, int which) {
        if(!isVisible()) return;
        int cX = xPos;
        int height = fontRendererObj.FONT_HEIGHT + 8;
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
        if(tabClicked) select();
        else {
            CustomGUIComponent component = tabMap.get(currentTab);
            if(component != null) component.mouseClicked(xM, yM, which);
        }
    }

    @Override
    public boolean keyTyped(char letter, int key) {
        // NOOP
        return false;
    }

    @Override
    public void draw() {
        if(!isVisible()) return;

        // Draw the selected tab's component first
        CustomGUIComponent comp = tabMap.get(currentTab);
        if(comp != null) comp.draw();

        int cX = xPos;
        int height = fontRendererObj.FONT_HEIGHT + 8;
        int hh = height / 2;
        int sX = -1;
        int sEnd = 0, sMiddle = 0;
        for(String tab : tabMap.keySet()) {
            int length = fontRendererObj.getStringWidth(tab) + 8;
            int endPart = length / 2;
            int middlePart = 0;
            if(endPart > 24) {
                middlePart = (endPart-24)*2;
                endPart = 24;
            }
            if(currentTab == null || !tab.equalsIgnoreCase(currentTab)) {
                Minecraft.getMinecraft().renderEngine.bindTexture(componentsTexture);
                GL11.glColor3f(1.0f,1.0f,1.0f);
                drawTexturedModalRect(cX,                    yPos,    128,         0,     endPart, hh);
                drawTexturedModalRect(cX+endPart+middlePart, yPos,    160-endPart, 0,     endPart, hh);
                drawTexturedModalRect(cX,                    yPos+hh, 128,         32-hh, endPart, hh);
                drawTexturedModalRect(cX+endPart+middlePart, yPos+hh, 160-endPart, 32-hh, endPart, hh);
                if(middlePart > 0) {
                    drawTexturedModalRect(cX+endPart, yPos,    130, 0,     middlePart, hh);
                    drawTexturedModalRect(cX+endPart, yPos+hh, 130, 32-hh, middlePart, hh);
                }
                fontRendererObj.drawStringWithShadow(tab, cX+4, yPos+4, -1);
            } else {
                sX = cX;
                sEnd = endPart;
                sMiddle = middlePart;
            }
            cX += length;
        }
        if(sX >= 0) {
            Minecraft.getMinecraft().renderEngine.bindTexture(componentsTexture);
            drawTexturedModalRect(sX-1,            yPos-1,  96,         0,       sEnd+1, hh+1);
            drawTexturedModalRect(sX+sEnd+sMiddle, yPos-1,  128-sEnd-1, 0,       sEnd+1, hh+1);
            drawTexturedModalRect(sX-1,            yPos+hh, 96,         32-hh-1, sEnd+1, hh+1);
            drawTexturedModalRect(sX+sEnd+sMiddle, yPos+hh, 128-sEnd-1, 32-hh-1, sEnd+1, hh+1);
            if(sMiddle > 0) {
                drawTexturedModalRect(sX-1+sEnd, yPos-1,    98, 0,       sMiddle, hh+1);
                drawTexturedModalRect(sX-1+sEnd, yPos+hh+1, 98, 32-hh-1, sMiddle, hh+1);
            }
            fontRendererObj.drawStringWithShadow(currentTab, sX+4, yPos+4, -1);
        }
    }
}
