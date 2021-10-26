package com.shefrengo.health.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.shefrengo.health.activity.CommunityDetails;
import com.shefrengo.health.activity.SearchActivity;
import com.shefrengo.health.adapters.RecyclerView2Adapter;
import com.shefrengo.health.model.Communities;
import com.shefrengo.health.R;
import com.shefrengo.health.utils.extentions.AppExtensionsKt;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommunitiesFragment extends BaseFragment {
    private static final String TAG = "CommunitiesFragment";

    private ArrayList<Communities> communities;

    private RecyclerView2Adapter<Communities> communitiesAdapter;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_communities, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.communitiesRecy);
        setHasOptionsMenu(true);

        showProgress();
        communitiesAdapter = getCommunitiesAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(communitiesAdapter);
        getData();

        return view;
    }

    private void getData() {
        communities = new ArrayList<>();
        CollectionReference communityRef = db.collection("Communities");


        Query query = communityRef.orderBy("timestamp", Query.Direction.DESCENDING).limit(13);
        query.get().addOnSuccessListener(queryDocumentSnapshots1 -> {
            communities.clear();
            for (QueryDocumentSnapshot queryDocument : queryDocumentSnapshots1) {
                String id = queryDocument.getId();
                Communities communities1 = queryDocument.toObject(Communities.class).withId(id);
                communities.add(communities1);
                communitiesAdapter.addItems(communities);
                hideProgress();
            }
            getCommunityClick();

        }).addOnFailureListener(e -> Log.e(TAG, "onFailure: ", e));
    }


    private void getCommunityClick() {

        communitiesAdapter.setOnItemClick((integer, view, communities) -> {
            String title = communities.getName();
            String description = communities.getDescription();
            int members = communities.getMembers();
            int posts = communities.getPosts();
            String phone = communities.getAdminPhone();
            String postid = communities.postId;
            String image = communities.getImageUrl();
            String communityId = communities.getCommunityId();
            List<String> admins = communities.getAdminUserid();
            Intent intent = new Intent(getActivity(), CommunityDetails.class);
            intent.putExtra("title", title);
            intent.putExtra("description", description);
            intent.putExtra("members", members);
            intent.putExtra("posts", posts);
            intent.putExtra("communityId", communityId);
            intent.putExtra("image", image);
            intent.putExtra("phone", phone);
            intent.putStringArrayListExtra("admins", (ArrayList) admins);
            intent.putExtra("postid", postid);
            startActivity(intent);
            return null;
        });
    }


    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        requireActivity().getMenuInflater().inflate(R.menu.menu_dashboard, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {

        if (item.getItemId() == R.id.action_search) {
            startActivity(new Intent(getActivity(), SearchActivity.class));
            return true;
        }
        return false;
    }
    private RecyclerView2Adapter<Communities> getCommunitiesAdapter() {
        return new RecyclerView2Adapter<>(R.layout.item_communities, (itemView, communities, integer) -> {
            TextView textView = itemView.findViewById(R.id.communities_title);
            CircleImageView circleImageView = itemView.findViewById(R.id.communities_image);
            AppExtensionsKt.loadImageFromUrl(circleImageView,communities.getImageUrl(),R.drawable.placeholder,R.drawable.placeholder);
            textView.setText(communities.getName());
            return null;
        });
    }
}