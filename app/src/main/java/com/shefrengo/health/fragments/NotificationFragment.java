package com.shefrengo.health.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.shefrengo.health.Adapters.NotificationAdapter;
import com.shefrengo.health.activity.LoginActivity;
import com.shefrengo.health.model.Data;
import com.shefrengo.health.model.Notifications;
import com.shefrengo.health.R;

import java.util.ArrayList;
import java.util.List;


public class NotificationFragment extends BaseFragment {
    private AppCompatActivity appCompatActivity;
    private static final String TAG = "NotificationFragment";
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private static final int limit = 20;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference;
    private List<Notifications> notificationList;
    private List<Data> dataList;
    private NotificationAdapter adapter;
    private DocumentSnapshot lastVisisble;
    private RecyclerView recyclerView;
    private TextView notificationText;

    private NestedScrollView nestedScrollView;
    private RelativeLayout errorLayout;
    private Button errorButton;
    private TextView errorTitle;

    private TextView errorMessage;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        appCompatActivity = (AppCompatActivity) view.getContext();
        if (user == null) {
            startActivity(new Intent(appCompatActivity, LoginActivity.class));
            appCompatActivity.overridePendingTransition(0, 0);
            appCompatActivity.finish();
        }

        notificationText = view.findViewById(R.id.no_notifications);

        nestedScrollView = view.findViewById(R.id.center_relative_layout);
        showProgress();
        collectionReference = db
                .collection("Notifications")
                .document(user.getUid()).collection("Notifications");
        recyclerView = view.findViewById(R.id.notication_recyclerview);

        setRecyclerview();
        return view;
    }

    private void setRecyclerview() {
        dataList = new ArrayList<>();
        notificationList = new ArrayList<>();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Query query = collectionReference.orderBy("timestamp", Query.Direction.DESCENDING).limit(limit);
            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                @Override
                public void onSuccess(QuerySnapshot documentSnapshots) {
                    dataList.clear();
                    notificationList.clear();
                    assert documentSnapshots != null;
                    if (!documentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot query : documentSnapshots) {
                            final Notifications notification = query.toObject(Notifications.class);
                            final String userid = notification.getUserid();
                            assert userid != null;


                            db.collection("Users").document(userid).addSnapshotListener((documentSnapshot, e) -> {
                                if (e != null) {
                                    Log.d(TAG, "onEvent: " + e.getMessage());
                                } else {
                                    Log.d(TAG, "onSuccess: adding notifications");
                                    assert documentSnapshot != null;
                                    final String username = documentSnapshot.getString("username");
                                    final String profilePhotoUrl = documentSnapshot.getString("profilePhotoUrl");

                                    dataList.add(new Data(username, profilePhotoUrl));
                                    notificationList.add(notification);


                                    hideProgress();
                                }
                            });
                        }
                        
                        adapter = new NotificationAdapter(appCompatActivity, notificationList, dataList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(appCompatActivity));
                        recyclerView.setNestedScrollingEnabled(false);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(adapter);

                        lastVisisble = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                                boolean lastReached = !recyclerView.canScrollVertically(1);

                                if (lastReached) {
                                    loadMore();
                                }
                            }
                        });

                    } else {
                        notificationText.setVisibility(View.VISIBLE);
                        hideProgress();

                    }
                }
            });
        }
    }


    private void loadMore() {
        Query query = collectionReference.orderBy("timestamp", Query.Direction.DESCENDING).startAfter(lastVisisble).limit(limit);
        query.get().addOnSuccessListener(documentSnapshots -> {
            assert documentSnapshots != null;
            if (!documentSnapshots.isEmpty()) {
                for (QueryDocumentSnapshot query1 : documentSnapshots) {
                    final Notifications notification = query1.toObject(Notifications.class);
                    final String userid = notification.getUserid();
                    assert userid != null;
                    db.collection("Users").document(userid).addSnapshotListener((documentSnapshot, e) -> {
                        if (e != null) {
                            Log.d(TAG, "onEvent: " + e.getMessage());
                        } else {
                            assert documentSnapshot != null;

                            String username = documentSnapshot.getString("username");
                            String profilePhotoUrl = documentSnapshot.getString("profilePhotoUrl");
                            dataList.add(new Data(username, profilePhotoUrl));
                            notificationList.add(notification);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
                lastVisisble = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
            }


        });
    }


}