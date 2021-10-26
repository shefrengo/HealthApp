package com.shefrengo.health.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Data extends PostId implements Parcelable {

    public static final Parcelable.Creator<Data> CREATOR = new Parcelable.Creator<Data>() {
        @Override
        public Data createFromParcel(Parcel source) {
            return new Data(source);
        }

        @Override
        public Data[] newArray(int size) {
            return new Data[size];
        }
    };
    private String username;
    private String profilePhotoUrl;
    private String userid;

    public Data(String username, String profilePhotoUrl) {
        this.username = username;
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public Data(String username, String profilePhotoUrl, String userid) {
        this.username = username;
        this.profilePhotoUrl = profilePhotoUrl;
        this.userid = userid;
    }

    public Data() {
    }

    protected Data(Parcel in) {
        this.username = in.readString();
        this.profilePhotoUrl = in.readString();
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.username);
        dest.writeString(this.profilePhotoUrl);
    }
}
