package com.shefrengo.health.utils;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SharedViewModel extends ViewModel {
    private MutableLiveData<String> title = new MutableLiveData<>();
    private MutableLiveData<ArrayList<String>> imageUrls = new MutableLiveData<>();
    private MutableLiveData<String> photolist = new MutableLiveData<>();

    private MutableLiveData<String> postid = new MutableLiveData<>();
    private MutableLiveData<String> userId = new MutableLiveData<>();
    private MutableLiveData<String> challengeTitle = new MutableLiveData<>();
    private MutableLiveData<String> challengePhoto = new MutableLiveData<>();
    private MutableLiveData<String> challengeDescription = new MutableLiveData<>();
    private MutableLiveData<String> Tags = new MutableLiveData<>();
    private MutableLiveData<Uri> imageUri = new MutableLiveData<>();

    private MutableLiveData<String> commentid = new MutableLiveData<>();
    private MutableLiveData<List<String>> tilelist2 = new MutableLiveData<>();
    private MutableLiveData<Date> timestamp = new MutableLiveData<>();
    private MutableLiveData<String> postDescription = new MutableLiveData<>();
    private MutableLiveData<String> postUserid = new MutableLiveData<>();
    private MutableLiveData<String> participateUserid = new MutableLiveData<>();
    private MutableLiveData<List<String>> tagList = new MutableLiveData<>();


    public LiveData<String> getParticipateUserid() {
        return participateUserid;
    }

    public void setParticipateUserid(String participateUserid) {
        this.participateUserid.setValue(participateUserid);
    }

    public LiveData<List<String>> getTagList() {
        return tagList;
    }

    public void setTagList(List<String> tagList) {
        this.tagList.setValue(tagList);
    }

    public LiveData<String> getPostUserid() {
        return postUserid;
    }

    public void setPostUserid(String postUserid) {
        this.postUserid.setValue(postUserid);
    }

    public LiveData<String> getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription.setValue(postDescription);
    }

    public LiveData<Date> getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp.setValue(timestamp);
    }

    public void setTilelist2(List<String> tilelist2) {
        this.tilelist2.setValue(tilelist2);
    }

    public LiveData<List<String>> getTitlelist2() {
        return tilelist2;
    }

    public void setCommentid(String commentid) {
        this.commentid.setValue(commentid);
    }

    public LiveData<String> getCommentId() {
        return commentid;
    }

    public LiveData<Uri> getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri uri) {
        this.imageUri.setValue(uri);
    }

    public LiveData<String> getTags() {
        return Tags;
    }

    public void setTags(String Tags) {
        this.Tags.setValue(Tags);
    }


    public void setChallengeDescription(String challengeDescription) {
        this.challengeDescription.setValue(challengeDescription);
    }

    public LiveData<String> getDescription() {
        return challengeDescription;
    }

    public LiveData<String> getChallengeTitle() {
        return challengeTitle;
    }

    public void setChallengeTitle(String challengeTitle) {
        this.challengeTitle.setValue(challengeTitle);
    }

    public LiveData<String> getChallengePhoto() {
        return challengePhoto;
    }

    public void setChallengePhoto(String challengePhoto) {
        this.challengePhoto.setValue(challengePhoto);
    }

    public LiveData<String> getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId.setValue(userId);
    }

    public LiveData<String> getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid.setValue(postid);
    }

    public void setImageUrls(ArrayList<String> urls) {
        imageUrls.setValue(urls);
    }

    public LiveData<ArrayList<String>> getImageurl() {
        return imageUrls;
    }

    public void setPhotolist(String list) {
        photolist.setValue(list);
    }

    public LiveData<String> getTitle() {
        return title;
    }

    public void setTitle(String title1) {
        title.setValue(title1);
    }

    public LiveData<String> getPhotoList() {
        return photolist;
    }


}

