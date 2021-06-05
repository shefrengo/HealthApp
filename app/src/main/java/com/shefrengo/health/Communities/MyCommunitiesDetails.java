package com.shefrengo.health.Communities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.shefrengo.health.Adapters.HomeAdapter;
import com.shefrengo.health.Models.Data;
import com.shefrengo.health.Models.Posts;
import com.shefrengo.health.Models.Users;
import com.shefrengo.health.PostDetails;
import com.shefrengo.health.PostQuestionActivity;
import com.shefrengo.health.R;

import java.util.ArrayList;
import java.util.List;

public class MyCommunitiesDetails extends AppCompatActivity {
    private NestedScrollView nestedScrollView;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private List<Posts> posts;
    private TextView count;
    private static final int limit = 12;
    private TextView title;
    private TextView header_title;
    private Button writeButton;
    private String communityId;
    private String image;
    private FirebaseUser user;
    private HomeAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String titleText;
    private String description;
    private DocumentSnapshot lastVisible;
    private int members;
    private int postscoun;
    private List<Data> dataList;
    private String postid;
    private ImageView imageView;
    private static final String TAG = "MyCommunitiesDetails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_communities_details);

        nestedScrollView = findViewById(R.id.nestedScr);
        progressBar = findViewById(R.id.progressbar);
        user = FirebaseAuth.getInstance().getCurrentUser();
        posts = new ArrayList<>();
        dataList = new ArrayList<>();
        adapter = new HomeAdapter(posts, this, dataList);
        recyclerView = findViewById(R.id.my_communities_details_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        count = findViewById(R.id.count);
        imageView = findViewById(R.id.header_image);
        title = findViewById(R.id.title);
        header_title = findViewById(R.id.title_header);
        writeButton = findViewById(R.id.write_post_button);
        showProgress();
        setIntents();
        setWigdets();
        writeButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, PostQuestionActivity.class);
            intent.putExtra("community", titleText);
            intent.putExtra("communityId", communityId);
            startActivity(intent);
        });
        getRecyclerview();
        adapter.setOnItemClickListener(new HomeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String image = posts.get(position).getImageUrl();
                String userid = posts.get(position).getUserid();
                String timestamp = posts.get(position).getTimestamp().toString();
                String title = posts.get(position).getTitle();
                String description = posts.get(position).getDescription();
                String category = posts.get(position).getCategory();
                int reply = posts.get(position).getReplyCount();
                String community = posts.get(position).getCommunity();
                String photo = dataList.get(position).getProfilePhotoUrl();
                String username = dataList.get(position).getUsername();
                String postid = posts.get(position).postId;
                Intent intent = new Intent(MyCommunitiesDetails.this, PostDetails.class);
                intent.putExtra("userid", userid);
                intent.putExtra("image", image);
                intent.putExtra("photo", photo);
                intent.putExtra("username", username);
                intent.putExtra("community", community);
                intent.putExtra("timestamp", timestamp);
                intent.putExtra("category", category);
                intent.putExtra("postid", postid);
                intent.putExtra("title", title);
                intent.putExtra("description", description);
                intent.putExtra("reply", reply);
                startActivity(intent);
            }
        });

    }

    private void getRecyclerview() {
        CollectionReference collectionReference = db.collection("Posts");
        Query query = collectionReference.whereEqualTo("community", communityId).whereEqualTo("category", titleText).limit(limit);
        query.get().addOnFailureListener(e -> Log.e(TAG, "onFailure: ", e))
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                        String id = queryDocumentSnapshot.getId();
                        Posts posts1 = queryDocumentSnapshot.toObject(Posts.class).withId(id);
                        String userid = posts1.getUserid();
                        db.collection("Users").document(userid).get().addOnSuccessListener(documentSnapshot -> {
                            Users users = documentSnapshot.toObject(Users.class);
                            posts.add(posts1);
                            assert users != null;
                            dataList.add(new Data(users.getUsername(), users.getProfilePhotoUrl()));
                            adapter.notifyDataSetChanged();
                            hideProgress();
                        });

                    }
                    if (queryDocumentSnapshots.size() > 0) {
                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                    }
                    nestedScrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
                        View view = nestedScrollView.getChildAt(nestedScrollView.getChildCount() - 1);
                        int diff = (view.getBottom() - (nestedScrollView.getHeight() + nestedScrollView.getScrollY()));
                        if (diff == 0) {
                            loadMore();
                        }
                    });
                });
    }

    private void loadMore() {
        CollectionReference collectionReference = db.collection("Posts");
        Query query = collectionReference.whereEqualTo("community", communityId).whereEqualTo("category", titleText)
                .startAfter(lastVisible).limit(limit);
        query.get().addOnFailureListener(e -> Log.e(TAG, "onFailure: ", e))
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                        String id = queryDocumentSnapshot.getId();
                        Posts posts1 = queryDocumentSnapshot.toObject(Posts.class).withId(id);
                        String userid = posts1.getUserid();
                        db.collection("Users").document(userid).get().addOnSuccessListener(documentSnapshot -> {
                            Users users = documentSnapshot.toObject(Users.class);
                            posts.add(posts1);
                            assert users != null;
                            dataList.add(new Data(users.getUsername(), users.getProfilePhotoUrl()));
                            adapter.notifyDataSetChanged();
                            hideProgress();
                        });


                    }
                    if (queryDocumentSnapshots.size() > 0) {
                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    void setWigdets() {

        title.setText(titleText);
        header_title.setText(titleText);
        count.setText(members + " members ." + " " + postscoun + " posts");
    }

    private void setIntents() {
        Intent intent = getIntent();
        if (intent.hasExtra("postid")) {
            postid = intent.getStringExtra("postid");
        }
        if (intent.hasExtra("image")) {
            image = intent.getStringExtra("image");
        }
        if (intent.hasExtra("communityId")) {
            communityId = intent.getStringExtra("communityId");
        }
        if (intent.hasExtra("title")) {
            titleText = intent.getStringExtra("title");
        }
        if (intent.hasExtra("description")) {
            description = intent.getStringExtra("description");
        }
        if (intent.hasExtra("members")) {
            members = intent.getIntExtra("members", 0);
        }
        if (intent.hasExtra("posts")) {
            postscoun = intent.getIntExtra("posts", 0);
        }
        Glide.with(MyCommunitiesDetails.this).asBitmap().load(image).addListener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource,
                                           boolean isFirstResource) {

                return false;
            }
        }).into(imageView);
    }


    private void showProgress() {

        progressBar.setVisibility(View.VISIBLE);
        nestedScrollView.setVisibility(View.INVISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
        nestedScrollView.setVisibility(View.VISIBLE);
    }

}