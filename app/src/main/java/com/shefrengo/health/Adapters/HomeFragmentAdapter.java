 package com.shefrengo.health.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.nativead.NativeAd;
import com.shefrengo.health.model.Data;
import com.shefrengo.health.model.Posts;
import com.shefrengo.health.R;
import com.shefrengo.health.SetTime;
import com.shefrengo.health.ViewHolders.AdViewHolder;
import com.shefrengo.health.ViewHolders.HomeViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomeFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int IS_AD = 0;
    private static final int NO_AD = 1;
    private static final int OFFSET = 2;
    private List<NativeAd> nativeAdList = new ArrayList<>();
    private List<Object> list = new ArrayList<>();
    private final List<Data> dataList;
    private static final String TAG = "HomeFragmentAdapter";
    private final Context context;

    public HomeFragmentAdapter(List<Data> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    public void setList(List<Posts> postsList) {
        this.list.clear();
        this.list.addAll(postsList);
    }

    public void setAd(List<NativeAd> ad) {
        this.nativeAdList.addAll(ad);

    }


    public void mixedData() {
        if (list.size() == 0) {
            return;
        }
        int num = 0;
        List<Object> objects = new ArrayList<>();
        if (nativeAdList.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (num + OFFSET == i) {
                    num += OFFSET + 1;

                    int x =new Random().nextInt(nativeAdList.size());
                    objects.add(nativeAdList.get(x));
                    continue;
                }
                objects.add(list.get(i));
            }
        }
        list.clear();
        list.addAll(objects);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == IS_AD) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_ad_item, parent, false);
            return new AdViewHolder(view);
        } else {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home, parent, false);
            return new HomeViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == IS_AD) {
            AdViewHolder adViewHolder = (AdViewHolder) holder;
            adViewHolder.setNativeAd((NativeAd) list.get(position));
        } else {
            HomeViewHolder homeViewHolder = (HomeViewHolder) holder;
            Posts posts = (Posts) list.get(position);

            String image = posts.getImageUrl();

            String timestamp = posts.getTimestamp().toString();
            String category = posts.getCategory();

            int replyCount = posts.getReplyCount();
            String replyText = posts.getReplyCount() + " Replies";

            if (replyCount == 0) {
                homeViewHolder.replyCount.setText(context.getResources().getString(R.string.first_reply));
            } else {
                homeViewHolder.replyCount.setText(replyText);
            }

            homeViewHolder.title.setText(posts.getTitle());
            homeViewHolder.description.setText(posts.getDescription());
            if (image.isEmpty()) {
                homeViewHolder.imageView.setVisibility(View.GONE);
            }
            Glide.with(context).asBitmap().load(posts.getImageUrl()).into(homeViewHolder.imageView);

            String username = dataList.get(position).getUsername();
            String profilePhotoUrl = dataList.get(position).getProfilePhotoUrl();

            String text = " By @" + username + " in " + category + "'s forum ..." + SetTime.TwitterTimeDifferentitaion(timestamp);
            homeViewHolder.category.setText(text);
            Glide.with(context).asBitmap().load(profilePhotoUrl).into(homeViewHolder.circleImageView);

        }

        Log.d(TAG, "onBindViewHolder: " + dataList.size());
        Log.d(TAG, "onBindViewHolder: " + list.size());
    }

    @Override
    public int getItemCount() {


        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) instanceof NativeAd) {
            return IS_AD;
        } else {
            return NO_AD;
        }
    }
}
