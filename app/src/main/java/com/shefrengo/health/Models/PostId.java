package com.shefrengo.health.Models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class PostId {
    @Exclude
    public String postId;

    public <T extends PostId> T withId(@NonNull final String id) {
        this.postId = id;
        return (T) this;
    }


}