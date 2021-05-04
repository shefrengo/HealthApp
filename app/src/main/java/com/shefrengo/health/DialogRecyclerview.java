package com.shefrengo.health;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.shefrengo.health.Adapters.CommunityAdapter;
import com.shefrengo.health.Adapters.DialogAdapter;
import com.shefrengo.health.Models.Communities;
import com.shefrengo.health.Models.MyCommunities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DialogRecyclerview extends DialogFragment {
    private DialogAdapter adapter;
    private RecyclerView mRecyclerView;
    private List<Communities> communitiesList;
    private static final String TAG = "DialogRecyclerview";
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    //   private AppCompatActivity appCompatActivity;
    private ProgressBar progressBar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // this method create view for your Dialog
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate layout with recycler view
        View v = inflater.inflate(R.layout.fragment_dialog, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        communitiesList = new ArrayList<>();
        adapter = new DialogAdapter(communitiesList, getActivity());

        progressBar = v.findViewById(R.id.dialog_progress);
        showProgress();
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(position -> {

            String community = communitiesList.get(position).getName();
            String id = communitiesList.get(position).getCommunityId();
            Intent intent = new Intent(getActivity(), PostQuestionActivity.class);
            intent.putExtra("community", community);
            intent.putExtra("communityId", id);
            Objects.requireNonNull(getActivity()).startActivity(intent);
        });
        getData();
        //get your recycler view and populate it.
        return v;
    }

    private void getData() {
        CollectionReference collectionReference = db.collection("Users").document(user.getUid())
                .collection("MyCommunities");
        collectionReference.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                MyCommunities myCommunities = queryDocumentSnapshot.toObject(MyCommunities.class);
                CollectionReference reference = db.collection("Communities");
                Query query = reference.whereEqualTo("communityId", myCommunities.getCommunityId());
                query.get().addOnSuccessListener(queryDocumentSnapshots1 -> {
                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots1) {
                        Communities communities = snapshot.toObject(Communities.class)
                                .withId(snapshot.getId());
                        communitiesList.add(communities);
                        adapter.notifyDataSetChanged();
                        hideProgress();
                    }
                });
            }
        }).addOnFailureListener(e -> Log.e(TAG, "onFailure: ", e));
    }

    private void showProgress() {

        progressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }
}
