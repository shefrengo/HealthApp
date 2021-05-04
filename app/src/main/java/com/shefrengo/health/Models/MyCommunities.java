package com.shefrengo.health.Models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class MyCommunities {
    private String userid;
    private String communityId;
    private @ServerTimestamp
    Date timestamp;

    public MyCommunities() {
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }



    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
