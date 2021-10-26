package com.shefrengo.health.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.shefrengo.health.AppBaseActivity;
import com.shefrengo.health.Communities.MyCommunitiesDetails;
import com.shefrengo.health.R;
import com.shefrengo.health.adapters.RecyclerView2Adapter;
import com.shefrengo.health.adapters.RecyclerViewAdapter;
import com.shefrengo.health.model.Communities;
import com.shefrengo.health.model.Data;
import com.shefrengo.health.model.MyCommunities;
import com.shefrengo.health.utils.extentions.AppExtensionsKt;
import com.shefrengo.health.utils.extentions.ExtensionsKt;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.functions.Function4;

public class MyCommunitiesActivity extends AppBaseActivity {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private RecyclerView recyclerView;
    private RecyclerView2Adapter<Communities> adapter;
    private ArrayList<Communities> communitiesList;
    private LinearLayout rlNoData;
    private TextView tvMsg;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_communities);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("My Communities");
        setToolbar(toolbar);
        tvMsg= findViewById(R.id.tvMsg);
        rlNoData = findViewById(R.id.rlNoData);
        recyclerView = findViewById(R.id.my_communities_recyclerview);
        communitiesList = new ArrayList<>();
        adapter = getAdapter();

        recyclerView.setHasFixedSize(true);
        ExtensionsKt.setVerticalLayout(recyclerView,false);
        recyclerView.setAdapter(adapter);

        showProgress(true);
        myCommunities();
        setOnClick();
    }

    private RecyclerView2Adapter<Communities> getAdapter() {
        return new RecyclerView2Adapter<Communities>(R.layout.item_communities, (view, communities, integer) -> {
            CircleImageView circleImageView = view.findViewById(R.id.communities_image);
            TextView notificatin_type = view.findViewById(R.id.communities_title);
            notificatin_type.setText(communities.getName());
            AppExtensionsKt.loadImageFromUrl(circleImageView, communities.
                    getImageUrl(), R.drawable.placeholder, R.drawable.placeholder);
            return null;
        });
    }

    private void myCommunities() {
        CollectionReference collectionReference = db.collection("Users")
                .document(user.getUid()).collection("MyCommunities");

        Query query1 = collectionReference.orderBy("timestamp", Query.Direction.DESCENDING).limit(10);
        query1.get().addOnSuccessListener(queryDocumentSnapshots -> {

            if (queryDocumentSnapshots.isEmpty()){
                rlNoData.setVisibility(View.VISIBLE);
                tvMsg.setText(getString(R.string.no_communities_joined));
                showProgress(false);
            }else {
                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {

                    MyCommunities myCommunities = queryDocumentSnapshot.toObject(MyCommunities.class);
                    String id = myCommunities.getCommunityId();
                    CollectionReference collectionReference1 = db.collection("Communities");
                    Query query = collectionReference1.whereEqualTo("communityId", id);
                    query.get().addOnSuccessListener(queryDocumentSnapshots1 -> {
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots1) {

                            Communities communities1 = snapshot.toObject(Communities.class).withId(snapshot.getId());

                            communitiesList.add(communities1);

                            adapter.addItems(communitiesList);

                            showProgress(false);
                        }
                    });

                }
            }


        });
    }
    private void setOnClick(){
        adapter.setOnItemClick((integer, view, communities) -> {
            Intent intent = new Intent(MyCommunitiesActivity.this, MyCommunitiesDetails.class);
            intent.putExtra("postid",communities.postId);
            intent.putExtra("image",communities.getImageUrl());
            intent.putExtra("communityId",communities.getCommunityId());
            intent.putExtra("title",communities.getName());
            intent.putExtra("description",communities.getDescription());
            intent.putExtra("members", communities.getMembers());
            intent.putExtra("posts",communities.getPosts());
            startActivity(intent);
            return null;
        });
    }
}