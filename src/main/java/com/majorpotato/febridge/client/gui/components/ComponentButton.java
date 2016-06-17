package com.majorpotato.febridge.client.gui.components;


import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class ComponentButton extends CustomGUIComponent {

    protected int texX, texY;
    protected ResourceLocation texture = null;

    boolean textButton = false;

    protected String text = null;
    protected FontRenderer fontRendererObj;

    protected boolean enabled = true;

    public static ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");

    public ComponentButton(int xPos, int yPos, ButtonType type) {
        super(xPos, yPos, type.getWidth(), type.getHeight());
        this.texX = type.getTexX();
        this.texY = type.getTexY();
        this.texture = type.getTexture();
    }

    public ComponentButton(int xPos, int yPos, ResourceLocation texture, int texX, int texY, int width, int height) {
        super(xPos, yPos, width, height);
        this.texture = texture;
        this.texX = texX;
        this.texY = texY;
    }

    public ComponentButton(int xPos, int yPos, String text, FontRenderer fontRendererObj) {
        super(xPos, yPos, fontRendererObj.getStringWidth(text)+8, fontRendererObj.FONT_HEIGHT+8);
        this.text = text;
        this.fontRendererObj = fontRendererObj;
        textButton = true;
    }

    public boolean isEnabled() { return enabled; }
    public void enable() { enabled = true; }
    public void disable() { enabled = false; }

    public String getText() { return text; }


    @Override
    public void draw(int xM, int yM) {
        if(!isVisible()) return;

        GL11.glColor3f(1.0f,1.0f,1.0f);

        // Button Background
        Minecraft.getMinecraft().renderEngine.bindTexture(buttonTextures);
        if(!enabled) drawSquare(xPos, yPos, width, height, 1, 47, 198, 18);
        else if(xM > xPos && xM < xPos+width && yM > yPos && yM < yPos+height) drawSquare(xPos, yPos, width, height, 1, 87, 198, 18);
        else drawSquare(xPos, yPos, width, height, 1, 67, 198, 18);

        // Button Foreground
        if(textButton) {
            fontRendererObj.drawStringWithShadow(text, xPos+4, yPos+4, -1);
        } else {
            Minecraft.getMinecraft().renderEngine.bindTexture(texture);
            drawTexturedModalRect(xPos, yPos, texX, texY, width, height);
        }
    }

    @Override
    public void mouseClicked(int xM, int yM, int which) {
        if(!enabled) return;
        if(xM > xPos && xM < xPos+width && yM > yPos && yM < yPos+height) {
            playSelectSound();
            invokeSelected();
        }
    }

    public static enum ButtonType {

        ADD(CustomGUIComponent.componentsTexture, 0, 64, 16, 16),
        REMOVE(CustomGUIComponent.componentsTexture, 16, 64, 16, 16);

        private ResourceLocation texture;
        private int texX, texY;
        private int width, height;

        private ButtonType(ResourceLocation texture, int texX, int texY, int width, int height) {
            this.texture = texture;
            this.texX = texX;
            this.texY = texY;
            this.width = width;
            this.height = height;
        }

        public ResourceLocation getTexture() { return texture; }
        public int getTexX() { return texX; }
        public int getTexY() { return texY; }
        public int getWidth() { return width; }
        public int getHeight() { return height; }

    }
}
