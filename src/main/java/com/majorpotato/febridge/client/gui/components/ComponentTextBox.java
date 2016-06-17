package com.majorpotato.febridge.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class ComponentTextBox extends CustomGUIComponent {

    protected FontRenderer fontRendererObj;

    protected boolean selected = false;

    protected String text = "";
    protected int typingIndex = 0;

    public ComponentTextBox(int xPos, int yPos, int width, FontRenderer fontRendererObj) {
        super(xPos, yPos, width, fontRendererObj.FONT_HEIGHT+8);
        this.fontRendererObj = fontRendererObj;
    }

    public void clear() {
        text = "";
        typingIndex = 0;
        invokeChanged();
    }

    public boolean isEmpty() {
        return text.isEmpty();
    }

    public void setText(String text) { this.text = text; invokeChanged(); }
    public String getText() { return text; }

    public void select() {
        selected = true;
        invokeSelected();
    }

    @Override
    public String toString() { return getText(); }

    @Override
    public boolean isSelected() { return selected; }

    @Override
    public void mouseClicked(int xM, int yM, int which) {
        if(!isVisible()) return;
        super.mouseClicked(xM, yM, which);

        if(xM > xPos && xM < xPos+width && yM > yPos && yM < yPos+getHeight()) {
            selected = true;
            invokeSelected();
        } else selected = false;
    }

    @Override
    public boolean keyTyped(char letter, int key) {
        if(!isVisible()) return false;

        if(isSelected()) {
            if(letter >= 32 && letter <= 126) {
                text = text.substring(0,typingIndex) + letter + text.substring(typingIndex);
                typingIndex++;
                invokeChanged();
                return true;
            } else {
                switch(key) {
                    case Keyboard.KEY_DELETE:
                        if(typingIndex < text.length()) {
                            if (typingIndex == text.length() - 1) text = text.substring(0, typingIndex);
                            else text = text.substring(0, typingIndex) + text.substring(typingIndex + 1);
                            invokeChanged();
                        }
                        return true;
                    case Keyboard.KEY_BACK:
                        if(typingIndex > 0) {
                            text = text.substring(0,typingIndex-1) + text.substring(typingIndex);
                            typingIndex--;
                            invokeChanged();
                        }
                        return true;
                    case Keyboard.KEY_END:
                        typingIndex = text.length();
                        return true;
                    case Keyboard.KEY_HOME:
                        typingIndex = 0;
                        return true;
                    case Keyboard.KEY_RIGHT:
                        if(typingIndex < text.length()) typingIndex++;
                        return true;
                    case Keyboard.KEY_LEFT:
                        if(typingIndex > 0) typingIndex--;
                        return true;
                    case Keyboard.KEY_RETURN:
                        selected = false;
                        return true;
                    default:
                        return false;
                }
            }
        } else return false;
    }

    @Override
    public void draw(int xM, int yM) {
        if(!isVisible()) return;

        Minecraft.getMinecraft().renderEngine.bindTexture(componentsTexture);
        GL11.glColor3f(1.0f,1.0f,1.0f);

        if(isSelected()) drawSquare(xPos, yPos, width, height, 64, 0, 32, 32);
        else drawSquare(xPos, yPos, width, height, 32, 0, 32, 32);

        fontRendererObj.drawStringWithShadow(text, xPos+4, yPos+4, -1);
        if(isSelected()) fontRendererObj.drawStringWithShadow("_", xPos+4+fontRendererObj.getStringWidth(text.substring(0,typingIndex)), yPos+6, 0xFFFFFB00);
    }
}
