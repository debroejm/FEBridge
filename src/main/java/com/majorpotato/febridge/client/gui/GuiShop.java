package com.majorpotato.febridge.client.gui;


import com.majorpotato.febridge.client.container.ContainerShop;
import com.majorpotato.febridge.reference.Reference;
import com.majorpotato.febridge.tileentity.TileEntityShop;
import com.majorpotato.febridge.util.FormatHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiShop extends GuiService {
    public static final ResourceLocation texture = new ResourceLocation(Reference.MOD_ID.toLowerCase(), "textures/gui/ShopGUITemplate.png");

    public static final int SLOT_LEFT = 139;
    public static final int SLOT_TOP = 28;

    public GuiShop(EntityPlayer player, TileEntityShop shop) {
        super(new ContainerShop(player.inventory, shop), player, shop);
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float f, int j, int i) {
        super.drawGuiContainerBackgroundLayer(f, j, i);

        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;

        // Slot
        drawTexturedModalRect(x+SLOT_LEFT, y+SLOT_TOP, 176, 32, 18, 18);
    }

    @Override
    public void drawGuiContainerForegroundLayer(int j, int i) {
        super.drawGuiContainerForegroundLayer(j, i);

        // Text
        drawCenteredString(fontRendererObj, FormatHelper.simplifyNumberFormat(((TileEntityShop)service).getItemCount()), SLOT_LEFT+9, SLOT_TOP+25, -1);
    }
}
