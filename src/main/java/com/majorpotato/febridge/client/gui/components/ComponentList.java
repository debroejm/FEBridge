package com.majorpotato.febridge.client.gui.components;


import com.majorpotato.febridge.util.SizedString;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ComponentList extends CustomGUIComponent {

    protected int rowSize;

    protected ArrayList<ListEntry> entries = new ArrayList<ListEntry>();
    protected HashMap<String, CustomGUIComponent> componentMap = new HashMap<String, CustomGUIComponent>();

    protected int scrollEntry = 0;
    protected int currentEntryNum = -1;
    protected String currentEntryName = null;

    protected FontRenderer fontRendererObj;

    public ComponentList(int xPos, int yPos, int width, int height, FontRenderer fontRendererObj) {
        super(xPos, yPos, width, height);
        this.fontRendererObj = fontRendererObj;
        rowSize = (height-4) / (fontRendererObj.FONT_HEIGHT+2);
    }

    public ComponentList addEntry(String name, CustomGUIComponent component) {
        return addEntry(new ListEntry(name, width-8, fontRendererObj), component);
    }

    public ComponentList addEntry(ListEntry input, CustomGUIComponent component) {
        for(ListEntry entry : entries) { if(entry.getTextData().actual().equals(input.getTextData().actual())) return this; }
        entries.add(input);
        Collections.sort(entries);
        componentMap.put(input.getTextData().actual(), component);
        return this;
    }

    protected final void autoSetScroll() {
        if(currentEntryNum < 0) {
            scrollEntry = 0;
            return;
        }
        int cMin = scrollEntry;
        int cMax = scrollEntry+rowSize;
        if(currentEntryNum > cMax) scrollEntry += (currentEntryNum-cMax);
        else if(currentEntryNum < cMin) scrollEntry -= (cMin-currentEntryNum);
    }

    public void clear() {
        entries.clear();
        componentMap.clear();
        clearSelected();
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public void clearSelected() {
        currentEntryName = null;
        currentEntryNum = -1;
        scrollEntry = 0;
    }

    public ComponentList setEntries(String[] names) {
        ListEntry[] entryArray = new ListEntry[names.length];
        for(int i = 0; i < names.length; i++) entryArray[i] = new ListEntry(names[i], width-8, fontRendererObj);
        return setEntries(entryArray);
    }

    public ComponentList setEntries(ListEntry[] names) {
        clear();
        for(ListEntry name : names) {
            entries.add(name);
            componentMap.put(name.getTextData().actual(), null);
        }
        Collections.sort(entries);
        return this;
    }

    public void setSelectedEntry(String name) {
        if(name == null) {
            clearSelected();
            invokeSelected();
            return;
        }
        for(int i = 0; i < entries.size(); i++) {
            if(name.equals(entries.get(i).getTextData().actual())) {
                currentEntryName = name;
                currentEntryNum = i;
                autoSetScroll();
                invokeSelected();
                return;
            }
        }
        clearSelected();
        invokeSelected();
    }

    public int size() { return entries.size(); }

    public int getScroll() { return scrollEntry; }
    public ListEntry getSelectedEntry() {
        if(currentEntryNum < 0) return null;
        else return entries.get(currentEntryNum);
    }

    public void setScroll(int scroll) {
        if(scroll >= entries.size()-rowSize) scrollEntry = entries.size()-rowSize;
        else scrollEntry = scroll;
        if(scrollEntry < 0) scrollEntry = 0;
    }

    public ListEntry getEntryAtMouse(int xM, int yM) {
        if(xM > xPos+1 && xM < xPos+width-1 && yM > yPos +3 && yM < yPos + height -3) {
            int num = (yM- yPos -3) / (fontRendererObj.FONT_HEIGHT+2);
            if(scrollEntry+num < entries.size()) {
                return entries.get(scrollEntry+num);
            }
        }
        return null;
    }

    @Override
    public void mouseClicked(int xM, int yM, int which) {
        if(!isVisible()) return;
        if(xM > xPos+1 && xM < xPos+width-1 && yM > yPos +3 && yM < yPos + height -3) {
            int num = (yM- yPos -3) / (fontRendererObj.FONT_HEIGHT+2);
            if(num < rowSize && scrollEntry+num < entries.size()) {
                currentEntryNum = scrollEntry+num;
                currentEntryName = entries.get(currentEntryNum).getTextData().actual();
                playSelectSound();
                if(which == 1 && contextMenu != null) {
                    contextMenu.setPosition(xM, yM);
                    contextMenu.show();
                }
            } else {
                currentEntryNum = -1;
                currentEntryName = null;
            }
            invokeSelected();
        } else if(xM > xPos+width && xM < xPos+width+8 && yM > yPos && yM < yPos + height) {
            setScroll((entries.size()-rowSize) * (yM - yPos) / (height - 32));
        }
    }

    @Override
    public void mouseClickMove(int xM, int yM, int which, long dTime) {
        if(xM > xPos+width && xM < xPos+width+8 && yM > yPos && yM < yPos + height) {
            setScroll((entries.size()-rowSize) * (yM - yPos) / (height - 32));
        }
    }

    @Override
    public void mouseWheel(int xM, int yM, int amount) {
        if(xM > xPos && xM < xPos+width+8 && yM > yPos && yM < yPos+height) {
            setScroll(scrollEntry + (amount/-120));
        }
    }

    @Override
    public void draw(int xM, int yM) {
        if(!isVisible()) return;
        if(currentEntryName != null) {
            CustomGUIComponent comp = componentMap.get(currentEntryName);
            if(comp != null) comp.draw(xM, yM);
        }
        int hoverEntryNum = -1;
        if (xM > xPos + 1 && xM < xPos + width - 1 && yM > yPos + 3 && yM < yPos + height - 3) {
            int num = (yM - yPos - 3) / (fontRendererObj.FONT_HEIGHT + 2);
            int entryNum = scrollEntry + num;
            if (entryNum < entries.size() && entryNum != currentEntryNum) {
                hoverEntryNum = entryNum;
            }
        }
        Minecraft.getMinecraft().renderEngine.bindTexture(componentsTexture);
        GL11.glColor3f(1.0f,1.0f,1.0f);
        drawSquare(xPos, yPos, width, height, 32, 0, 32, 32);
        if(entries.size() > rowSize) {
            int scrollOffset = (height - 32) * scrollEntry / (entries.size()-rowSize);
            drawRect(xPos+width, yPos, xPos+width+8, yPos+height, 0xCC333333);
            GL11.glColor3f(1.0f,1.0f,1.0f);
            drawTexturedModalRect(xPos+width, yPos+scrollOffset,16,32,8,32);
        }
        for(int i = 0; i < rowSize; i++) {
            if(scrollEntry+i >= entries.size()) break;
            if(scrollEntry+i == currentEntryNum)
                entries.get(scrollEntry+i).drawSelect(xPos+1, yPos +3+i*(fontRendererObj.FONT_HEIGHT+2), width-2, fontRendererObj.FONT_HEIGHT+2);
            else if(scrollEntry+i == hoverEntryNum)
                entries.get(scrollEntry+i).drawHover(xPos+1, yPos +3+i*(fontRendererObj.FONT_HEIGHT+2), width-2, fontRendererObj.FONT_HEIGHT+2);
            else
                entries.get(scrollEntry+i).draw(xPos+1, yPos +3+i*(fontRendererObj.FONT_HEIGHT+2), width-2, fontRendererObj.FONT_HEIGHT+2);
        }
    }

    public static class ListEntry implements Comparable<ListEntry> {

        protected SizedString textData;

        protected FontRenderer fontRendererObj;

        public ListEntry(String text, int width, FontRenderer fontRendererObj) {
            textData = new SizedString(text, width, fontRendererObj);
            this.fontRendererObj = fontRendererObj;
        }

        public ListEntry(SizedString ss, FontRenderer fontRendererObj) {
            this.textData = ss;
            this.fontRendererObj = fontRendererObj;
        }

        public SizedString getTextData() { return textData; }

        public void draw(int xPos, int yPos, int width, int height) {
            fontRendererObj.drawStringWithShadow(textData.display(), xPos+3, yPos+1, -1);
        }

        public void drawHover(int xPos, int yPos, int width, int height) {
            Gui.drawRect(xPos, yPos, xPos+width, yPos+height, HOVER_COLOR);
            fontRendererObj.drawStringWithShadow(textData.display(), xPos+3, yPos+1, -1);
        }

        public void drawSelect(int xPos, int yPos, int width, int height) {
            Gui.drawRect(xPos, yPos, xPos+width, yPos+height, SELECT_COLOR);
            fontRendererObj.drawStringWithShadow(textData.display(), xPos+3, yPos+1, SELECT_TEXT_COLOR);
        }

        @Override
        public int compareTo(ListEntry other) {
            if(other == null) return 0;
            return textData.compareTo(((ListEntry)other).getTextData());
        }
    }
}
