package com.majorpotato.febridge.client.gui.components;


import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

public class ComponentDialogueBox extends ComponentMessageBox {

    protected ComponentTextBox textBox;
    protected ComponentButton cancelButton;

    protected boolean canceled = false;

    protected ArrayList<ComponentButton> acceptOptions = new ArrayList<ComponentButton>();
    protected boolean customAcceptOptions = false;
    protected ComponentButton selectedButton = null;

    public ComponentDialogueBox(String message, FontRenderer fontRendererObj) {
        super(message, fontRendererObj);
        this.textBox = new ComponentTextBox(
                xPos+8,
                yPos+height-28-fontRendererObj.FONT_HEIGHT*2,
                MESSAGE_BOX_WIDTH,
                fontRendererObj);
        this.cancelButton = new ComponentButton(
                acceptButton.getXPos() - fontRendererObj.getStringWidth("Cancel") - 16,
                yPos + height - fontRendererObj.FONT_HEIGHT - 16,
                "Cancel", fontRendererObj);
        cancelButton.addEventListener(this);
    }

    public ComponentDialogueBox(String message, FontRenderer fontRendererObj, String[] acceptOptions) {
        super(message, fontRendererObj);
        this.textBox = new ComponentTextBox(
                xPos+8,
                yPos+height-28-fontRendererObj.FONT_HEIGHT*2,
                MESSAGE_BOX_WIDTH,
                fontRendererObj);

        int lastXPos = xPos+width;
        for(String option : acceptOptions) {
            ComponentButton b = new ComponentButton(
                    lastXPos - fontRendererObj.getStringWidth(option) - 16,
                    yPos + height - fontRendererObj.FONT_HEIGHT - 16,
                    option, fontRendererObj);
            b.addEventListener(this);
            this.acceptOptions.add(b);
            lastXPos -= (fontRendererObj.getStringWidth(option) + 16);
        }
        customAcceptOptions = true;
        acceptButton.hide();

        this.cancelButton = new ComponentButton(
                lastXPos - fontRendererObj.getStringWidth("Cancel") - 16,
                yPos + height - fontRendererObj.FONT_HEIGHT - 16,
                "Cancel", fontRendererObj);
        cancelButton.addEventListener(this);
    }

    @Override
    public void setMessage(String message) {
        super.setMessage(message);
        height = 10 + (fontRendererObj.FONT_HEIGHT+2)*messageParts.length + 4 + fontRendererObj.FONT_HEIGHT + 4 + (fontRendererObj.FONT_HEIGHT+8) + 16;
        yPos = (screenHeight-height)/2;
    }

    public String getResponse() { return textBox.getText(); }
    public void clearResponse() { textBox.clear(); }
    public void setResponse(String response) { textBox.setText(response); }

    public boolean isCanceled() { return canceled; }

    public ComponentButton getSelectedButton() {
        return canceled ? cancelButton : customAcceptOptions ? selectedButton : acceptButton;
    }

    @Override
    public void onComponentSelected(CustomGUIComponent component) {
        if(component == acceptButton && !customAcceptOptions) {
            hide();
            canceled = false;
            invokeSelected();
        } else if(component == cancelButton) {
            hide();
            canceled = true;
            invokeSelected();
        } else if(customAcceptOptions) {
            for(ComponentButton button : acceptOptions) {
                if(component == button) {
                    hide();
                    canceled = false;
                    selectedButton = button;
                    invokeSelected();
                    break;
                }
            }
        }
    }

    @Override
    public void mouseClicked(int xM, int yM, int which) {
        if(!isVisible()) return;

        if(customAcceptOptions) {
            for(ComponentButton button : acceptOptions) button.mouseClicked(xM, yM, which);
        } else acceptButton.mouseClicked(xM, yM, which);

        cancelButton.mouseClicked(xM, yM, which);
        textBox.mouseClicked(xM, yM, which);
    }

    @Override
    public boolean keyTyped(char letter, int key) {
        if(!isVisible()) return false;

        if(key == Keyboard.KEY_RETURN) {
            if(customAcceptOptions) {
                if(GuiScreen.isCtrlKeyDown() && acceptOptions.size() > 2) selectedButton = acceptOptions.get(2);
                else if(GuiScreen.isShiftKeyDown() && acceptOptions.size() > 1) selectedButton = acceptOptions.get(1);
                else if(acceptOptions.size() > 0) selectedButton = acceptOptions.get(0);
            }
            hide();
            canceled = false;
            invokeSelected();
            return true;
        }

        return textBox.keyTyped(letter, key);
    }

    @Override
    public void draw(int xM, int yM) {
        if(!isVisible()) return;

        super.draw(xM, yM);

        if(customAcceptOptions) {
            for(ComponentButton button : acceptOptions) button.draw(xM, yM);
        }

        cancelButton.draw(xM, yM);
        textBox.draw(xM, yM);
    }
}
