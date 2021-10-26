package com.shefrengo.health.Utils;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

import static com.shefrengo.health.Utils.Constants.ACTION;
import static com.shefrengo.health.Utils.Constants.DATA_KEY_1;
import static com.shefrengo.health.Utils.Constants.DATA_KEY_2;
import static com.shefrengo.health.Utils.Constants.DATA_KEY_3;

import com.shefrengo.health.utils.SharedViewModel;

public class FragmentUtils {
    private static final String TAG = "FragmentUtils";
    private static final String TAG_SEPARATOR = ":";
    public static String userid;
    public static String photoUri;
    public static String title;
    public static String challengeDescription;
    public static String postid;
    public static String commentid;
    public static String getTag;
    public static String postUserid;

    public static String myDescription;
    public static String timestamp;
    public static Date times;
    public static String participateUserid;
    public static String videoUri;
    public static String sound;
    public static List<String> tagList;
    private String test;

    public static String getUserid() {
        return userid;
    }

    public static void setUserid(String userid) {
        FragmentUtils.userid = userid;
    }

    public static String getPhotoUri() {
        return photoUri;
    }

    public static void setPhotoUri(String photoUri) {
        FragmentUtils.photoUri = photoUri;
    }

    public static String getTitle() {
        return title;
    }

    public static void setTitle(String title) {
        FragmentUtils.title = title;
    }

    public static String getChallengeDescription() {
        return challengeDescription;
    }

    public static void setChallengeDescription(String challengeDescription) {
        FragmentUtils.challengeDescription = challengeDescription;
    }

    public static String getPostid() {
        return postid;
    }

    public static void setPostid(String postid) {
        FragmentUtils.postid = postid;
    }

    public static String getCommentid() {
        return commentid;
    }

    public static void setCommentid(String commentid) {
        FragmentUtils.commentid = commentid;
    }

    public static String getGetTag() {
        return getTag;
    }

    public static void setGetTag(String getTag) {
        FragmentUtils.getTag = getTag;
    }

    public static String getPostUserid() {
        return postUserid;
    }

    public static void setPostUserid(String postUserid) {
        FragmentUtils.postUserid = postUserid;
    }

    public static String getMyDescription() {
        return myDescription;
    }

    public static void setMyDescription(String myDescription) {
        FragmentUtils.myDescription = myDescription;
    }

    public static String getTimestamp() {
        return timestamp;
    }

    public static void setTimestamp(String timestamp) {
        FragmentUtils.timestamp = timestamp;
    }

    public static String getTAG() {
        return TAG;
    }

    public static String getTagSeparator() {
        return TAG_SEPARATOR;
    }

    /**
     * Add fragment in the particular tab stack and show it, while hiding the one that was before
     */
    public static void addShowHideFragment(FragmentManager fragmentManager,
                                           Map<String, Stack<String>> tagStacks,
                                           String tag,
                                           Fragment show,

                                           Fragment hide,
                                           int layoutId,
                                           boolean shouldAddToStack, SharedViewModel viewModel, boolean recieveData) {
        fragmentManager
                .beginTransaction()
                .add(layoutId, show, show.getClass().getName() + TAG_SEPARATOR + show.hashCode())
                .show(show)
                .hide(hide)
                .commit();
        if (recieveData) {
            viewModel.setUserId(FragmentUtils.userid);
            viewModel.setTitle(FragmentUtils.title);
            viewModel.setChallengeDescription(challengeDescription);
            viewModel.setPhotolist(photoUri);
            viewModel.setPostid(FragmentUtils.postid);
            viewModel.setCommentid(FragmentUtils.commentid);
            viewModel.setTags(FragmentUtils.getTag);
            viewModel.setPostUserid(FragmentUtils.postUserid);
            viewModel.setPostDescription(FragmentUtils.myDescription);
            viewModel.setTagList(tagList);
            viewModel.setTimestamp(times);
            viewModel.setParticipateUserid(participateUserid);


        }


        if (shouldAddToStack)
            Objects.requireNonNull(tagStacks.get(tag)).push(show.getClass().getName() + TAG_SEPARATOR + show.hashCode());
    }

    /**
     * Add the initial fragment, in most cases the first tab in BottomNavigationView
     */
    public static void addInitialTabFragment(FragmentManager fragmentManager,
                                             Map<String, Stack<String>> tagStacks,
                                             String tag,
                                             Fragment fragment,
                                             int layoutId,
                                             boolean shouldAddToStack) {
        fragmentManager
                .beginTransaction()
                .add(layoutId, fragment, fragment.getClass().getName() + TAG_SEPARATOR + fragment.hashCode())
                .commit();
        if (shouldAddToStack)
            Objects.requireNonNull(tagStacks.get(tag)).push(fragment.getClass().getName() + TAG_SEPARATOR + fragment.hashCode());
    }

    /**
     * Add additional tab in BottomNavigationView on click, apart from the initial one and for the first time
     */
    public static void addAdditionalTabFragment(FragmentManager fragmentManager,
                                                Map<String, Stack<String>> tagStacks,
                                                String tag,
                                                Fragment show,
                                                Fragment hide,
                                                int layoutId,

                                                boolean shouldAddToStack) {

        if (show.isVisible()) {
            show.onResume();
        }

        hide.onPause();


        fragmentManager
                .beginTransaction()
                .add(layoutId, show, show.getClass().getName() + TAG_SEPARATOR + show.hashCode())
                .show(show)
                .hide(hide)
                .commit();



        if (shouldAddToStack)
            Objects.requireNonNull(tagStacks.get(tag)).push(show.getClass().getName() + TAG_SEPARATOR + show.hashCode());

    }

    /**
     * Hide previous and show current tab fragment if it has already been added
     * In most cases, when tab is clicked again, not for the first time
     */
    public static void showHideTabFragment(FragmentManager fragmentManager,
                                           Fragment show,
                                           Fragment hide) {
        fragmentManager
                .beginTransaction()
                .hide(hide)
                .show(show)
                .commit();

    }

    public static void removeFragment(FragmentManager fragmentManager, Fragment show, Fragment remove) {


        fragmentManager
                .beginTransaction()
                .remove(remove)
                .show(show)
                .commit();
    }

    /**
     * Send action from fragment to activity
     */
    public static void sendActionToActivity(String action, String tab, boolean shouldAdd,
                                            BaseFragment.FragmentInteractionCallback fragmentInteractionCallback
            , boolean recieveData) {
        Bundle bundle = new Bundle();
        bundle.putString(ACTION, action);
        bundle.putString(DATA_KEY_1, tab);
        bundle.putBoolean(DATA_KEY_2, shouldAdd);

        bundle.putBoolean(DATA_KEY_3, recieveData);
        fragmentInteractionCallback.onFragmentInteractionCallback(bundle);
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }
}