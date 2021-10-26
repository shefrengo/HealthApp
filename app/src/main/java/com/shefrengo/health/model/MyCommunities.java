package com.shefrengo.health.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;

public class MyCommunities {
    private String userid;
    private String communityId;
    private ArrayList<String> search_keywords;
    private @ServerTimestamp
    Date timestamp;

    public MyCommunities() {
    }

    public ArrayList<String> getSearch_keywords() {
        return search_keywords;
    }

    public void setSearch_keywords(ArrayList<String> search_keywords) {
        this.search_keywords = search_keywords;
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
