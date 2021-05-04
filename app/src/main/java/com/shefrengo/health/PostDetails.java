package com.shefrengo.health;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shefrengo.health.Adapters.ReplyAdapter;
import com.shefrengo.health.Models.Comment;
import com.shefrengo.health.Models.Notifications;
import com.shefrengo.health.Models.Posts;
import com.shefrengo.health.Models.Users;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostDetails extends AppCompatActivity implements View.OnClickListener {
    private CircleImageView userProfile, myProfile;
    private TextView myUsername, userUsername, timestamp, title, description, replyCount;
    private ImageView imageView;
    private Intent intent;
    private String image, userid, timestampText, postid, category, descriptionText, titleText, username, photo;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseUser user = auth.getCurrentUser();
    private static final String TAG = "PostDetails";
    private EditText editText;
    private ReplyAdapter adapter;
    private Button replyBtn;
    private List<Comment> commentList;
    private ProgressBar replyProgressbar;
    private String communityId;
    private ImageView moreOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        intent = getIntent();
        moreOptions = findViewById(R.id.more);
        replyBtn = findViewById(R.id.reply_button);
        RecyclerView recyclerView = findViewById(R.id.reply_recyclerview);
        commentList = new ArrayList<>();
        adapter = new ReplyAdapter(commentList, this);
        imageView = findViewById(R.id.photo);
        replyProgressbar = findViewById(R.id.reply_progress);
        RelativeLayout replyRelative = findViewById(R.id.reply_relativelayout);
        userProfile = findViewById(R.id.profile_pic);
        editText = findViewById(R.id.reply_edit);
        myProfile = findViewById(R.id.my_profile_pic);
        myUsername = findViewById(R.id.my_username);
        userUsername = findViewById(R.id.username);
        timestamp = findViewById(R.id.timestamp);
        title = findViewById(R.id.title_question);
        description = findViewById(R.id.description);
        replyCount = findViewById(R.id.reply_count);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        getIntents();
        getInfo();
        getReplies();
        if (userid.equals(user.getUid())) {
            moreOptions.setVisibility(View.VISIBLE);
        }else {
            moreOptions.setVisibility(View.GONE);
        }
        moreOptions.setOnClickListener(this);
        replyBtn.setOnClickListener(this);

    }

    @SuppressLint("SetTextI18n")
    private void getReplies() {
        CollectionReference collectionReference = db.collection("Posts").document(postid).collection("Comments");

        collectionReference.addSnapshotListener((queryDocumentSnapshots, error) -> {

            if (error != null) {
                Log.e(TAG, "onEvent: ", error);
            } else {
                int replies = queryDocumentSnapshots.size();

                replyCount.setText(replies + " replies");
                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                    String id = queryDocumentSnapshot.getId();
                    Comment comment = queryDocumentSnapshot.toObject(Comment.class).withId(id);
                    commentList.add(comment);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void postReply(String reply) {
        CollectionReference collectionReference = db.collection("Posts")
                .document(postid).collection("Comments");

        Comment myRepy = new Comment();
        myRepy.setCategory(category);
        myRepy.setImageUrl("");
        myRepy.setDescription(reply);
        myRepy.setPostDocumentId(postid);
        myRepy.setUserid(user.getUid());
        myRepy.setTitle(titleText);

        collectionReference.add(myRepy).addOnFailureListener(e -> {
            Log.e(TAG, "postReply: ", e);
        }).addOnSuccessListener(documentReference -> {

            db.collection("Posts").document(postid).update("replyCount", FieldValue.increment(1))
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(PostDetails.this, "Commented", Toast.LENGTH_SHORT).show();
                        addNotification();
                        editText.setText("");
                        //  commentList.add(0, myRepy);
                        replyProgressbar.setVisibility(View.GONE);
                        // adapter.notifyDataSetChanged();
                    }).addOnFailureListener(e -> Log.e(TAG, "onFailure: ", e));

        });
    }

    @SuppressLint("SetTextI18n")
    private void getFirestoreInfo() {
        Glide.with(PostDetails.this).asBitmap().load(photo).into(userProfile);
        userUsername.setText(username);

        CollectionReference collectionReference = db.collection("Users");

        if (user != null) {
            collectionReference.document(user.getUid()).get().addOnSuccessListener(documentSnapshot -> {
                Users users = documentSnapshot.toObject(Users.class);
                assert users != null;
                String name = users.getUsername();
                String photo = users.getProfilePhotoUrl();

                myUsername.setText("Add a reply " + name);
                Glide.with(PostDetails.this).asBitmap().load(photo).into(myProfile);

            }).addOnFailureListener(e -> Log.e(TAG, "onFailure: ", e));
        }


    }

    private void getInfo() {
        if (!image.isEmpty()) {
            imageView.setVisibility(View.VISIBLE);
            Glide.with(this).asBitmap().load(image).into(imageView);
        }
        timestamp.setText(SetTime.TwitterTimeDifferentitaion(timestampText));
        title.setText(titleText);
        description.setText(descriptionText);
        getFirestoreInfo();
    }

    private void getIntents() {
        if (intent.hasExtra("image")) {
            image = intent.getStringExtra("image");
        }
        if (intent.hasExtra("userid")) {
            userid = intent.getStringExtra("userid");
        }
        if (intent.hasExtra("photo")) {
            photo = intent.getStringExtra("photo");
        }
        if (intent.hasExtra("username")) {
            username = intent.getStringExtra("username");
        }
        if (intent.hasExtra("timestamp")) {
            timestampText = intent.getStringExtra("timestamp");
        }
        if (intent.hasExtra("postid")) {
            postid = intent.getStringExtra("postid");
        }
        if (intent.hasExtra("community")) {
            communityId = intent.getStringExtra("community");
        }
        if (intent.hasExtra("category")) {
            category = intent.getStringExtra("category");
        }
        if (intent.hasExtra("description")) {
            descriptionText = intent.getStringExtra("description");
        }
        if (intent.hasExtra("title")) {
            titleText = intent.getStringExtra("title");
        }
    }

    private void validate() {

        String reply = editText.getText().toString().trim();
        if (reply.isEmpty()) {
            editText.requestFocus();
            editText.setError(getResources().getString(R.string.enter_field));
            replyProgressbar.setVisibility(View.GONE);
            return;
        }
        if (userid.equals(user.getUid())) {
            Toast.makeText(this, "Cant reply to your own post", Toast.LENGTH_SHORT).show();
            return;
        }
        replyProgressbar.setVisibility(View.VISIBLE);
        postReply(reply);
    }

    @Override
    public void onClick(View v) {

        if (v == replyBtn) {
            validate();
        } else if (v == moreOptions) {
            setUpOptions();
        }
    }

    private void setUpOptions() {
        PopupMenu popupMenu = new PopupMenu(PostDetails.this, moreOptions);
        popupMenu.setOnMenuItemClickListener(item -> {

            replyProgressbar.setVisibility(View.VISIBLE);
            if (image.equals("")){
            deletePost();
            }else {
                deleteStorageMedia();
            }
            return false;
        });
        popupMenu.inflate(R.menu.post_menu);
        popupMenu.show();
    }

    private void deletePost() {


        CollectionReference collectionReference = db.collection("Posts");
        Query query = collectionReference.whereEqualTo("userid", user.getUid())
                .whereEqualTo("title", titleText).whereEqualTo("description", descriptionText)
                .whereEqualTo("community", communityId).whereEqualTo("category", category);
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {

            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                Posts posts = queryDocumentSnapshot.toObject(Posts.class);
                String imageUrl = posts.getImageUrl();
                String id = queryDocumentSnapshot.getId();
                Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
                db.collection("Posts").document(id).delete()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "successfully deleted", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PostDetails.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }).addOnFailureListener(e -> Log.e(TAG, "onFailure: ",e ));
            }
        }).addOnFailureListener(e -> Log.e(TAG, "onFailure: ",e ));

    }

    private void deleteStorageMedia() {
        CollectionReference collectionReference = db.collection("Posts");
        Query query = collectionReference.whereEqualTo("userid", user.getUid())
                .whereEqualTo("title", titleText).whereEqualTo("description", descriptionText)
                .whereEqualTo("community", communityId).whereEqualTo("category", category);
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {

            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                Posts posts = queryDocumentSnapshot.toObject(Posts.class);
                String imageUrl = posts.getImageUrl();
                String id = queryDocumentSnapshot.getId();
                db.collection("Posts").document(id).delete().addOnSuccessListener(aVoid -> {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReferenceFromUrl(imageUrl);
                    storageRef.delete().addOnSuccessListener(aVoid1 -> {
                        Intent intent = new Intent(PostDetails.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    });
                });
            }
        });

    }

    private void addNotification() {
        CollectionReference collectionReference = db.collection("Notifications").document(userid).collection("Notifications");
        long min = TimeUnit.DAYS.toMillis(1) + System.currentTimeMillis();

        final List<String> commentsUserlist = new ArrayList<>();
        commentsUserlist.add(user.getUid());
        Notifications notifications = new Notifications();
        notifications.setUserid(user.getUid());
        notifications.setText(getResources().getString(R.string.comment_notification));
        notifications.setInPost(true);
        notifications.setInReply(false);
        notifications.setPostid(userid);
        notifications.setPostUserid(userid);
        notifications.setPostid(postid);
        notifications.setRead(false);
        notifications.setStoryTitle(titleText);
        notifications.setTimeEnd(min);
        notifications.setCommentsUserids(commentsUserlist);

        Query query = collectionReference
                .whereEqualTo("postid", postid).whereEqualTo("text", getResources().getText(R.string.comment_notification))
                .whereEqualTo("postUserid", userid);

        query.get().addOnSuccessListener(documentSnapshots -> {
            Log.d(TAG, "onSuccess: ");

            if (documentSnapshots.isEmpty()) {
                Toast.makeText(this, "empty", Toast.LENGTH_SHORT).show();
                collectionReference.add(notifications);
            } else {
                for (QueryDocumentSnapshot queryDocumentSnapshot : documentSnapshots) {
                    Notifications notification1 = queryDocumentSnapshot.toObject(Notifications.class);
                    long currentTime = System.currentTimeMillis();
                    String docId = queryDocumentSnapshot.getId();

                    if (currentTime > (long) notification1.getTimeEnd()) {
                        collectionReference.add(notifications);
                        Toast.makeText(this, "one day past", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "day not passed", Toast.LENGTH_SHORT).show();
                        collectionReference.document(docId).update("commentsUserids", FieldValue.arrayUnion(user.getUid()));
                        collectionReference.document(docId).update("read", false);
                        collectionReference.document(docId).update("userid", user.getUid());
                    }
                }
            }
        }).addOnFailureListener(e -> Log.e(TAG, "onFailure: ", e));
    }
}