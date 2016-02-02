package com.majorpotato.febridge.client.gui;


import cpw.mods.fml.client.IModGuiFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.util.Set;

public class GuiFactory implements IModGuiFactory {

    @Override
    public void initialize(Minecraft minecraft) {

    }

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() { return MyGuiConfig.class; }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() { return null; }

    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement runtimeOptionCategoryElement) {
        return null;
    }
}
