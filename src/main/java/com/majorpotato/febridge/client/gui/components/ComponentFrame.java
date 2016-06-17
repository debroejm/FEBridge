package com.majorpotato.febridge.client.gui.components;


import java.util.ArrayList;

public class ComponentFrame extends CustomGUIComponent {

    ArrayList<CustomGUIComponent> components = new ArrayList<CustomGUIComponent>();

    ComponentMessageBox currentMessage = null;

    public ComponentFrame() {
        super(0,0,0,0);
        this.width = screenWidth;
        this.height = screenHeight;
    }

    public void addComponent(CustomGUIComponent component) {
        if(components.contains(component)) return;
        components.add(component);
    }

    public void showMessageBox(ComponentMessageBox messageBox) {
        messageBox.show();
        currentMessage = messageBox;
        if(currentMessage instanceof ComponentDialogueBox) {
            ((ComponentDialogueBox)currentMessage).clearResponse();
            ((ComponentDialogueBox)currentMessage).textBox.select();
        }
    }

    @Override
    public void draw(int xM, int yM) {
        if(!isVisible()) return;
        for(CustomGUIComponent component : components) component.draw(xM, yM);
        if(currentMessage != null) currentMessage.draw(xM, yM);
    }

    @Override
    public void mouseClicked(int xM, int yM, int which) {
        if(!isVisible()) return;
        if(currentMessage != null && currentMessage.isVisible()) currentMessage.mouseClicked(xM, yM, which);
        else for(CustomGUIComponent component : components) component.mouseClicked(xM, yM, which);
    }

    @Override
    public void mouseMovedOrUp(int xM, int yM, int which) {
        if(!isVisible()) return;
        if(currentMessage != null && currentMessage.isVisible()) currentMessage.mouseMovedOrUp(xM, yM, which);
        else for(CustomGUIComponent component : components) component.mouseMovedOrUp(xM, yM, which);
    }

    @Override
    public void mouseClickMove(int xM, int yM, int which, long dTime) {
        if(!isVisible()) return;
        if(currentMessage != null && currentMessage.isVisible()) currentMessage.mouseClickMove(xM, yM, which, dTime);
        else for(CustomGUIComponent component : components) component.mouseClickMove(xM, yM, which, dTime);
    }

    @Override
    public void mouseWheel(int xM, int yM, int amount) {
        if(!isVisible()) return;
        if(currentMessage != null && currentMessage.isVisible()) currentMessage.mouseWheel(xM, yM, amount);
        else for(CustomGUIComponent component : components) component.mouseWheel(xM, yM, amount);
    }

    @Override
    public boolean keyTyped(char letter, int key) {
        if(!isVisible()) return false;
        if(currentMessage != null && currentMessage.isVisible()) return currentMessage.keyTyped(letter, key);
        else {
            boolean grab = false;
            for (CustomGUIComponent component : components) {
                if (component.keyTyped(letter, key)) grab = true;
            }
            return grab;
        }
    }
}
