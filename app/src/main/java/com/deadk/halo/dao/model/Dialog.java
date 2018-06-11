package com.deadk.halo.dao.model;

import com.deadk.halo.common.models.IDialog;

import java.util.HashMap;

public class Dialog implements IDialog<Message> {

    private String dialogId;
    private String dialogName;
    private String users;
    private HashMap<String, Object> idClient;
    private String clientAvatar;
    private String uidHost;
    private String lastMessage;
    private int unreadCount;
    private int isGroup;

    public Dialog(){}

    public Dialog(String dialogId,String dialogName, String users, HashMap<String, Object> idClient, String clientAvatar,
                  String uidHost, String lastMessage, int unreadCount, int isGroup) {
        this.dialogId = dialogId;
        this.dialogName = dialogName;
        this.users = users;
        this.idClient = idClient;
        this.clientAvatar = clientAvatar;
        this.uidHost = uidHost;
        this.lastMessage = lastMessage;
        this.unreadCount = unreadCount;
        this.isGroup = isGroup;
    }

    @Override
    public String getDialogId() {
        return dialogId;
    }

    @Override
    public void setDialogId(String dialogId) {
        this.dialogId = dialogId;
    }

    @Override
    public String getDialogName() {
        return dialogName;
    }

    @Override
    public void setDialogName(String dialogName) {
        this.dialogName = dialogName;
    }

    @Override
    public String getUsers() {
        return users;
    }

    @Override
    public void setUsers(String users) {
        this.users = users;
    }

    @Override
    public String getLastMessage() {
        return lastMessage;
    }

    @Override
    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    @Override
    public int getUnreadCount() {
        return unreadCount;
    }

    @Override
    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    @Override
    public HashMap<String, Object> getIdClient() {
        return idClient;
    }

    @Override
    public void setIdClient(HashMap<String, Object> idClient) {
        this.idClient = idClient;
    }

    @Override
    public String getClientAvatar() {
        return clientAvatar;
    }

    @Override
    public void setClientAvatar(String clientAvatar) {
        this.clientAvatar = clientAvatar;
    }

    @Override
    public int getIsGroup() {
        return isGroup;
    }

    @Override
    public void setIsGroup(int isGroup) {
        this.isGroup = isGroup;
    }

    @Override
    public String getUidHost() {
        return uidHost;
    }

    @Override
    public void setUidHost(String uidHost) {
        this.uidHost = uidHost;
    }
}
