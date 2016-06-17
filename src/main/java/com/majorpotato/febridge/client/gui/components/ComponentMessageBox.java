package com.majorpotato.febridge.client.gui.components;


import com.majorpotato.febridge.client.gui.components.events.CEventListener_OnSelected;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class ComponentMessageBox extends CustomGUIComponent implements CEventListener_OnSelected {

    public static final int MESSAGE_BOX_WIDTH = 250;

    protected String[] messageParts;
    protected String message;
    protected FontRenderer fontRendererObj;

    protected ComponentButton acceptButton;


    public ComponentMessageBox(String message, FontRenderer fontRendererObj) {
        super(0,0,MESSAGE_BOX_WIDTH+16,0);
        this.xPos = (screenWidth-width)/2;
        this.fontRendererObj = fontRendererObj;
        setMessage(message); // Sets yPos + height
        this.acceptButton = new ComponentButton(
                xPos + width - fontRendererObj.getStringWidth("OK") - 16,
                yPos + height - fontRendererObj.FONT_HEIGHT - 16,
                "OK", fontRendererObj);
        acceptButton.addEventListener(this);
    }

    public void setMessage(String message) {
        this.message = message;
        String toChop = message;
        ArrayList<String> parts = new ArrayList<String>();
        while(!toChop.isEmpty()) {
            int index = toChop.length();
            int size = fontRendererObj.getStringWidth(toChop);
            while(size > MESSAGE_BOX_WIDTH) {
                index = toChop.lastIndexOf(" ", index-1);
                size = fontRendererObj.getStringWidth(toChop.substring(0,index));
            }
            parts.add(toChop.substring(0,index));
            if(index < toChop.length()-1) toChop = toChop.substring(index+1);
            else toChop = "";
        }
        messageParts = parts.toArray(new String[0]);
        height = 10 + (fontRendererObj.FONT_HEIGHT+2)*messageParts.length + 4 + fontRendererObj.FONT_HEIGHT + 16;
        yPos = (screenHeight-height)/2;
    }

    public String getMessage() { return message; }

    @Override
    public void onComponentSelected(CustomGUIComponent component) {
        if(component == acceptButton) {
            hide();
            invokeSelected();
        }
    }

    @Override
    public void mouseClicked(int xM, int yM, int which) {
        if(!isVisible()) return;

        acceptButton.mouseClicked(xM, yM, which);
    }

    @Override
    public boolean keyTyped(char letter, int key) {
        if(!isVisible()) return false;

        if(key == Keyboard.KEY_RETURN) {
            hide();
            invokeSelected();
            return true;
        }

        return false;
    }

    @Override
    public void draw(int xM, int yM) {
        if(!isVisible()) return;

        Minecraft.getMinecraft().renderEngine.bindTexture(componentsTexture);
        GL11.glColor3f(1.0f,1.0f,1.0f);

        this.drawGradientRect(0, 0, screenWidth, screenHeight, -1072689136, -804253680);

        drawSquare(xPos,yPos,width,height,0,0,32,32);

        acceptButton.draw(xM, yM);

        for(int i = 0; i < messageParts.length; i++) {
            fontRendererObj.drawStringWithShadow(messageParts[i], xPos+8, yPos+8+(fontRendererObj.FONT_HEIGHT+2)*i, -1);
        }
    }
}
