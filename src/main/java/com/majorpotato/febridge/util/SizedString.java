package com.majorpotato.febridge.util;


import net.minecraft.client.gui.FontRenderer;

public class SizedString implements Comparable<Object> {

    protected String actualString;
    protected String displayString;

    protected int width;

    protected FontRenderer fontRendererObj;

    private boolean dirty = false;

    public SizedString(String input, int width, FontRenderer fontRendererObj) {
        this.fontRendererObj = fontRendererObj;
        actualString = input;
        this.width = width;
        dirty = true;
    }

    public void set(String input) {
        actualString = input;
        dirty = true;
    }

    public void setWidth(int width) {
        this.width = width;
        dirty = true;
    }

    public String display() {
        if(dirty) format();
        return displayString;
    }
    public String actual() { return actualString; }

    protected void format() {
        format(fontRendererObj);
    }

    protected void format(FontRenderer fontRendererObj) {
        displayString = actualString;
        int tW = fontRendererObj.getStringWidth(displayString);
        String tS = displayString;
        while(tW > width) {
            displayString = displayString.substring(0,displayString.length()-1);
            tS = displayString + "...";
            tW = fontRendererObj.getStringWidth(tS);
        }
        displayString = tS;
        dirty = false;
    }

    @Override
    public int compareTo(Object other) {
        if(actualString == null) return 0;
        if(other instanceof SizedString) {
            return actualString.compareTo(((SizedString)other).actual());
        } else if(other instanceof String) {
            return actualString.compareTo((String)other);
        } else return 0;
    }
}
