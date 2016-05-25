package com.majorpotato.febridge.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public class PermCache {

    private static PermCache instance = new PermCache();
    public static PermCache instance() { return instance; }

    private boolean waitingForGroups = false;
    public boolean isWaitingForGroups() { return waitingForGroups; }
    public void waitForGroups() { waitingForGroups = true; }

    private boolean waitingForGroupData = false;
    public boolean isWaitingForGroupData() { return waitingForGroupData; }
    public void waitForGroupData() { waitingForGroupData = true; }

    private boolean waitingForUsers = false;
    public boolean isWaitingForUsers() { return waitingForUsers; }
    public void waitForUsers() { waitingForUsers = true; }

    private boolean waitingForUserData = false;
    public boolean isWaitingForUserData() { return waitingForUserData; }
    public void waitForUserData() { waitingForUserData = true; }

    private String[] groups;

    private String[] groupPerms;
    private String[] groupUsers;
    private String currentGroup;

    private String[] users;

    private String[] userPerms;
    private String[] userGroups;
    private String userName;

    public void setGroups(String[] groups) {
        this.groups = groups;
        waitingForGroups = false;
    }

    public void setGroupData(String[] groupPerms, String[] groupUsers, String currentGroup) {
        this.groupPerms = groupPerms;
        this.groupUsers = groupUsers;
        this.currentGroup = currentGroup;
        waitingForGroupData = false;
    }

    public void setUsers(String[] users) {
        this.users = users;
        waitingForUsers = false;
    }

    public void setUserData(String[] userPerms, String[] userGroups, String userName) {
        this.userPerms = userPerms;
        this.userGroups = userGroups;
        this.userName = userName;
        waitingForUserData = false;
    }

    public String[] getGroups() { return waitingForGroups ? null : groups; }

    public String[] getGroupPerms() { return waitingForGroupData ? null : groupPerms; }
    public String[] getGroupUsers() { return waitingForGroupData ? null : groupUsers; }
    public String getCurrentGroup() { return waitingForGroupData ? null : currentGroup; }

    public String[] getUsers() { return waitingForUsers ? null : users; }

    public String[] getUserPerms() { return waitingForUserData ? null : userPerms; }
    public String[] getUserGroups() { return waitingForUserData ? null : userGroups; }
    public String getUserName() { return waitingForUserData ? null : userName; }


    // *****************
    //  Auto-Completion
    // *****************

    public void trimOptions(String text, ArrayList<String> possibilities) {
        for(int i = 0; i < possibilities.size(); i++) {
            String current = possibilities.get(i);
            if(current.length() < text.length() || !current.substring(0,text.length()).equals(text)) {
                possibilities.remove(i);
                i--;
            }
        }
    }

    private ArrayList<String> getOptions(String text, ArrayList<String> options) {
        ArrayList<String> out = new ArrayList<String>();
        for(String option : options) {
            if(option.length() >= text.length() && option.substring(0,text.length()).equals(text))
                out.add(option);
        }
        return out;
    }

    private ArrayList<String> getOptions(String text, String[] options) {
        ArrayList<String> out = new ArrayList<String>();
        for(String option : options) {
            if(option.length() >= text.length() && option.substring(0,text.length()).equals(text))
                out.add(option);
        }
        return out;
    }

    public ArrayList<String> autoGroups(String text) { return groups == null ? new ArrayList<String>() : getOptions(text, groups); }
    public ArrayList<String> autoUsers(String text) { return users == null ? new ArrayList<String>() : getOptions(text, users); }
}
