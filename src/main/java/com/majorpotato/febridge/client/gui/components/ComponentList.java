package com.majorpotato.febridge.client.gui.components;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ComponentList extends CustomGUIComponentSelectable {

    protected int xPos, yPos, width, height;
    protected int rowSize;

    protected ArrayList<String> entries = new ArrayList<String>();
    protected HashMap<String, CustomGUIComponent> componentMap = new HashMap<String, CustomGUIComponent>();

    protected int scrollEntry = 0;
    protected int currentEntryNum = -1;
    protected String currentEntryName = null;

    protected FontRenderer fontRendererObj;

    public ComponentList(int xPos, int yPos, int width, int height, FontRenderer fontRendererObj) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.height = height;
        this.fontRendererObj = fontRendererObj;
        rowSize = (height-4) / (fontRendererObj.FONT_HEIGHT+2);
    }

    public ComponentList addEntry(String name, CustomGUIComponent component) {
        if(entries.contains(name)) return this;
        entries.add(name);
        Collections.sort(entries);
        componentMap.put(name, component);
        return this;
    }

    public void clear() {
        entries.clear();
        componentMap.clear();
        scrollEntry = 0;
        currentEntryNum = -1;
        currentEntryName = null;
    }

    public ComponentList setEntries(String[] names) {
        clear();
        for(String name : names) {
            entries.add(name);
            componentMap.put(name, null);
            Collections.sort(entries);
        }
        return this;
    }

    public int size() { return entries.size(); }

    public int getScroll() { return scrollEntry; }
    public String getSelectedEntry() { return currentEntryName; }

    public void setScroll(int scroll) {
        if(scroll < 0) scrollEntry = 0;
        else if(scroll >= entries.size()) scrollEntry = entries.size()-1;
        else scrollEntry = scroll;
    }

    @Override
    public void mouseClicked(int xM, int yM, int which) {
        if(!isVisible()) return;
        if(xM > xPos+4 && xM < xPos+width-4 && yM > yPos+4 && yM < yPos+height-4) {
            int num = (yM-yPos-4) / (fontRendererObj.FONT_HEIGHT+2);
            if(scrollEntry+num < entries.size()) {
                currentEntryNum = scrollEntry+num;
                currentEntryName = entries.get(currentEntryNum);
            } else {
                currentEntryNum = -1;
                currentEntryName = null;
            }
            select();
        } else if(xM > xPos+width && xM < xPos+width+8 && yM > yPos && yM < yPos+height) {
            setScroll((entries.size()-1) * (yM-height-16) / (height-32));
        }
    }

    @Override
    public void draw() {
        if(!isVisible()) return;
        if(currentEntryName != null) {
            CustomGUIComponent comp = componentMap.get(currentEntryName);
            if(comp != null) comp.draw();
        }
        Minecraft.getMinecraft().renderEngine.bindTexture(componentsTexture);
        GL11.glColor3f(1.0f,1.0f,1.0f);
        drawSquare(xPos, yPos, width, height, 32, 0, 32, 32);
        int scrollOffset = 0;
        if(entries.size() > 1) scrollOffset = (height-32) * scrollEntry / (entries.size()-1);
        drawTexturedModalRect(xPos+width,yPos+scrollOffset,32,32,8,32);
        for(int i = 0; i < rowSize; i++) {
            if(scrollEntry+i >= entries.size()) break;
            if(scrollEntry+i == currentEntryNum) {
                fontRendererObj.drawStringWithShadow(entries.get(scrollEntry+i), xPos+4, yPos+4+i*(fontRendererObj.FONT_HEIGHT+2), 0xFFFFFB00);
            } else {
                fontRendererObj.drawStringWithShadow(entries.get(scrollEntry+i), xPos+4, yPos+4+i*(fontRendererObj.FONT_HEIGHT+2), -1);
            }
        }
    }

    @Override
    public boolean keyTyped(char letter, int key) {
        // NOOP
        return false;
    }
}
