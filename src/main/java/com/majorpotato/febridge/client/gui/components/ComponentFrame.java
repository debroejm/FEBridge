package com.majorpotato.febridge.client.gui.components;


import java.util.ArrayList;

public class ComponentFrame extends CustomGUIComponent {

    ArrayList<CustomGUIComponent> components = new ArrayList<CustomGUIComponent>();

    public void addComponent(CustomGUIComponent component) {
        if(components.contains(component)) return;
        components.add(component);
    }

    @Override
    public void draw() {
        if(!isVisible()) return;
        for(CustomGUIComponent component : components) component.draw();
    }

    @Override
    public void mouseClicked(int xM, int yM, int which) {
        if(!isVisible()) return;
        for(CustomGUIComponent component : components) component.mouseClicked(xM, yM, which);
    }

    @Override
    public boolean keyTyped(char letter, int key) {
        if(!isVisible()) return false;
        boolean grab = false;
        for(CustomGUIComponent component : components) {
            if(component.keyTyped(letter, key)) grab = true;
        }
        return grab;
    }
}
