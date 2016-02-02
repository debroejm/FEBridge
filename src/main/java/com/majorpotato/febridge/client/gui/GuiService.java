package com.majorpotato.febridge.client.gui;


import com.majorpotato.febridge.client.container.ContainerBasic;
import com.majorpotato.febridge.network.PacketBuilder;
import com.majorpotato.febridge.reference.Reference;
import com.majorpotato.febridge.tileentity.ICurrencyService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiService extends GuiContainer {
    public static final ResourceLocation texture = new ResourceLocation(Reference.MOD_ID.toLowerCase(), "textures/gui/ShopGUITemplate.png");

    protected static final int TEXT_LEFT = 43;
    protected static final int TEXT_TOP_1 = 29;
    protected static final int TEXT_TOP_2 = 52;

    protected enum TextField {
        NONE, BUYPRICE, SELLPRICE
    }
    protected TextField whichText = TextField.NONE;
    protected String textBuyPrice = "";
    protected String textSellPrice = "";
    protected int buyPrice = 0;
    protected int sellPrice = 0;

    protected EntityPlayer player;
    protected ICurrencyService service;

    protected GuiService(Container container, EntityPlayer player, ICurrencyService service) {
        super(container);

        xSize = 176;
        ySize = 166;

        this.player = player;
        this.service = service;

        this.buyPrice = service.getBuyPrice();
        this.sellPrice = service.getSellPrice();

        this.textBuyPrice = buyPrice+"";
        this.textSellPrice = sellPrice+"";
    }

    public GuiService(EntityPlayer player, ICurrencyService service) {
        this(new ContainerBasic(player.inventory), player, service);
    }

    @Override
    protected void mouseClicked(int xM, int yM, int which) {
        super.mouseClicked(xM, yM, which);

        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;

        TextField oldField = whichText;

        if(xM > x+TEXT_LEFT && yM > y+TEXT_TOP_1 && xM < x+TEXT_LEFT+80 && yM < y+TEXT_TOP_1+16) {
            whichText = TextField.BUYPRICE;
        } else if(xM > x+TEXT_LEFT && yM > y+TEXT_TOP_2 && xM < x+TEXT_LEFT+80 && yM < y+TEXT_TOP_2+16) {
            whichText = TextField.SELLPRICE;
        } else {
            whichText = TextField.NONE;
        }

        if(oldField != whichText) {
            updateServiceDataFields();
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        updateServiceDataFields();
    }

    protected void updateServiceDataFields() {
        try { buyPrice = Integer.parseInt(textBuyPrice); }
        catch(Exception ex) { buyPrice = 0; }
        service.setBuyPrice(buyPrice);
        try { sellPrice = Integer.parseInt(textSellPrice); }
        catch(Exception ex) { sellPrice = 0; }
        service.setSellPrice(sellPrice);
        service.markDirty();
        PacketBuilder.instance().sendServiceDataChangePacket(service);
    }

    @Override
    public void keyTyped(char key, int mode) {
        boolean stop = true;

        int code = (int)key;

        switch(whichText) {
            case BUYPRICE:
                if(code >= 48 && code <= 57) {
                    textBuyPrice = textBuyPrice + (code - 48);
                } else if(code == 8 && textBuyPrice.length() > 0) {
                    textBuyPrice = textBuyPrice.substring(0,textBuyPrice.length()-1);
                } else stop = false;
                break;
            case SELLPRICE:
                if(code >= 48 && code <= 57) {
                    textSellPrice = textSellPrice + (code - 48);
                } else if(code == 8 && textSellPrice.length() > 0) {
                    textSellPrice = textSellPrice.substring(0,textSellPrice.length()-1);
                } else stop = false;
                break;
            default:
                stop = false;
                break;
        }
        if(!stop) super.keyTyped(key, mode);
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float f, int j, int i) {
        GL11.glColor4f(1F, 1F, 1F, 1F);

        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);

        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;


        // Text Fields
        switch(whichText) {
            case BUYPRICE:
                if(service.useBuyPrice()) drawTexturedModalRect(x+TEXT_LEFT, y+TEXT_TOP_1, 176, 16, 80, 16);
                if(service.useSellPrice()) drawTexturedModalRect(x+TEXT_LEFT, y+TEXT_TOP_2, 176, 0, 80, 16);
                break;
            case SELLPRICE:
                if(service.useBuyPrice()) drawTexturedModalRect(x+TEXT_LEFT, y+TEXT_TOP_1, 176, 0, 80, 16);
                if(service.useSellPrice()) drawTexturedModalRect(x+TEXT_LEFT, y+TEXT_TOP_2, 176, 16, 80, 16);
                break;
            default:
                if(service.useBuyPrice()) drawTexturedModalRect(x+TEXT_LEFT, y+TEXT_TOP_1, 176, 0, 80, 16);
                if(service.useSellPrice()) drawTexturedModalRect(x+TEXT_LEFT, y+TEXT_TOP_2, 176, 0, 80, 16);
                break;
        }
    }

    @Override
    public void drawGuiContainerForegroundLayer(int j, int i) {
        GL11.glColor4f(1F, 1F, 1F, 1F);

        // Text
        drawCenteredString(fontRendererObj, service.getServiceName(), xSize/2,10, -1);
        if(service.useBuyPrice()) fontRendererObj.drawStringWithShadow("Buy", 15, TEXT_TOP_1+4, -1);
        if(service.useSellPrice()) fontRendererObj.drawStringWithShadow("Sell", 15, TEXT_TOP_2+4, -1);
        if(service.useBuyPrice()) fontRendererObj.drawStringWithShadow(textBuyPrice, TEXT_LEFT+4, TEXT_TOP_1+4, -1);
        if(service.useSellPrice()) fontRendererObj.drawStringWithShadow(textSellPrice, TEXT_LEFT+4, TEXT_TOP_2+4, -1);
    }
}
