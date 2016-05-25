package com.majorpotato.febridge.client.gui;


import com.majorpotato.febridge.client.gui.components.*;
import com.majorpotato.febridge.client.gui.components.events.CEventListener_OnSelected;
import com.majorpotato.febridge.network.PacketBuilder;
import com.majorpotato.febridge.reference.Reference;
import com.majorpotato.febridge.util.PermCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiPermEditor extends GuiScreen implements CEventListener_OnSelected {
    public static final ResourceLocation texture = new ResourceLocation(Reference.MOD_ID.toLowerCase(), "textures/gui/EditorGUIFramework.png");

    protected static final int LIST_LEFT = 16;
    protected static final int LIST_TOP = 16;
    protected static final int LIST_WIDTH = 100;
    protected static final int LIST_HEIGHT = 200;
    protected static final int SPACING = 50;
    protected static final int TAB_HEIGHT = 24;

    protected EntityPlayer player;

    protected ComponentFrame componentFrame;

    protected TabControl typeTabs;
    protected ComponentList groupList;
    protected ComponentList userList;
    //protected ComponentList dataList;

    protected TabControl groupTabs;
    protected ComponentList groupPermList;
    protected ComponentList groupUserList;

    protected TabControl userTabs;
    protected ComponentList userPermList;
    protected ComponentList userGroupList;

    protected enum TextField {
        NONE,
    }

    protected enum TabStatus {
        GROUPS, GROUPS_WAITING, USERS, USERS_WAITING;
        public boolean isWaiting() {
            return (
                this == GROUPS_WAITING ||
                this == USERS_WAITING
            );
        }
    }
    protected TabStatus tabStatus = TabStatus.GROUPS_WAITING;

    protected enum DataStatus {
        NONE, GROUP, GROUP_WAITING, USER, USER_WAITING;
        public boolean isWaiting() {
            return (
                this == GROUP_WAITING ||
                this == USER_WAITING
            );
        }
    }
    protected DataStatus dataStatus = DataStatus.NONE;


    public GuiPermEditor(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public void initGui() {
        super.initGui();

        tabStatus = TabStatus.GROUPS_WAITING;
        dataStatus = DataStatus.NONE;

        componentFrame = new ComponentFrame();


        // Overall Tabs
        typeTabs = new TabControl(LIST_LEFT, LIST_TOP, fontRendererObj);
        typeTabs.addSelectedListener(this);


        // Build Group Controls
        ComponentFrame groupFrame = new ComponentFrame();

            // Build Group List
            groupList = new ComponentList(LIST_LEFT, LIST_TOP+TAB_HEIGHT, LIST_WIDTH, LIST_HEIGHT, fontRendererObj);
            groupList.addSelectedListener(this);
            groupFrame.addComponent(groupList);

            // Build Group Tabs
            groupTabs = new TabControl(LIST_LEFT+LIST_WIDTH+SPACING, LIST_TOP, fontRendererObj);
            groupPermList = new ComponentList(LIST_LEFT+LIST_WIDTH+SPACING, LIST_TOP+TAB_HEIGHT, LIST_WIDTH*2, LIST_HEIGHT, fontRendererObj);
            groupTabs.addTab("Permissions", groupPermList);
            groupUserList = new ComponentList(LIST_LEFT+LIST_WIDTH+SPACING, LIST_TOP+TAB_HEIGHT, LIST_WIDTH*2, LIST_HEIGHT, fontRendererObj);
            groupTabs.addTab("Users", groupUserList);
            groupFrame.addComponent(groupTabs);

        typeTabs.addTab("Groups", groupFrame);
        PacketBuilder.instance().requestGroups();


        // Build User Controls
        ComponentFrame userFrame = new ComponentFrame();

            // Build User List
            userList = new ComponentList(LIST_LEFT, LIST_TOP+24, LIST_WIDTH, LIST_HEIGHT, fontRendererObj);
            userList.addSelectedListener(this);
            userFrame.addComponent(userList);

            // Build User Tabs
            userTabs = new TabControl(LIST_LEFT+LIST_WIDTH+SPACING, LIST_TOP, fontRendererObj);
            userPermList = new ComponentList(LIST_LEFT+LIST_WIDTH+SPACING, LIST_TOP+TAB_HEIGHT, LIST_WIDTH*2, LIST_HEIGHT, fontRendererObj);
            userTabs.addTab("Permissions", userPermList);
            userGroupList = new ComponentList(LIST_LEFT+LIST_WIDTH+SPACING, LIST_TOP+TAB_HEIGHT, LIST_WIDTH*2, LIST_HEIGHT, fontRendererObj);
            userTabs.addTab("Groups", userGroupList);
            userFrame.addComponent(userTabs);

        typeTabs.addTab("Users", userFrame);


        // Finalize Building
        componentFrame.addComponent(typeTabs);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        switch(tabStatus) {
            case GROUPS_WAITING:
                if(!PermCache.instance().isWaitingForGroups()) {
                    groupList.setEntries(PermCache.instance().getGroups());
                    tabStatus = TabStatus.GROUPS;
                }
                break;
            case USERS_WAITING:
                if(!PermCache.instance().isWaitingForUsers()) {
                    userList.setEntries(PermCache.instance().getUsers());
                    tabStatus = TabStatus.USERS;
                }
                break;
            default: break;
        }

        switch(dataStatus) {
            case GROUP_WAITING:
                if(!PermCache.instance().isWaitingForGroupData()) {
                    groupPermList.setEntries(PermCache.instance().getGroupPerms());
                    groupUserList.setEntries(PermCache.instance().getGroupUsers());
                    dataStatus = DataStatus.GROUP;
                }
                break;
            case USER_WAITING:
                if(!PermCache.instance().isWaitingForUserData()) {
                    userPermList.setEntries(PermCache.instance().getUserPerms());
                    userGroupList.setEntries(PermCache.instance().getUserGroups());
                    dataStatus = DataStatus.USER;
                }
            default: break;
        }
    }

    @Override
    public void onComponentSelected(CustomGUIComponent eventComponent) {
        if(eventComponent == typeTabs) {
            System.out.println("TabSelected");
            String tab = typeTabs.getSelectedTab();
            if(tab.equals("Groups")) {
                System.out.println("GroupsSelected");
                PacketBuilder.instance().requestGroups();
                groupList.clear();
                tabStatus = TabStatus.GROUPS_WAITING;
            } else if(tab.equals("Users")) {
                System.out.println("UsersSelected");
                PacketBuilder.instance().requestUsers();
                userList.clear();
                tabStatus = TabStatus.USERS_WAITING;
            }
        } else if(eventComponent == groupList) {
            String group = groupList.getSelectedEntry();
            if(group != null && group.length() > 0) {
                PacketBuilder.instance().requestGroupData(group);
                groupPermList.clear();
                groupUserList.clear();
                dataStatus = DataStatus.GROUP_WAITING;
            } else {
                groupPermList.clear();
                groupUserList.clear();
                dataStatus = DataStatus.NONE;
            }
        } else if(eventComponent == userList) {
            String user = userList.getSelectedEntry();
            if(user != null && user.length() > 0) {
                PacketBuilder.instance().requestUserData(user);
                userPermList.clear();
                userGroupList.clear();
                dataStatus = DataStatus.USER_WAITING;
            } else {
                userPermList.clear();
                userGroupList.clear();
                dataStatus = DataStatus.NONE;
            }
        }
    }

    @Override
    protected void mouseClicked(int xM, int yM, int which) {

        // Do stuff
        componentFrame.mouseClicked(xM, yM, which);

        super.mouseClicked(xM, yM, which);
    }

    @Override
    // which==-1 is move, otherwise up
    protected void mouseMovedOrUp(int xM, int yM, int which) {

        // Do stuff

        super.mouseMovedOrUp(xM, yM, which);
    }

    @Override
    public void keyTyped(char key, int mode) {

        // Do stuff
        if(!componentFrame.keyTyped(key, mode))
            super.keyTyped(key, mode);
    }

    @Override
    public void drawScreen(int j, int i, float f) {

        this.drawDefaultBackground();

        // Do stuff
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);

        int x = (this.width - 32) / 2;
        int y = (this.height - 16) / 2;

        componentFrame.draw();

        if(tabStatus.isWaiting()) {
            Minecraft.getMinecraft().renderEngine.bindTexture(CustomGUIComponent.componentsTexture);
            GL11.glColor3f(1.0f,1.0f,1.0f);
            drawTexturedModalRect(
                    LIST_LEFT+(LIST_WIDTH-16)/2,
                    LIST_TOP+24+(LIST_HEIGHT-8)/2,
                    0, 40, 16, 8
            );
        }

        if(dataStatus.isWaiting()) {
            Minecraft.getMinecraft().renderEngine.bindTexture(CustomGUIComponent.componentsTexture);
            GL11.glColor3f(1.0f,1.0f,1.0f);
            drawTexturedModalRect(
                    LIST_LEFT+LIST_WIDTH+50+(LIST_WIDTH*2-16)/2,
                    LIST_TOP+24+(LIST_HEIGHT-8)/2,
                    0, 40, 16, 8
            );
        }

        // Draw buttons and labels
        super.drawScreen(j, i, f);
    }
}
