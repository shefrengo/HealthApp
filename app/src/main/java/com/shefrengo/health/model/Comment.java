package com.shefrengo.health.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Comment extends PostId {

    private String title;
    private String userid;
    private String category;
    private String imageUrl;
    private String postDocumentId;
    private String description;
    private int replyCount;
    @ServerTimestamp
    private Date timestamp;

    public Comment() {
    }


    public String getPostDocumentId() {
        return postDocumentId;
    }

    public void setPostDocumentId(String postDocumentId) {
        this.postDocumentId = postDocumentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
