package com.majorpotato.febridge.client.gui.components;


import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

public class ComponentTooltip extends CustomGUIComponent {

    public static final int TOOLTIP_COLOR = 0xDD555555;

    protected String text;
    protected FontRenderer fontRendererObj;

    public ComponentTooltip(String text, FontRenderer fontRendererObj) {
        super(0,0,0,0);
        this.fontRendererObj = fontRendererObj;
        setText(text);
    }

    public void setPosition(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public void setText(String text) {
        this.text = text;
        this.width = 10+fontRendererObj.getStringWidth(text);
        this.height = fontRendererObj.FONT_HEIGHT;
    }

    @Override
    public void draw(int xM, int yM) {
        if(!isVisible()) return;
        drawRect(xPos, yPos, xPos+width, yPos+height, TOOLTIP_COLOR);
        fontRendererObj.drawStringWithShadow(text, xPos+7, yPos+3, -1);
    }
}
