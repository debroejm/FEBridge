package com.majorpotato.febridge.client.gui.components;


import com.majorpotato.febridge.client.gui.components.events.CEventListener_OnChanged;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;

public class ComponentFilteredList extends ComponentList implements CEventListener_OnChanged {

    protected ComponentTextBox filterBox;
    
    protected int totalHeight = 0;
    protected int filterYPos = 0;

    protected ArrayList<ListEntry> allEntries = new ArrayList<ListEntry>();
    
    public ComponentFilteredList(int xPos, int yPos, int width, int height, FontRenderer fontRendererObj) {
        super(xPos, yPos+fontRendererObj.FONT_HEIGHT+12, width, height-fontRendererObj.FONT_HEIGHT-12, fontRendererObj);
        filterYPos = yPos;
        totalHeight = height;
        filterBox = new ComponentTextBox(xPos, filterYPos, width, fontRendererObj);
        filterBox.addEventListener(this);
    }

    public ComponentTextBox getFilterBox() { return filterBox; }

    @Override
    public void clear() {
        allEntries.clear();
        filterBox.clear();
        super.clear();
    }
    
    @Override
    public void mouseClicked(int xM, int yM, int which) {
        if(!isVisible()) return;

        filterBox.mouseClicked(xM, yM, which);
        
        super.mouseClicked(xM, yM, which);
    }

    @Override
    public boolean keyTyped(char letter, int key) {
        if(!isVisible()) return false;

        return filterBox.keyTyped(letter, key);
    }

    @Override
    public void onComponentChanged(CustomGUIComponent eventComponent) {
        if(eventComponent == filterBox) {
            entries.clear();
            for(ListEntry entry : allEntries) {
                if(entry.getTextData().actual().toLowerCase().contains(filterBox.getText().toLowerCase())) entries.add(entry);
            }

            if(currentEntryName != null) {
                currentEntryNum = -1;
                for(int i = 0; i < entries.size(); i++) {
                    if(entries.get(i).getTextData().actual().equals(currentEntryName)) {
                        currentEntryNum = i;
                        break;
                    }
                }
                if(currentEntryNum < 0) currentEntryName = null;
            }

            autoSetScroll();

            invokeChanged();
        }
    }

    @Override
    public void draw(int xM, int yM) {
        if(!isVisible()) return;

        filterBox.draw(xM, yM);

        super.draw(xM, yM);
    }

    @Override
    public ComponentList addEntry(ListEntry input, CustomGUIComponent component) {
        for(ListEntry entry : allEntries) { if(entry.getTextData().actual().equals(input.getTextData().actual())) return this; }
        allEntries.add(input);
        Collections.sort(allEntries);
        componentMap.put(input.getTextData().actual(), component);
        onComponentChanged(filterBox);
        return this;
    }

    @Override
    public ComponentList setEntries(ListEntry[] names) {
        clear();
        for(ListEntry name : names) {
            if(name == null) continue;
            allEntries.add(name);
            componentMap.put(name.getTextData().actual(), null);
        }
        Collections.sort(allEntries);
        onComponentChanged(filterBox);
        return this;
    }
}
