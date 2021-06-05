package com.shefrengo.health.Models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class Communities extends PostId {
    private String name;
    private String description;
    private int members;
    private int posts;
    private String communityId;
    private List<String> adminUserid;

    private @ServerTimestamp
    Date timestamp;

    private String imageUrl;
    public Communities() {
    }

    public Communities(String name, String communityId, String imageUrl) {
        this.name = name;
        this.communityId = communityId;
        this.imageUrl = imageUrl;
    }

    public List<String> getAdminUserid() {
        return adminUserid;
    }

    public void setAdminUserid(List<String> adminUserid) {
        this.adminUserid = adminUserid;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }



    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMembers() {
        return members;
    }

    public void setMembers(int members) {
        this.members = members;
    }

    public int getPosts() {
        return posts;
    }

    public void setPosts(int posts) {
        this.posts = posts;
    }
}
