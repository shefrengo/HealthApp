package com.shefrengo.health.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shefrengo.health.Models.Members;
import com.shefrengo.health.Models.MyCommunities;
import com.shefrengo.health.R;

public class CommunityDetails extends AppCompatActivity {
    private TextView titleText, descriptionText, enrollText;
    private Button enroll;
    private ImageView headerImage;
    private String image;
    private Intent intent;
    private String title;
    private String description;
    private String communityId;
    private int members;
    private int posts;
    private String postid;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "CommunityDetails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_details);
        intent = getIntent();

        titleText = findViewById(R.id.community_details_title);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        descriptionText = findViewById(R.id.description_of_community);
        enrollText = findViewById(R.id.community_enrolled_number);
        enroll = findViewById(R.id.enroll_button);
        headerImage = findViewById(R.id.header_image);


        enroll.setOnClickListener(v -> {


            CollectionReference collectionReference = db.collection("Communities").document(postid)
                    .collection("Members");
            Members members = new Members();
            members.setUserid(user.getUid());
            members.setCommunityId(communityId);
            collectionReference.document(user.getUid()).set(members).addOnFailureListener(e -> {
                Log.e(TAG, "onFailure: ", e);
                Toast.makeText(CommunityDetails.this, "Failed to enroll, please try again later", Toast.LENGTH_SHORT).show();
            }).addOnSuccessListener(aVoid -> {
                CollectionReference userRef = db.collection("Users").document(user.getUid()).collection("MyCommunities");
                MyCommunities myCommunities = new MyCommunities();
                myCommunities.setCommunityId(communityId);
                userRef.document(postid).set(myCommunities).addOnSuccessListener(aVoid1 -> {
                    db.collection("Users").document(user.getUid()).update("communities", FieldValue.increment(1))
                            .addOnSuccessListener(aVoid2 -> enroll.setText(getResources().getString(R.string.enrolled)));
                });
            });
        });

        setIntents();
        putIntents();
    }

    private void setIntents() {
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
            title = intent.getStringExtra("title");
        }
        if (intent.hasExtra("description")) {
            description = intent.getStringExtra("description");
        }
        if (intent.hasExtra("members")) {
            members = intent.getIntExtra("members", 0);
        }
        if (intent.hasExtra("posts")) {
            posts = intent.getIntExtra("posts", 0);
        }
    }

    private void putIntents() {
        titleText.setText(title);
        descriptionText.setText(description);
        db.collection("Communities").document(postid).collection("Members")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            int size = queryDocumentSnapshots.size();
            String stringBuilder = size +
                    " enrolled";
            enrollText.setText(stringBuilder);
        }).addOnFailureListener(e -> Log.e(TAG, "onFailure: ", e));


        Glide.with(CommunityDetails.this).asBitmap().load(image).into(headerImage);
    }


}
