package com.shefrengo.health.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class Notifications {
    private String userid;
    private String text;
    private String postid;
    private boolean inPost;
    private boolean inComment;
    private boolean inReply;
    private String storyTitle;
    private String postUserid;
    private String commentId;
    private @ServerTimestamp
    Date timestamp;
    private Object timeEnd;
    private boolean isRead;
    private List<String> likesUserids;
    private List<String> commentsUserids;
    private List<String> replyUserid;





    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }


    public boolean isInPost() {
        return inPost;
    }

    public void setInPost(boolean inPost) {
        this.inPost = inPost;
    }

    public boolean isInComment() {
        return inComment;
    }

    public void setInComment(boolean inComment) {
        this.inComment = inComment;
    }

    public boolean isInReply() {
        return inReply;
    }

    public void setInReply(boolean inReply) {
        this.inReply = inReply;
    }

    public String getStoryTitle() {
        return storyTitle;
    }

    public void setStoryTitle(String storyTitle) {
        this.storyTitle = storyTitle;
    }

    public String getPostUserid() {
        return postUserid;
    }

    public void setPostUserid(String postUserid) {
        this.postUserid = postUserid;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }


    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Object getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Object timeEnd) {
        this.timeEnd = timeEnd;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public List<String> getLikesUserids() {
        return likesUserids;
    }

    public void setLikesUserids(List<String> likesUserids) {
        this.likesUserids = likesUserids;
    }

    public List<String> getCommentsUserids() {
        return commentsUserids;
    }

    public void setCommentsUserids(List<String> commentsUserids) {
        this.commentsUserids = commentsUserids;
    }

    public List<String> getReplyUserid() {
        return replyUserid;
    }


}
