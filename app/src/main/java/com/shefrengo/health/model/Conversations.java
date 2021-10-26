package com.shefrengo.health.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Conversations {
    private String userUid;
    private String chatWithId;
    private String chatId;
    private String lastMessage;

    private @ServerTimestamp
    Date timestamp;
    private int unreadChatCount = 0;

    public Conversations() {
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getChatWithId() {
        return chatWithId;
    }

    public void setChatWithId(String chatWithId) {
        this.chatWithId = chatWithId;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getUnreadChatCount() {
        return unreadChatCount;
    }

    public void setUnreadChatCount(int unreadChatCount) {
        this.unreadChatCount = unreadChatCount;
    }
}
