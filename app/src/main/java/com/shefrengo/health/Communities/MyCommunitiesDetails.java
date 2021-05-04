package com.shefrengo.health.Communities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shefrengo.health.Adapters.HomeAdapter;
import com.shefrengo.health.Models.Data;
import com.shefrengo.health.Models.Posts;
import com.shefrengo.health.Models.Users;
import com.shefrengo.health.PostQuestionActivity;
import com.shefrengo.health.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyCommunitiesDetails extends AppCompatActivity {
    private NestedScrollView nestedScrollView;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private List<Posts> posts;
    private TextView count;
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

    }

    private void getRecyclerview() {
        CollectionReference collectionReference = db.collection("Posts");
        Query query = collectionReference.whereEqualTo("community", communityId).whereEqualTo("category", titleText);
        query.get().addOnFailureListener(e -> Log.e(TAG, "onFailure: ", e))
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                        Posts posts1 = queryDocumentSnapshot.toObject(Posts.class);
                        String userid = posts1.getUserid();
                        db.collection("Users").document(userid).get().addOnSuccessListener(documentSnapshot -> {
                            Users users = documentSnapshot.toObject(Users.class);
                            posts.add(posts1);
                            dataList.add(new Data(users.getUsername(), users.getProfilePhotoUrl()));
                            adapter.notifyDataSetChanged();
                            hideProgress();
                        });


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