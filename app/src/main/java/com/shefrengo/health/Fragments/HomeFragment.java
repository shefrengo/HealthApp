package com.shefrengo.health.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
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
import com.shefrengo.health.DialogRecyclerview;

import com.shefrengo.health.Models.Data;
import com.shefrengo.health.Models.MyCommunities;
import com.shefrengo.health.Models.Posts;
import com.shefrengo.health.Models.Users;
import com.shefrengo.health.PostDetails;
import com.shefrengo.health.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.shefrengo.health.Utils.Constants.EXTRA_IS_ROOT_FRAGMENT;


public class HomeFragment extends Fragment {
    private HomeAdapter adapter;
    private RecyclerView recyclerView;
    private List<Posts> postsList;
    private static final String TAG = "HomeFragment";
    private RelativeLayout relativeLayout;
    private CardView cardview;
    private NestedScrollView nestedScrollView;
    private List<Data> dataList;
    private ProgressBar progressBar;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
private CircleImageView circleImageView;
private TextView name;
private String question = "What is you question.";
    private DocumentSnapshot lastVisible;

    private static final int limit = 10;

    public static HomeFragment newInstance(boolean isRoot) {
        Bundle args = new Bundle();
        args.putBoolean(EXTRA_IS_ROOT_FRAGMENT, isRoot);
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        relativeLayout = view.findViewById(R.id.main);
        nestedScrollView = view.findViewById(R.id.home_nestedScrollView);
        postsList = new ArrayList<>();
        circleImageView = view.findViewById(R.id.home_profile_pic);
        name = view.findViewById(R.id.question_textview);

        dataList = new ArrayList<>();
        progressBar = view.findViewById(R.id.home_progress);
        cardview = view.findViewById(R.id.home_cardview);
        showProgress();
        //hideProgress();
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setNestedScrollingEnabled(false);

        adapter = new HomeAdapter(postsList, getActivity(), dataList);
        adapter.setOnItemClickListener(position -> {
            String image = postsList.get(position).getImageUrl();
            String userid = postsList.get(position).getUserid();
            String timestamp = postsList.get(position).getTimestamp().toString();
            String title = postsList.get(position).getTitle();
            String description = postsList.get(position).getDescription();
            String category = postsList.get(position).getCategory();
            int reply = postsList.get(position).getReplyCount();
            String community = postsList.get(position).getCommunity();
            String photo = dataList.get(position).getProfilePhotoUrl();
            String username = dataList.get(position).getUsername();
            String postid = postsList.get(position).postId;
            Intent intent = new Intent(getActivity(), PostDetails.class);
            intent.putExtra("userid", userid);
            intent.putExtra("image", image);
            intent.putExtra("photo", photo);
            intent.putExtra("username", username);
            intent.putExtra("community",community);
            intent.putExtra("timestamp", timestamp);
            intent.putExtra("category", category);
            intent.putExtra("postid", postid);
            intent.putExtra("title", title);
            intent.putExtra("description", description);
            intent.putExtra("reply", reply);
            startActivity(intent);

        });
        cardview.setOnClickListener(v -> {
            DialogRecyclerview dialogRecyclerview = new DialogRecyclerview();
            FragmentManager fm = getActivity().getSupportFragmentManager();
            dialogRecyclerview.show(fm, "tag");

        });

        db.collection("Users").document(user.getUid()).get().addOnSuccessListener(documentSnapshot -> {
            Users users = documentSnapshot.toObject(Users.class);
            String username = users.getUsername();
            String photo = users.getProfilePhotoUrl();
            Glide.with(getActivity()).asBitmap().placeholder(R.drawable.ic_app_background).load(photo).into(circleImageView);
            question = "What is your question, "+username+"?";
            name.setText(question);
        });

        initRecyclerview();
        getData();

        return view;
    }

    private void initRecyclerview() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }


    private void getData() {


        CollectionReference myCommunitRef = db.collection("Users")
                .document(user.getUid()).collection("MyCommunities");

        myCommunitRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot myCommunitySnapshot : queryDocumentSnapshots) {
                MyCommunities myCommunities = myCommunitySnapshot.toObject(MyCommunities.class);
                String communityId = myCommunities.getCommunityId();
                CollectionReference postRef = db.collection("Posts");
                Query query = postRef.whereEqualTo("community", communityId)
                        .orderBy("timestamp", Query.Direction.DESCENDING).limit(limit);

                // posts
                query.get().addOnSuccessListener(postSnapshot -> {

                    for (QueryDocumentSnapshot queryDocumentSnapshot1 : postSnapshot) {
                        String id = queryDocumentSnapshot1.getId();
                        Posts posts = queryDocumentSnapshot1.toObject(Posts.class).withId(id);
                        String userid = posts.getUserid();

                        db.collection("Users").document(userid).get().addOnSuccessListener(documentSnapshot -> {
                            Users users = documentSnapshot.toObject(Users.class);
                            String username = users.getUsername();
                            String photo = users.getProfilePhotoUrl();
                            dataList.add(new Data(username, photo));
                            postsList.add(posts);
                            //  adapter.notifyDataSetChanged();
                            hideProgress();
                        });

                    }

                    if (postSnapshot.size() > 0) {
                        lastVisible = postSnapshot.getDocuments().get(postSnapshot.size() - 1);
                    }
                    nestedScrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
                        View view = nestedScrollView.getChildAt(nestedScrollView.getChildCount() - 1);
                        int diff = (view.getBottom() - (nestedScrollView.getHeight() + nestedScrollView.getScrollY()));
                        if (diff == 0) {
                            // load more
                            loadMore(communityId);

                        }
                    });
                    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            boolean bottomReached = !recyclerView.canScrollVertically(1);
                            if (bottomReached) {
                                //loadMore(communityId);
                            }
                        }
                    });

                }).addOnFailureListener(e -> Log.e(TAG, "onFailure: ", e));

            }
        });


    }


    private void loadMore(String communityId) {
        CollectionReference postRef = db.collection("Posts");
        Query query = postRef.whereEqualTo("community", communityId)
                .orderBy("timestamp", Query.Direction.DESCENDING).startAfter(lastVisible).limit(limit);

        // posts
        query.get().addOnSuccessListener(postSnapshot -> {


            for (QueryDocumentSnapshot queryDocumentSnapshot1 : postSnapshot) {
                String id = queryDocumentSnapshot1.getId();
                Posts posts = queryDocumentSnapshot1.toObject(Posts.class).withId(id);
                String userid = posts.getUserid();


                db.collection("Users").document(userid).get().addOnSuccessListener(documentSnapshot -> {
                    Users users = documentSnapshot.toObject(Users.class);
                    String username = users.getUsername();
                    String photo = users.getProfilePhotoUrl();
                    dataList.add(new Data(username, photo));
                    postsList.add(posts);
                   

                });

            }

            if (postSnapshot.size() > 0) {
                lastVisible = postSnapshot.getDocuments().get(postSnapshot.size() - 1);
            }

        }).addOnFailureListener(e -> Log.e(TAG, "onFailure: ", e));
    }

    private void showProgress() {

        progressBar.setVisibility(View.VISIBLE);
        relativeLayout.setVisibility(View.GONE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
        relativeLayout.setVisibility(View.VISIBLE);
    }
}