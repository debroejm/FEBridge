package com.majorpotato.febridge.client.gui.components;


import com.majorpotato.febridge.client.gui.components.events.CEventListener_OnSelected;

import java.util.ArrayList;

public abstract class CustomGUIComponentSelectable extends CustomGUIComponent {

    protected boolean selected;

    protected ArrayList<CEventListener_OnSelected> listeners_selected = new ArrayList<CEventListener_OnSelected>();

    public boolean isSelected() { return selected; }
    public void setSelected(boolean val) { selected = val; if(selected) informListeners_selected(); }
    public void select() { selected = true; informListeners_selected(); }

    public void addSelectedListener(CEventListener_OnSelected listener) {
        if(!listeners_selected.contains(listener)) listeners_selected.add(listener);
    }

    protected void informListeners_selected() {
        for(CEventListener_OnSelected listener : listeners_selected) listener.onComponentSelected(this);
    }
}
