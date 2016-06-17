package com.majorpotato.febridge.client.gui.components;


import net.minecraft.client.gui.FontRenderer;

public class ComponentContextMenu extends CustomGUIComponent {

    FontRenderer fontRendererObj;
    String[] menu;

    String response = null;

    public ComponentContextMenu(String[] menu, FontRenderer fontRendererObj) {
        super(0,0,0,0);
        this.fontRendererObj = fontRendererObj;
        setOptions(menu);
    }

    public void setOptions(String[] menu) {
        this.height = 2 + (fontRendererObj.FONT_HEIGHT+4)*menu.length;
        int index = -1;
        int longest = 0;
        for(int i = 0; i < menu.length; i++) if(menu[i].length() > longest) { longest = menu[i].length(); index = i; }
        this.width = 6 + fontRendererObj.getStringWidth(menu[index]);
        this.menu = menu;
    }

    public void setPosition(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public String response() { return response; }

    @Override
    public void mouseClicked(int xM, int yM, int which) {
        if(!isVisible()) return;
        for(int i = 0; i < menu.length; i++) {
            int thisYPos = yPos + 1 + i * (fontRendererObj.FONT_HEIGHT+4);
            if(xM > xPos && xM < xPos+width && yM > thisYPos && yM < thisYPos+fontRendererObj.FONT_HEIGHT+4) {
                response = menu[i];
                hide();
                invokeSelected();
                break;
            }
        }
        response = null;
        hide();
    }

    @Override
    public void draw(int xM, int yM) {
        if(!isVisible()) return;
        drawRect(xPos, yPos, xPos+width, yPos+height, TOOLTIP_COLOR);
        for(int i = 0; i < menu.length; i++) {
            int thisYPos = yPos + 1 + i * (fontRendererObj.FONT_HEIGHT+4);
            if(xM > xPos && xM < xPos+width && yM > thisYPos && yM < thisYPos+fontRendererObj.FONT_HEIGHT+4)
                drawRect(xPos, thisYPos, xPos+width, thisYPos+fontRendererObj.FONT_HEIGHT+4, HOVER_COLOR);
            fontRendererObj.drawStringWithShadow(menu[i], xPos + 3, thisYPos + 2, -1);
        }
    }
}
