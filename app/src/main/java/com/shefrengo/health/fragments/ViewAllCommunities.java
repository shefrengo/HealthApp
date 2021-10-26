package com.shefrengo.health.fragments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.shefrengo.health.Adapters.CommunityAdapter;
import com.shefrengo.health.AppBaseActivity;
import com.shefrengo.health.Communities.MyCommunitiesDetails;
import com.shefrengo.health.model.Communities;
import com.shefrengo.health.model.MyCommunities;
import com.shefrengo.health.R;

import java.util.ArrayList;
import java.util.List;

public class ViewAllCommunities extends AppBaseActivity {
    private RecyclerView recyclerView;
    private List<Communities> communitiesList;
    private CommunityAdapter adapter;
    private static final String TAG = "ViewAllCommunities";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_communities);
        recyclerView = findViewById(R.id.view_all_recyclerview);
        communitiesList = new ArrayList<>();
        adapter = new CommunityAdapter(communitiesList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        progressBar = findViewById(R.id.view_all_progress);


        adapter.setOnItemClickListener(new CommunityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String title = communitiesList.get(position).getName();
                String description = communitiesList.get(position).getDescription();
                int members = communitiesList.get(position).getMembers();
                int posts = communitiesList.get(position).getPosts();
                String postid = communitiesList.get(position).postId;
                String communityId = communitiesList.get(position).getCommunityId();
                String image = communitiesList.get(position).getImageUrl();
                Intent intent = new Intent(ViewAllCommunities.this, MyCommunitiesDetails.class);
                intent.putExtra("title", title);
                intent.putExtra("description", description);
                intent.putExtra("members", members);
                intent.putExtra("posts", posts);
                intent.putExtra("communityId", communityId);
                intent.putExtra("image", image);
                intent.putExtra("postid", postid);
                startActivity(intent);
            }
        });
        showProgress();
        myCommunities();
    }


    private void myCommunities() {
        CollectionReference collectionReference = db.collection("Users")
                .document(user.getUid()).collection("MyCommunities");
        //      Query query = collectionReference.whereEqualTo("", user.getUid()).limit(3);

        collectionReference.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                MyCommunities myCommunities = queryDocumentSnapshot.toObject(MyCommunities.class);
                String id = myCommunities.getCommunityId();

                CollectionReference collectionReference1 = db.collection("Communities");
                Query query = collectionReference1.whereEqualTo("communityId", myCommunities.getCommunityId());

                query.get().addOnSuccessListener(queryDocumentSnapshots1 -> {

                    for (QueryDocumentSnapshot queryDocumentSnapshot1 : queryDocumentSnapshots1) {
                        String commId = queryDocumentSnapshot1.getId();
                        Communities comm = queryDocumentSnapshot1.toObject(Communities.class).withId(commId);
                        communitiesList.add(comm);
                        adapter.notifyDataSetChanged();
                        hideProgress();
                    }
                });
            }
        });
    }

    private void showProgress() {

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }
}