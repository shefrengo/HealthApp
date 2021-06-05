package com.shefrengo.health.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.shefrengo.health.Adapters.GridViewAdapter;
import com.shefrengo.health.MainActivity;
import com.shefrengo.health.Models.Communities;
import com.shefrengo.health.Models.Members;
import com.shefrengo.health.Models.MyCommunities;
import com.shefrengo.health.Models.Users;
import com.shefrengo.health.R;

import java.util.ArrayList;
import java.util.List;

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
    private RecyclerView gridView;
    private GridViewAdapter gridViewAdapter;
    private List<Users> usersList;
    //private List<String> strings = new ArrayList<>();
    private ArrayList<String> stringList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_details);
        intent = getIntent();

        gridView = findViewById(R.id.contact_grid);

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
                            .addOnSuccessListener(unused -> {
                                Intent intent = new Intent(CommunityDetails.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            });
                });
            });
        });

        setIntents();
        putIntents();
        getContactInfo();
    }

    private void getContactInfo() {

        CollectionReference communityRef = db.collection("Communities");
        Query query = communityRef.whereEqualTo("communityId", communityId);
        query.get().addOnFailureListener(e -> Log.e(TAG, "onFailure: ", e))
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                        Communities communities = queryDocumentSnapshot.toObject(Communities.class);

                        stringList.addAll(communities.getAdminUserid());
                    }

                });

getAdmin();
    }

    private void getAdmin() {

        usersList = new ArrayList<>();
        CollectionReference collectionReference = db.collection("Users");


       // Query query = collectionReference.whereEqualTo("userId", admins);
       collectionReference.get().addOnSuccessListener(queryDocumentSnapshots -> {
           // usersList.clear();
            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                Users users = queryDocumentSnapshot.toObject(Users.class);

                for (String s:stringList){
                    Log.d(TAG, "getAdmin: "+s);
                    if (users.getUserId().equals(s)){
                        usersList.add(users);
                    }
                }
            }
            gridViewAdapter = new GridViewAdapter(usersList, CommunityDetails.this);
            gridView.setLayoutManager(new LinearLayoutManager(this));
            gridView.setHasFixedSize(true);
            gridView.setAdapter(gridViewAdapter);
        });
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
