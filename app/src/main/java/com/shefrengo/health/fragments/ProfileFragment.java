package com.shefrengo.health.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.shefrengo.health.Adapters.ProfileAdapter;
import com.shefrengo.health.Admin;
import com.shefrengo.health.activity.LoginActivity;
import com.shefrengo.health.model.Data;
import com.shefrengo.health.model.Posts;
import com.shefrengo.health.model.Users;

import com.shefrengo.health.activity.PostDetails;
import com.shefrengo.health.R;
import com.shefrengo.health.utils.extentions.AppExtensionsKt;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.shefrengo.health.utils.extentions.AppExtensionsKt.getUserProfile;

public class ProfileFragment extends BaseFragment {
    private static final String TAG = "ProfileFragment";
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private TextView followers, following, posts, username;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Posts> postsList;
    private Button button;
    private NestedScrollView nestedScrollView;
    private RelativeLayout relativeLayout;
    private ProfileAdapter adapter;
    private AppCompatActivity appCompatActivity;
    private TextView noPostText;
    private ImageView options;
    private CircleImageView circleImageView;
    private List<Data> dataList;
    private TextView adminText;
    private static final int limit = 10;
    private DocumentSnapshot lastvisible;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        relativeLayout = view.findViewById(R.id.nestedScr);
        appCompatActivity = (AppCompatActivity) view.getContext();
        followers = view.findViewById(R.id.followersNum);
        following = view.findViewById(R.id.followingNum);

        nestedScrollView = view.findViewById(R.id.center_profile_main_relative);
        posts = view.findViewById(R.id.postNum);
        noPostText = view.findViewById(R.id.nopost);
        //options = view.findViewById(R.id.options);
        showProgress();
        postsList = new ArrayList<>();
        dataList = new ArrayList<>();
        button = view.findViewById(R.id.admin);
        adminText = view.findViewById(R.id.profile_display_bio);
        adapter = new ProfileAdapter(postsList, appCompatActivity, dataList);
        username = view.findViewById(R.id.profile_display_name);
        circleImageView = view.findViewById(R.id.profile_display_photo);
        RecyclerView recyclerView = view.findViewById(R.id.profile_recyclerview);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(appCompatActivity));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(position -> {
            String image = postsList.get(position).getImageUrl();
            String userid = postsList.get(position).getUserid();
            String timestamp = postsList.get(position).getTimestamp().toString();
            String title = postsList.get(position).getTitle();
            String description = postsList.get(position).getDescription();
            String category = postsList.get(position).getCategory();
            int reply = postsList.get(position).getReplyCount();
            String photo = dataList.get(position).getProfilePhotoUrl();
            String username = dataList.get(position).getUsername();
            String communityId = postsList.get(position).getCommunity();
            String postid = postsList.get(position).postId;
            Intent intent = new Intent(getActivity(), PostDetails.class);
            intent.putExtra("userid", userid);
            intent.putExtra("image", image);
            intent.putExtra("photo", photo);
            intent.putExtra("username", username);
            intent.putExtra("timestamp", timestamp);
            intent.putExtra("category", category);
            intent.putExtra("community",communityId);
            intent.putExtra("postid", postid);
            intent.putExtra("title", title);
            intent.putExtra("description", description);
            intent.putExtra("reply", reply);
            startActivity(intent);
        });

        if (user == null) {
            startActivity(new Intent(appCompatActivity, LoginActivity.class));
            appCompatActivity.finish();
        }

        getData();
        getFollowers();

        button.setOnClickListener(v -> startActivity(new Intent(appCompatActivity, Admin.class)));
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void getFollowers() {
        CollectionReference collectionReference = db.collection("Users");
        collectionReference.document(user.getUid()).addSnapshotListener((documentSnapshot, error) -> {

            if (error != null) {
                Log.e(TAG, "getFollowers: ", error);
            } else {
                assert documentSnapshot != null;
                Users users = documentSnapshot.toObject(Users.class);
                assert users != null;
                boolean admin = users.isAdmin();
                if (admin) {
                    button.setVisibility(View.VISIBLE);
                    adminText.setVisibility(View.VISIBLE);
                }
                String name = users.getUsername();
                int follower = users.getFollowers();
                int followin = users.getFollowing();
                int postCount = users.getPosts();
                username.setText(name);

                posts.setText(postCount + "");
                followers.setText(follower + "");
                following.setText(followin + "");

                AppExtensionsKt.loadImageFromUrl(circleImageView,getUserProfile(),R.drawable.ic_profile,R.drawable.ic_profile);

            }

        });
    }

    private void getData() {

        CollectionReference collectionReference = db.collection("Posts");
        Query query = collectionReference.whereEqualTo("userid", user.getUid());
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.isEmpty()) {
                noPostText.setVisibility(View.VISIBLE);
                hideProgress();
            } else {
                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                    hideProgress();
                    String id = queryDocumentSnapshot.getId();
                    Posts posts = queryDocumentSnapshot.toObject(Posts.class).withId(id);
                    String userid = posts.getUserid();
                    db.collection("Users").document(userid).get().addOnSuccessListener(documentSnapshot -> {
                        Users users = documentSnapshot.toObject(Users.class);
                        assert users != null;
                        dataList.add(new Data(users.getUsername(), users.getProfilePhotoUrl()));
                        postsList.add(posts);
                        adapter.notifyDataSetChanged();
                        hideProgress();
                    });

                }

                if (queryDocumentSnapshots.size() > 0) {
                    lastvisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                }
                nestedScrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
                    View view = nestedScrollView.getChildAt(nestedScrollView.getChildCount() - 1);
                    int diff = (view.getBottom() - (nestedScrollView.getHeight() + nestedScrollView.getScrollY()));
                    if (diff == 0) {
                        // load more
                        //  loadMore();

                    }
                });
            }
        }).addOnFailureListener(e -> Log.e(TAG, "onFailure: ", e));
    }

    private void loadMore() {
        CollectionReference collectionReference = db.collection("Posts");
        Query query = collectionReference.whereEqualTo("userid", user.getUid()).startAfter(lastvisible).limit(limit);
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.isEmpty()) {
                noPostText.setVisibility(View.VISIBLE);
            } else {
                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                    hideProgress();
                    String id = queryDocumentSnapshot.getId();
                    Posts posts = queryDocumentSnapshot.toObject(Posts.class).withId(id);
                    postsList.add(posts);
                    adapter.notifyDataSetChanged();
                }

                if (queryDocumentSnapshots.size() > 0) {
                    lastvisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                }

            }
        }).addOnFailureListener(e -> Log.e(TAG, "onFailure: ", e));
    }


    private void setPosts() {
        DocumentReference collectionReference = db.collection("Users").document(user.getUid());

        collectionReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Users users = value.toObject(Users.class);
                int postsCount = users.getPosts();
                int communitiesCount = users.getCommunities();
                int followersCount = users.getFollowers();
                int followingCount = users.getFollowing();

                followers.setText(followersCount + "");
                following.setText("" + followingCount);
                posts.setText("" + postsCount);


            }
        });
    }

}