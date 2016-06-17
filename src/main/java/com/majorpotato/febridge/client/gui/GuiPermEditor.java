package com.majorpotato.febridge.client.gui;


import com.majorpotato.febridge.client.gui.components.*;
import com.majorpotato.febridge.client.gui.components.events.CEventListener_OnChanged;
import com.majorpotato.febridge.client.gui.components.events.CEventListener_OnSelected;
import com.majorpotato.febridge.network.PacketBuilder;
import com.majorpotato.febridge.network.packet.PacketRequest;
import com.majorpotato.febridge.reference.Reference;
import com.majorpotato.febridge.util.cache.CacheType;
import com.majorpotato.febridge.util.cache.CategoryType;
import com.majorpotato.febridge.util.cache.PermCacheClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

public class GuiPermEditor extends GuiScreen implements CEventListener_OnChanged, CEventListener_OnSelected {
    public static final ResourceLocation texture = new ResourceLocation(Reference.MOD_ID.toLowerCase(), "textures/gui/EditorGUIFramework.png");

    protected static final int LIST_LEFT = 16;
    protected static final int LIST_TOP = 16;
    protected static final int LIST_WIDTH = 100;
    protected static final int LIST_HEIGHT = 200;
    protected static final int SPACING = 50;
    protected static final int TAB_HEIGHT = 24;

    protected EntityPlayer player;

    protected ComponentFrame componentFrame;

    // Components For: Data Type
    protected ComponentTabs typeTabs;
    //protected ComponentList typeList;
    protected ComponentFilteredList typeList;
    protected String lastSelectEntry_Type = null;
    protected String targetSelectEntry_type = null;
    protected ComponentButton typeButton_add;
    protected ComponentButton typeButton_remove;

    // Components For: Filtering Data (Searching)
    //protected ComponentTextBox filterText;

    // Components For: Data Values
    protected ComponentTabs groupTabs;
    protected ComponentTabs userTabs;
    //protected ComponentList dataList;
    protected ComponentFilteredList dataList;
    protected HashMap<String, String> dataValueMap = null;
    protected long lastSelectTime_Data = 0;
    protected String lastSelectEntry_Data = null;
    protected ComponentButton dataButton_add;
    protected ComponentButton dataButton_remove;
    protected ComponentButton dataButton_edit;

    // Components For: Dialogues
    protected ComponentMessageBox simpleMessage;
    protected ComponentDialogueBox dialogue_addGroup;
    protected ComponentDialogueBox dialogue_addGroupToUser;
    protected ComponentDialogueBox dialogue_addUserToGroup;
    protected ComponentDialogueBox dialogue_permissionEdit;
    protected ComponentDialogueBox dialogue_permissionEditValue;

    // Components For: Tooltip
    protected ComponentTooltip hoverText;
    protected ComponentContextMenu permEditMenu;

    protected enum TypeStatus {
        GROUPS,
        GROUPS_WAITING,
        USERS,
        USERS_WAITING;

        public boolean isWaiting() {
            switch(this) {
                case GROUPS_WAITING:
                case USERS_WAITING:
                    return true;
                default: return false;
            }
        }

        public TypeStatus stopWaiting() {
            switch(this) {
                case GROUPS_WAITING: return GROUPS;
                case USERS_WAITING: return USERS;
                default: return this;
            }
        }

        public TypeStatus startWaiting() {
            switch(this) {
                case GROUPS: return GROUPS_WAITING;
                case USERS: return USERS_WAITING;
                default: return this;
            }
        }
    }
    protected TypeStatus typeStatus = TypeStatus.GROUPS_WAITING;

    protected enum DataStatus {
        NONE,
        GROUP_USERS,
        GROUP_USERS_WAITING,
        GROUP_PERMS,
        GROUP_PERMS_WAITING,
        USER_GROUPS,
        USER_GROUPS_WAITING,
        USER_PERMS,
        USER_PERMS_WAITING;

        public boolean isWaiting() {
            switch(this) {
                case GROUP_USERS_WAITING:
                case GROUP_PERMS_WAITING:
                case USER_GROUPS_WAITING:
                case USER_PERMS_WAITING:
                    return true;
                default: return false;
            }
        }

        public DataStatus stopWaiting() {
            switch(this) {
                case GROUP_USERS_WAITING: return GROUP_USERS;
                case GROUP_PERMS_WAITING: return GROUP_PERMS;
                case USER_GROUPS_WAITING: return USER_GROUPS;
                case USER_PERMS_WAITING: return USER_PERMS;
                default: return this;
            }
        }

        public DataStatus startWaiting() {
            switch(this) {
                case GROUP_USERS: return GROUP_USERS_WAITING;
                case GROUP_PERMS: return GROUP_PERMS_WAITING;
                case USER_GROUPS: return USER_GROUPS_WAITING;
                case USER_PERMS: return USER_PERMS_WAITING;
                default: return this;
            }
        }
    }
    protected DataStatus dataStatus = DataStatus.NONE;


    public GuiPermEditor(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public void initGui() {
        super.initGui();

        lastSelectTime_Data = Minecraft.getSystemTime();

        typeStatus = TypeStatus.GROUPS_WAITING;
        dataStatus = DataStatus.NONE;

        componentFrame = new ComponentFrame();


        // Type List : Groups, Users
        //typeList = new ComponentList(
        typeList = new ComponentFilteredList(
                LIST_LEFT,                              // X
                LIST_TOP+TAB_HEIGHT,                    // Y
                LIST_WIDTH,                             // Width
                LIST_HEIGHT,                            // Height
                fontRendererObj);
        typeList.addEventListener(this);
        componentFrame.addComponent(typeList);

        // Type Buttons
        typeButton_add = new ComponentButton(
                LIST_LEFT+LIST_WIDTH+4,                 // X
                LIST_TOP+TAB_HEIGHT,                    // Y
                ComponentButton.ButtonType.ADD);
        typeButton_add.addEventListener(this);
        typeButton_add.disable();
        componentFrame.addComponent(typeButton_add);
        typeButton_remove = new ComponentButton(
                LIST_LEFT+LIST_WIDTH+4,                 // X
                LIST_TOP+TAB_HEIGHT+20,                 // Y
                ComponentButton.ButtonType.REMOVE);
        typeButton_remove.addEventListener(this);
        componentFrame.addComponent(typeButton_remove);

        // Data List : Permissions, subLists
        //dataList = new ComponentList(
        dataList = new ComponentFilteredList(
                LIST_LEFT+LIST_WIDTH+SPACING,           // X
                LIST_TOP+TAB_HEIGHT,//+TEXT_HEIGHT,     // Y
                LIST_WIDTH*2,                           // Width
                LIST_HEIGHT,                            // Height
                fontRendererObj);
        dataList.addEventListener(this);
        componentFrame.addComponent(dataList);

        // Data Buttons
        dataButton_add = new ComponentButton(
                LIST_LEFT+LIST_WIDTH*3+SPACING+4,       // X
                LIST_TOP+TAB_HEIGHT,                    // Y
                ComponentButton.ButtonType.ADD);
        dataButton_add.addEventListener(this);
        dataButton_add.disable();
        componentFrame.addComponent(dataButton_add);
        dataButton_remove = new ComponentButton(
                LIST_LEFT+LIST_WIDTH*3+SPACING+4,       // X
                LIST_TOP+TAB_HEIGHT+20,                 // Y
                ComponentButton.ButtonType.REMOVE);
        dataButton_remove.addEventListener(this);
        componentFrame.addComponent(dataButton_remove);
        dataButton_edit = new ComponentButton(
                LIST_LEFT+LIST_WIDTH*3+SPACING+4,       // X
                LIST_TOP+TAB_HEIGHT+40,                 // Y
                "EDIT",
                fontRendererObj);
        dataButton_edit.addEventListener(this);
        dataButton_edit.disable();
        componentFrame.addComponent(dataButton_edit);

        // Tybe Tabs
        typeTabs = new ComponentTabs(LIST_LEFT, LIST_TOP, fontRendererObj);
        typeTabs.addEventListener(this);

        // Build Group Tabs
        groupTabs = new ComponentTabs(
                LIST_LEFT+LIST_WIDTH+SPACING,       // X
                LIST_TOP,                           // Y
                fontRendererObj);
        groupTabs.addEventListener(this);
        groupTabs.addTab("Permissions", null);
        groupTabs.addTab("Users", null);

        typeTabs.addTab("Groups", groupTabs);

        // Build User Tabs
        userTabs = new ComponentTabs(
                LIST_LEFT+LIST_WIDTH+SPACING,       // X
                LIST_TOP,                           // Y
                fontRendererObj);
        userTabs.addEventListener(this);
        userTabs.addTab("Permissions", null);
        userTabs.addTab("Groups", null);

        typeTabs.addTab("Users", userTabs);

        // Build Dialogue Boxes
        simpleMessage = new ComponentMessageBox("", fontRendererObj);
        simpleMessage.addEventListener(this);
        simpleMessage.hide();
        dialogue_addGroup = new ComponentDialogueBox("Enter Name of Group to Create:", fontRendererObj);
        dialogue_addGroup.addEventListener(this);
        dialogue_addGroup.hide();
        dialogue_addGroupToUser = new ComponentDialogueBox("Enter Name of Group to put User In:", fontRendererObj);
        dialogue_addGroupToUser.addEventListener(this);
        dialogue_addGroupToUser.hide();
        dialogue_addUserToGroup = new ComponentDialogueBox("Enter Name of User to put in this Group:", fontRendererObj);
        dialogue_addUserToGroup.addEventListener(this);
        dialogue_addUserToGroup.hide();
        dialogue_permissionEdit = new ComponentDialogueBox("Enter a Permission Node:", fontRendererObj, new String[]{ "Allow", "Deny", "Set Value" });
        dialogue_permissionEdit.addEventListener(this);
        dialogue_permissionEdit.hide();
        dialogue_permissionEditValue = new ComponentDialogueBox("Enter Value for Permission:", fontRendererObj);
        dialogue_permissionEditValue.addEventListener(this);
        dialogue_permissionEditValue.hide();


        // Finalize Tabs
        componentFrame.addComponent(typeTabs);

        // Tooltips / Context Menus
        hoverText = new ComponentTooltip("", fontRendererObj);
        hoverText.hide();
        componentFrame.addComponent(hoverText);
        permEditMenu = new ComponentContextMenu(new String[]{ "Clear", "Edit" }, fontRendererObj);
        permEditMenu.hide();
        permEditMenu.addEventListener(this);
        dataList.setContextMenu(permEditMenu);
        componentFrame.addComponent(permEditMenu);


        hideDataControls();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        if(typeStatus.isWaiting()) {
            CategoryType categoryType = null;
            switch(typeStatus) {
                case GROUPS_WAITING:
                    categoryType = CategoryType.GROUP;
                    break;
                case USERS_WAITING:
                    categoryType = CategoryType.USER;
                    break;
                default: break; // Should never happen; will throw NullPointerException
            }
            PermCacheClient.CacheData data = PermCacheClient.instance().get(CacheType.GLOBAL_LISTING, categoryType, null);
            if(data instanceof PermCacheClient.CacheDataList) {
                setTypeList(((PermCacheClient.CacheDataList)data).list);
                typeStatus = typeStatus.stopWaiting();
                if(typeStatus == TypeStatus.GROUPS) typeButton_add.enable();
            }
        }

        if(dataStatus.isWaiting()) {
            CacheType cacheType = null;
            CategoryType categoryType = null;
            ComponentList.ListEntry typeEntry = typeList.getSelectedEntry();
            String name = null;
            if(typeEntry != null) name = typeEntry.getTextData().actual();
            switch(dataStatus) {
                case GROUP_USERS_WAITING:
                    cacheType = CacheType.LOCAL_LISTING_USER;
                    categoryType = CategoryType.GROUP;
                    break;
                case GROUP_PERMS_WAITING:
                    cacheType = CacheType.PERMISSIONS;
                    categoryType = CategoryType.GROUP;
                    break;
                case USER_GROUPS_WAITING:
                    cacheType = CacheType.LOCAL_LISTING_GROUP;
                    categoryType = CategoryType.USER;
                    break;
                case USER_PERMS_WAITING:
                    cacheType = CacheType.PERMISSIONS;
                    categoryType = CategoryType.USER;
                    break;
                default: break; // Should never happen; will throw NullPointerException
            }
            //PermCacheClient.CacheData data = PermCacheClient.instance().filter(cacheType, categoryType, name, filterText.getText());
            PermCacheClient.CacheData data = PermCacheClient.instance().get(cacheType, categoryType, name);
            if(data instanceof PermCacheClient.CacheDataList) {
                setDataList(((PermCacheClient.CacheDataList)data).list);
                dataStatus = dataStatus.stopWaiting();
                if(dataStatus == DataStatus.GROUP_USERS) {
                    if(typeList.getSelectedEntry() instanceof ListEntrySpecial) dataButton_add.disable();
                    else dataButton_add.enable();
                } else dataButton_add.enable();
                dataButton_edit.hide();
            } else if(data instanceof PermCacheClient.CacheDataPermissions) {
                setDataList(((PermCacheClient.CacheDataPermissions)data).permissions, ((PermCacheClient.CacheDataPermissions)data).values);
                dataStatus = dataStatus.stopWaiting();
                dataButton_add.enable();
                dataButton_edit.show();
            }
        }
    }

    protected final void refreshType() {
        CategoryType category;
        switch(typeStatus) {
            case GROUPS:
                category = CategoryType.GROUP;
                break;
            case USERS:
                category = CategoryType.USER;
                break;
            default:
                return;
        }
        PermCacheClient.instance().clear(CacheType.GLOBAL_LISTING, category, null);
        clearType();
        typeStatus = typeStatus.startWaiting();
    }

    protected final void refreshData() {
        ComponentList.ListEntry entry = typeList.getSelectedEntry();
        switch(dataStatus) {
            case GROUP_PERMS:
            case USER_PERMS:
                PermCacheClient.instance().clear(
                        CacheType.PERMISSIONS,
                        dataStatus == DataStatus.GROUP_PERMS ? CategoryType.GROUP : CategoryType.USER,
                        entry == null ? null : entry.getTextData().actual());
                break;
            case GROUP_USERS:
            case USER_GROUPS:
                PermCacheClient.instance().clear(
                        CacheType.LOCAL_LISTING_GROUP,
                        CategoryType.USER,
                        entry == null ? null : entry.getTextData().actual());
                PermCacheClient.instance().clear(
                        CacheType.LOCAL_LISTING_USER,
                        CategoryType.GROUP,
                        entry == null ? null : entry.getTextData().actual());
                break;
            default:
                return;
        }
        clearData();
        dataStatus = dataStatus.startWaiting();
    }

    protected final void clearType() {
        typeList.clear();
        typeButton_add.disable();
        typeButton_remove.disable();
        lastSelectEntry_Type = null;
    }

    protected final void clearData() {
        dataList.clear();
        dataValueMap = null;
        //filterText.clear();
        dataButton_add.disable();
        dataButton_remove.disable();
        dataButton_edit.disable();
        lastSelectEntry_Data = null;
    }

    protected final void hideDataControls() {
        dataStatus = DataStatus.NONE;
        groupTabs.hide();
        userTabs.hide();
        //filterText.hide();
        dataList.hide();
        dataButton_add.hide();
        dataButton_remove.hide();
        dataButton_edit.hide();
        clearData();
    }

    protected final void showDataControls() {
        groupTabs.show();
        userTabs.show();
        //filterText.show();
        dataButton_add.show();
        dataButton_remove.show();
        dataList.show();
        if(dataStatus == DataStatus.GROUP_PERMS || dataStatus == DataStatus.USER_PERMS) dataButton_edit.show();
        else dataButton_edit.hide();
    }

    protected final void setTypeList(String[] data) {
        if(data != null) {
            ComponentList.ListEntry[] entryArray = new ComponentList.ListEntry[data.length];
            for( int i = 0; i < data.length; i++) {
                if(data[i] == null || data[i].length() < 1) continue;
                if(data[i].charAt(0) == '_' || data[i].charAt(0) == '$') entryArray[i] = new ListEntrySpecial(data[i], typeList.getWidth()-8, fontRendererObj);
                else entryArray[i] = new ComponentList.ListEntry(data[i], typeList.getWidth()-8, fontRendererObj);
            }
            typeList.setEntries(entryArray);
            if(targetSelectEntry_type != null) {
                lastSelectEntry_Type = null;
                typeList.setSelectedEntry(targetSelectEntry_type);
                targetSelectEntry_type = null;
            }
        } else {
            typeList.clear();
        }
    }

    protected final void setDataList(String[] data) { setDataList(data, null); }
    protected final void setDataList(String[] data, String[] dataSecondary) {
        if(data != null) {
            ComponentList.ListEntry[] entryArray = new ComponentList.ListEntry[data.length];
            for( int i = 0; i < data.length; i++) {
                if(data[i] == null || data[i].length() < 1) continue;
                if(data[i].charAt(0) == '_' || data[i].charAt(0) == '$') entryArray[i] = new ListEntrySpecial(data[i], dataList.getWidth()-8, fontRendererObj);
                else if(dataSecondary != null) {
                    if(dataSecondary[i].equalsIgnoreCase("true"))
                        entryArray[i] = new ListEntryFlag(data[i], dataList.getWidth()-8, fontRendererObj, 1);
                    else if(dataSecondary[i].equalsIgnoreCase("false"))
                        entryArray[i] = new ListEntryFlag(data[i], dataList.getWidth()-8, fontRendererObj, -1);
                    else entryArray[i] = new ComponentList.ListEntry(data[i], dataList.getWidth()-8, fontRendererObj);
                } else entryArray[i] = new ComponentList.ListEntry(data[i], dataList.getWidth()-8, fontRendererObj);
            }
            dataList.setEntries(entryArray);
            if(dataSecondary != null) {
                dataValueMap = new HashMap<String, String>();
                for( int i = 0; i < data.length; i++) {
                    dataValueMap.put(data[i], dataSecondary[i]);
                }
            } else dataValueMap = null;
        } else {
            dataList.clear();
            dataValueMap = null;
        }
    }

    @Override
    public void onComponentChanged(CustomGUIComponent eventComponent) {
        if(eventComponent == typeList) {
            if(typeList.getSelectedEntry() == null) {
                hideDataControls();
                typeButton_remove.disable();
            } else {
                switch(typeStatus) {
                    case GROUPS:
                        typeButton_remove.enable();
                        break;
                    default:
                        typeButton_remove.disable();
                        break;
                }
            }

            /*
            if(typeList.isFilterEmpty()) typeButton_add.disable();
            else {
                switch(typeStatus) {
                    case GROUPS:
                        PermCacheClient.CacheData cacheData = PermCacheClient.instance().get(CacheType.GLOBAL_LISTING, CategoryType.GROUP, null);
                        if(cacheData instanceof PermCacheClient.CacheDataList) {
                            String[] groups = ((PermCacheClient.CacheDataList)cacheData).list;
                            for(String group : groups) {
                                if(typeList.getFilterText().equals(group)) {
                                    typeButton_add.disable();
                                    break;
                                }
                            }
                        }
                        typeButton_add.enable();
                        break;
                    default:
                        typeButton_add.disable();
                        break;
                }
            }
            */

        } else if(eventComponent == dataList) {
            if(dataList.getSelectedEntry() == null) {
                dataButton_remove.disable();
                dataButton_edit.disable();
            } else if(dataStatus == DataStatus.GROUP_USERS) {
                if(typeList.getSelectedEntry() instanceof ListEntrySpecial) dataButton_remove.disable();
                else dataButton_remove.enable();
            } else if(dataStatus == DataStatus.USER_GROUPS) {
                if(dataList.getSelectedEntry() instanceof ListEntrySpecial) dataButton_remove.disable();
                else dataButton_remove.enable();
            } else {
                dataButton_remove.enable();
                dataButton_edit.enable();
            }

            /*
            if(dataList.isFilterEmpty()) dataButton_add.disable();
            else {
                switch(dataStatus) {
                    case GROUP_PERMS:
                    case USER_PERMS:
                        dataButton_add.enable();
                        break;
                    case USER_GROUPS:
                        PermCacheClient.CacheData cacheData = PermCacheClient.instance().get(CacheType.GLOBAL_LISTING, CategoryType.GROUP, null);
                        if(cacheData instanceof PermCacheClient.CacheDataList) {
                            String[] groups = ((PermCacheClient.CacheDataList)cacheData).list;
                            for(String group : groups) {
                                if(dataList.getFilterText().equals(group)) {
                                    dataButton_add.enable();
                                    break;
                                }
                            }
                        }
                        dataButton_add.disable();
                        break;
                    default:
                        dataButton_add.disable();
                        break;
                }
            }
            */

        }
    }

    @Override
    public void onComponentSelected(CustomGUIComponent eventComponent) {
        if(eventComponent == typeTabs) {
            String tab = typeTabs.getSelectedTab();
            if (tab.equals("Groups")) {
                clearType();
                typeStatus = TypeStatus.GROUPS_WAITING;
                hideDataControls();
            } else if (tab.equals("Users")) {
                clearType();
                typeStatus = TypeStatus.USERS_WAITING;
                hideDataControls();
            }
        } else if(eventComponent == groupTabs) {
            String tab = groupTabs.getSelectedTab();
            if (tab.equals("Permissions")) {
                clearData();
                dataStatus = DataStatus.GROUP_PERMS_WAITING;
            } else if (tab.equals("Users")) {
                clearData();
                dataStatus = DataStatus.GROUP_USERS_WAITING;
            }
        } else if(eventComponent == userTabs) {
            String tab = userTabs.getSelectedTab();
            if (tab.equals("Permissions")) {
                clearData();
                dataStatus = DataStatus.USER_PERMS_WAITING;
            } else if (tab.equals("Groups")) {
                clearData();
                dataStatus = DataStatus.USER_GROUPS_WAITING;
            }
        } else if(eventComponent == typeList) {
            ComponentList.ListEntry entry = typeList.getSelectedEntry();
            if(entry != null && !entry.getTextData().actual().equals(lastSelectEntry_Type)) {
                typeButton_remove.enable();
                clearData();
                switch(typeStatus) {
                    case GROUPS:
                        dataStatus = DataStatus.GROUP_PERMS_WAITING;
                        groupTabs.setSelectedTab("Permissions");
                        showDataControls();
                        break;
                    case USERS:
                        dataStatus = DataStatus.USER_PERMS_WAITING;
                        userTabs.setSelectedTab("Permissions");
                        showDataControls();
                        break;
                    default: break;
                }
            } else if(entry == null) {
                hideDataControls();
                typeButton_remove.disable();
            }
            lastSelectEntry_Type = entry == null ? null : entry.getTextData().actual();
        } else if(eventComponent == dataList) {
            long thisTime = Minecraft.getSystemTime();
            long timeDiff = thisTime - lastSelectTime_Data;
            lastSelectTime_Data = thisTime;
            ComponentList.ListEntry entry = dataList.getSelectedEntry();
            if(entry != null && entry.getTextData().actual().equals(lastSelectEntry_Data) && timeDiff < 500) {
                switch(dataStatus) {
                    case GROUP_USERS:
                        typeTabs.setSelectedTab("Users");
                        targetSelectEntry_type = entry.getTextData().actual();
                        break;
                    case USER_GROUPS:
                        typeTabs.setSelectedTab("Groups");
                        targetSelectEntry_type = entry.getTextData().actual();
                        break;
                    default: break;
                }
                lastSelectEntry_Type = null;
                lastSelectEntry_Data = null;
            } else lastSelectEntry_Data = entry == null ? null : entry.getTextData().actual();

            if(entry == null) {
                dataButton_remove.disable();
                dataButton_edit.disable();
            } else if(dataStatus == DataStatus.GROUP_USERS) {
                if(typeList.getSelectedEntry() instanceof ListEntrySpecial) dataButton_remove.disable();
                else dataButton_remove.enable();
            } else if(dataStatus == DataStatus.USER_GROUPS) {
                if(dataList.getSelectedEntry() instanceof ListEntrySpecial) dataButton_remove.disable();
                else dataButton_remove.enable();
            } else {
                dataButton_remove.enable();
                dataButton_edit.enable();
            }
        } else if(eventComponent == typeButton_add) {
            switch(typeStatus) {
                case GROUPS:
                    componentFrame.showMessageBox(dialogue_addGroup);
                    break;
                default: break;
            }
        } else if(eventComponent == dataButton_add) {
            switch (dataStatus) {
                case GROUP_USERS:
                    componentFrame.showMessageBox(dialogue_addUserToGroup);
                    break;
                case USER_GROUPS:
                    componentFrame.showMessageBox(dialogue_addGroupToUser);
                    break;
                case GROUP_PERMS:
                case USER_PERMS:
                    componentFrame.showMessageBox(dialogue_permissionEdit);
                    break;
                default:
                    break;
            }
        } else if(eventComponent == dataButton_remove) {
            switch (dataStatus) {
                case GROUP_USERS: {
                    ComponentList.ListEntry groupEntry = typeList.getSelectedEntry();
                    ComponentList.ListEntry userEntry = dataList.getSelectedEntry();
                    if (groupEntry != null && userEntry != null) {
                        if (groupEntry instanceof ListEntrySpecial) {
                            simpleMessage.setMessage("Group is of a Reserved Type!");
                            componentFrame.showMessageBox(simpleMessage);
                        } else {
                            PacketBuilder.instance().requestServerAction(PacketRequest.RequestType.GROUP_REMOVE_USER, groupEntry.getTextData().actual(), userEntry.getTextData().actual());
                            refreshData();
                        }
                    }
                    break;
                }
                case USER_GROUPS: {
                    ComponentList.ListEntry groupEntry = dataList.getSelectedEntry();
                    ComponentList.ListEntry userEntry = typeList.getSelectedEntry();
                    if (groupEntry != null && userEntry != null) {
                        if (groupEntry instanceof ListEntrySpecial) {
                            simpleMessage.setMessage("Group is of a Reserved Type!");
                            componentFrame.showMessageBox(simpleMessage);
                        } else {
                            PacketBuilder.instance().requestServerAction(PacketRequest.RequestType.GROUP_REMOVE_USER, groupEntry.getTextData().actual(), userEntry.getTextData().actual());
                            refreshData();
                        }
                    }
                    break;
                }
                case GROUP_PERMS: {
                    ComponentList.ListEntry groupEntry = typeList.getSelectedEntry();
                    ComponentList.ListEntry permEntry = dataList.getSelectedEntry();
                    if (groupEntry == null || permEntry == null) break;
                    PacketBuilder.instance().requestServerAction(PacketRequest.RequestType.GROUP_PERM_SET, groupEntry.getTextData().actual(), permEntry.getTextData().actual(), null);
                    refreshData();
                    break;
                }
                case USER_PERMS: {
                    ComponentList.ListEntry userEntry = typeList.getSelectedEntry();
                    ComponentList.ListEntry permEntry = dataList.getSelectedEntry();
                    if (userEntry == null || permEntry == null) break;
                    PacketBuilder.instance().requestServerAction(PacketRequest.RequestType.USER_PERM_SET, userEntry.getTextData().actual(), permEntry.getTextData().actual(), null);
                    refreshData();
                    break;
                }
                default:
                    break;
            }
        } else if(eventComponent == dataButton_edit) {
            switch(dataStatus) {
                case GROUP_PERMS:
                case USER_PERMS:
                {
                    ComponentList.ListEntry permEntry = dataList.getSelectedEntry();
                    if(permEntry == null) break;
                    componentFrame.showMessageBox(dialogue_permissionEdit);
                    dialogue_permissionEdit.setResponse(permEntry.getTextData().actual());
                    break;
                }
                default:
                    break;
            }
        } else if(eventComponent == dialogue_addGroup) {
            if(!dialogue_addGroup.isCanceled()) {
                String newGroup = dialogue_addGroup.getResponse();
                PermCacheClient.CacheData cacheData = PermCacheClient.instance().get(CacheType.GLOBAL_LISTING, CategoryType.GROUP, null);
                if(cacheData instanceof PermCacheClient.CacheDataList) {
                    String[] groups = ((PermCacheClient.CacheDataList)cacheData).list;
                    for(String group : groups) {
                        if(newGroup.equals(group)) {
                            simpleMessage.setMessage("Group Already Exists!");
                            componentFrame.showMessageBox(simpleMessage);
                            return;
                        }
                    }
                }
                PacketBuilder.instance().requestServerAction(PacketRequest.RequestType.GROUP_CREATE, newGroup);
                refreshType();
                hideDataControls();
            }
        } else if(eventComponent == dialogue_addGroupToUser) {
            if(!dialogue_addGroupToUser.isCanceled()) {
                String newGroup = dialogue_addGroupToUser.getResponse();
                boolean valid = false;
                PermCacheClient.CacheData cacheData = PermCacheClient.instance().get(CacheType.GLOBAL_LISTING, CategoryType.GROUP, null);
                if(cacheData instanceof PermCacheClient.CacheDataList) {
                    String[] groups = ((PermCacheClient.CacheDataList)cacheData).list;
                    for(String group : groups) {
                        if(newGroup.equals(group)) {
                            valid = true;
                            break;
                        }
                    }
                }
                if(valid && newGroup.length() > 0) {
                    ComponentList.ListEntry userEntry = typeList.getSelectedEntry();
                    if(newGroup.charAt(0) == '_') {
                        simpleMessage.setMessage("Group is of a Reserved Type!");
                        componentFrame.showMessageBox(simpleMessage);
                    } else if(userEntry != null){
                        PacketBuilder.instance().requestServerAction(PacketRequest.RequestType.GROUP_ADD_USER, newGroup, userEntry.getTextData().actual());
                        refreshData();
                    }
                } else {
                    simpleMessage.setMessage("Group Does Not Exist!");
                    componentFrame.showMessageBox(simpleMessage);
                }
            }
        } else if(eventComponent == dialogue_addUserToGroup) {
            if(!dialogue_addUserToGroup.isCanceled()) {
                String newUser = dialogue_addUserToGroup.getResponse();
                boolean valid = false;
                PermCacheClient.CacheData cacheData = PermCacheClient.instance().get(CacheType.GLOBAL_LISTING, CategoryType.USER, null);
                if(cacheData instanceof PermCacheClient.CacheDataList) {
                    String[] users = ((PermCacheClient.CacheDataList)cacheData).list;
                    for(String user : users) {
                        if(newUser.equals(user)) {
                            valid = true;
                            break;
                        }
                    }
                }
                if(valid) {
                    ComponentList.ListEntry groupEntry = typeList.getSelectedEntry();
                    if(groupEntry instanceof ListEntrySpecial) {
                        simpleMessage.setMessage("Group is of a Reserved Type!");
                        componentFrame.showMessageBox(simpleMessage);
                    } else if(groupEntry != null) {
                        PacketBuilder.instance().requestServerAction(PacketRequest.RequestType.GROUP_ADD_USER, groupEntry.getTextData().actual(), newUser);
                        refreshData();
                    }
                } else {
                    simpleMessage.setMessage("User Does Not Exist!");
                    componentFrame.showMessageBox(simpleMessage);
                }
            }
        } else if(eventComponent == dialogue_permissionEdit) {
            if(!dialogue_permissionEdit.isCanceled()) {
                String buttonText = dialogue_permissionEdit.getSelectedButton().getText();
                if(buttonText == null) return; // Shouldn't happen
                String property = null;
                if(buttonText.equals("Allow")) property = "true";
                else if(buttonText.equals("Deny")) property = "false";
                if(property != null) {
                    ComponentList.ListEntry typeEntry = typeList.getSelectedEntry();
                    String permNode = dialogue_permissionEdit.getResponse();
                    if(typeEntry == null) return;
                    if(dataStatus == DataStatus.GROUP_PERMS) {
                        PacketBuilder.instance().requestServerAction(PacketRequest.RequestType.GROUP_PERM_SET, typeEntry.getTextData().actual(), permNode, property);
                        refreshData();
                    } else if(dataStatus == DataStatus.USER_PERMS) {
                        PacketBuilder.instance().requestServerAction(PacketRequest.RequestType.USER_PERM_SET, typeEntry.getTextData().actual(), permNode, property);
                        refreshData();
                    }
                } else {
                    componentFrame.showMessageBox(dialogue_permissionEditValue);
                }
            }
        } else if(eventComponent == dialogue_permissionEditValue) {
            if(!dialogue_permissionEditValue.isCanceled()) {
                String permValue = dialogue_permissionEditValue.getResponse();
                String permNode = dialogue_permissionEdit.getResponse();
                ComponentList.ListEntry typeEntry = typeList.getSelectedEntry();
                if(typeEntry == null) return;
                if(dataStatus == DataStatus.GROUP_PERMS) {
                    PacketBuilder.instance().requestServerAction(PacketRequest.RequestType.GROUP_PERM_SET, typeEntry.getTextData().actual(), permNode, permValue);
                    refreshData();
                } else if(dataStatus == DataStatus.USER_PERMS) {
                    PacketBuilder.instance().requestServerAction(PacketRequest.RequestType.USER_PERM_SET, typeEntry.getTextData().actual(), permNode, permValue);
                    refreshData();
                }
            }
        }
    }

    @Override
    public void handleMouseInput() {
        int dWheel = Mouse.getEventDWheel();
        if(dWheel != 0) {
            int xM = Mouse.getEventX() * this.width / this.mc.displayWidth;
            int yM = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
            componentFrame.mouseWheel(xM, yM, dWheel);
        }
        super.handleMouseInput();
    }

    @Override
    protected void mouseClicked(int xM, int yM, int which) {
        super.mouseClicked(xM, yM, which);

        // Do stuff
        componentFrame.mouseClicked(xM, yM, which);
    }

    @Override
    // which==-1 is move, otherwise up
    protected void mouseMovedOrUp(int xM, int yM, int which) {
        // Note: Need to call FIRST to release previous
        super.mouseMovedOrUp(xM, yM, which);

        // Do stuff
        componentFrame.mouseMovedOrUp(xM, yM, which);
    }

    @Override
    public void keyTyped(char key, int mode) {

        // Do stuff
        if(!componentFrame.keyTyped(key, mode))
            super.keyTyped(key, mode);
    }

    @Override
    public void drawScreen(int xM, int yM, float f) {

        this.drawDefaultBackground();

        // Do stuff
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);

        int x = (this.width - 32) / 2;
        int y = (this.height - 16) / 2;


        // Determine if Tooltip is needed

        ComponentList.ListEntry dataEntry = dataList.getEntryAtMouse(xM, yM);
        if (dataEntry != null && dataEntry.getTextData().actual() != null
                && !(dataEntry instanceof ListEntrySpecial) && !(dataEntry instanceof ListEntryFlag)
                && dataValueMap != null) {
            String dispText = dataValueMap.get(dataEntry.getTextData().actual());
            if (dispText != null) {
                hoverText.setText(dispText);
                hoverText.setPosition(xM, yM);
                hoverText.show();
            } else {
                hoverText.setText("");
                hoverText.hide();
            }
        } else {
            hoverText.setText("");
            hoverText.hide();
        }


        // Draw Main Component
        componentFrame.draw(xM, yM);


        // Draw Waiting Dots

        if(typeStatus.isWaiting()) {
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
        super.drawScreen(xM, yM, f);
    }

    @Override
    public void onGuiClosed() {
        // Prevents NullPointerException when Gui is closed due to a different MC crash
        if(Minecraft.getMinecraft() != null && Minecraft.getMinecraft().thePlayer != null) {
            PacketBuilder.instance().requestCacheClear();
            PermCacheClient.instance().clear();
        }
    }




    // ***************************
    //  Alternate ListEntry types
    // ***************************

    protected static class ListEntrySpecial extends ComponentList.ListEntry {
        protected String prefix = "";

        public ListEntrySpecial(String name, int width, FontRenderer fontRendererObj) {
            super(name, width, fontRendererObj);
        }
        public ListEntrySpecial(String name, int width, FontRenderer fontRendererObj, String prefix) {
            super(name, width, fontRendererObj);
            this.prefix = prefix;
        }

        public String getPrefix() { return prefix; }

        @Override
        public void draw(int xPos, int yPos, int width, int height) {
            fontRendererObj.drawStringWithShadow(getPrefix()+textData.display(), xPos+3, yPos+1, 0xFF999999);
        }

        @Override
        public void drawHover(int xPos, int yPos, int width, int height) {
            Gui.drawRect(xPos, yPos, xPos+width, yPos+height, 0x20FFFFFF);
            fontRendererObj.drawStringWithShadow(getPrefix()+textData.display(), xPos+3, yPos+1, 0xFF999999);
        }

        @Override
        public void drawSelect(int xPos, int yPos, int width, int height) {
            Gui.drawRect(xPos, yPos, xPos+width, yPos+height, 0x50FFFFFF);
            fontRendererObj.drawStringWithShadow(getPrefix()+textData.display(), xPos+3, yPos+1, 0xFFFFFB00);
        }
    }

    protected static class ListEntryFlag extends ComponentList.ListEntry {
        protected int type = 0;

        public ListEntryFlag(String name, int width, FontRenderer fontRendererObj, int type) {
            super(name, width, fontRendererObj);
            this.type = type;
        }

        protected final int getColor() {
            if(type == 0) return 0xFFFFFFFF;
            else if(type > 0) return 0xFF22FF22;
            else return 0xFFFF2222;
        }

        protected final String getPrefix() {
            if(type == 0) return "";
            else if(type > 0) return "+";
            else return "-";
        }

        @Override
        public void draw(int xPos, int yPos, int width, int height) {
            fontRendererObj.drawStringWithShadow(getPrefix()+textData.display(), xPos+3, yPos+1, getColor());
        }

        @Override
        public void drawHover(int xPos, int yPos, int width, int height) {
            Gui.drawRect(xPos, yPos, xPos+width, yPos+height, 0x20FFFFFF);
            fontRendererObj.drawStringWithShadow(getPrefix()+textData.display(), xPos+3, yPos+1, getColor());
        }

        @Override
        public void drawSelect(int xPos, int yPos, int width, int height) {
            Gui.drawRect(xPos, yPos, xPos+width, yPos+height, 0x50FFFFFF);
            fontRendererObj.drawStringWithShadow(getPrefix()+textData.display(), xPos+3, yPos+1, getColor());
        }
    }
}
