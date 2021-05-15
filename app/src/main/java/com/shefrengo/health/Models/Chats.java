package com.shefrengo.health.Models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Chats extends PostId {
    private String myUserid;
    private String chatUserid;
    private @ServerTimestamp
    Date timestamp;
    private String message;
    private boolean seen;

    public Chats() {
    }

    public String getMyUserid() {
        return myUserid;
    }

    public void setMyUserid(String myUserid) {
        this.myUserid = myUserid;
    }

    public String getChatUserid() {
        return chatUserid;
    }

    public void setChatUserid(String chatUserid) {
        this.chatUserid = chatUserid;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
