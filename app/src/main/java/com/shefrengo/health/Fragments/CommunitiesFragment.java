package com.shefrengo.health.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.shefrengo.health.Adapters.CommunityAdapter;
import com.shefrengo.health.Adapters.MyCommunitiesAdapter;
import com.shefrengo.health.Communities.MyCommunitiesDetails;
import com.shefrengo.health.Activities.CommunityDetails;
import com.shefrengo.health.Models.Communities;
import com.shefrengo.health.Models.MyCommunities;
import com.shefrengo.health.R;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.shefrengo.health.Utils.Constants.EXTRA_IS_ROOT_FRAGMENT;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommunitiesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommunitiesFragment extends Fragment {
    private static final String TAG = "CommunitiesFragment";

    private RecyclerView recyclerView;
    private List<Communities> communities;

    private EditText editText;
    private CommunityAdapter adapter;
    private RecyclerView recyclerView2;
    private TextView viewAll;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private MyCommunitiesAdapter adapter2;
    private List<Communities> communitiesList;
    private NestedScrollView nestedScrollView;
    private ProgressBar progressBar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static CommunitiesFragment newInstance(boolean isRoot) {
        Bundle args = new Bundle();
        args.putBoolean(EXTRA_IS_ROOT_FRAGMENT, isRoot);
        CommunitiesFragment fragment = new CommunitiesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_communities, container, false);
        recyclerView = view.findViewById(R.id.communitiesRecy);
        communities = new ArrayList<>();
        communitiesList = new ArrayList<>();
        editText = view.findViewById(R.id.edtPassword);

        viewAll = view.findViewById(R.id.view_all);
        progressBar = view.findViewById(R.id.communities_progress);
        nestedScrollView = view.findViewById(R.id.communities_nested);
        showProgress();
        recyclerView2 = view.findViewById(R.id.my_communities_recyclerview);
        adapter = new CommunityAdapter(communities, getActivity());
        adapter2 = new MyCommunitiesAdapter(communitiesList, getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setNestedScrollingEnabled(false);
        recyclerView2.setAdapter(adapter2);
        adapter.setOnItemClickListener(position -> {
            String title = communities.get(position).getName();
            String description = communities.get(position).getDescription();
            int members = communities.get(position).getMembers();
            int posts = communities.get(position).getPosts();
            String postid = communities.get(position).postId;
            String image = communities.get(position).getImageUrl();
            String communityId = communities.get(position).getCommunityId();
            Intent intent = new Intent(getActivity(), CommunityDetails.class);
            intent.putExtra("title", title);
            intent.putExtra("description", description);
            intent.putExtra("members", members);
            intent.putExtra("posts", posts);
            intent.putExtra("communityId", communityId);
            intent.putExtra("image", image);
            intent.putExtra("postid", postid);
            startActivity(intent);
        });
        adapter2.setOnItemClickListener(position -> {
            String title = communitiesList.get(position).getName();
            String description = communitiesList.get(position).getDescription();
            int members = communitiesList.get(position).getMembers();
            int posts = communitiesList.get(position).getPosts();
            String postid = communitiesList.get(position).postId;
            String communityId = communitiesList.get(position).getCommunityId();
            String image = communitiesList.get(position).getImageUrl();
            Intent intent = new Intent(getActivity(), MyCommunitiesDetails.class);
            intent.putExtra("title", title);
            intent.putExtra("description", description);
            intent.putExtra("members", members);
            intent.putExtra("posts", posts);
            intent.putExtra("communityId", communityId);
            intent.putExtra("image", image);
            intent.putExtra("postid", postid);

            startActivity(intent);
        });
        getData();
        myCommunities();
        viewAll.setOnClickListener(v -> startActivity(new Intent(getActivity(), ViewAllCommunities.class)));

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchOnline(s.toString());
            }
        });
        return view;
    }

    private void getData() {
        CollectionReference collectionReference = db.collection("Users")
                .document(user.getUid()).collection("MyCommunities");

        CollectionReference communityRef = db.collection("Communities");

        communityRef.get().addOnSuccessListener(queryDocumentSnapshots1 -> {

            for (QueryDocumentSnapshot queryDocument : queryDocumentSnapshots1) {
                String id = queryDocument.getId();
                Communities communities1 = queryDocument.toObject(Communities.class).withId(id);

                communities.add(communities1);
                adapter.notifyDataSetChanged();
            }

        }).addOnFailureListener(e -> Log.e(TAG, "onFailure: ", e));
    }

    private void myCommunities() {
        CollectionReference collectionReference = db.collection("Users")
                .document(user.getUid()).collection("MyCommunities");
        //      Query query = collectionReference.whereEqualTo("", user.getUid()).limit(3);

        collectionReference.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {

                MyCommunities myCommunities = queryDocumentSnapshot.toObject(MyCommunities.class);
                String id = myCommunities.getCommunityId();


                CollectionReference collectionReference1 = db.collection("Communities");
                Query query = collectionReference1.whereEqualTo("communityId", id);
                query.get().addOnSuccessListener(queryDocumentSnapshots1 -> {
                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots1) {

                        Communities communities1 = snapshot.toObject(Communities.class).withId(snapshot.getId());
                        Log.d(TAG, "myCommunities: " + id);
                        communitiesList.add(communities1);
                        adapter2.notifyDataSetChanged();
                        hideProgress();
                    }
                });

            }
        });
    }

    private void showProgress() {

        progressBar.setVisibility(View.VISIBLE);
        nestedScrollView.setVisibility(View.GONE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
        nestedScrollView.setVisibility(View.VISIBLE);
    }


    private void searchOnline(final String text) {
        final Client client = new Client(getString(R.string.Agloia_application_id), getString(R.string.Admiin_api_key));

        final Index index = client.getIndex(getString(R.string.agolia_index_name));

        final com.algolia.search.saas.Query query = new com.algolia.search.saas.Query(text)
                .setAttributesToRetrieve("name", "profilePhotoUrl")
                .setHitsPerPage(30);


        index.searchAsync(query, (content, error) -> {
            try {


                assert content != null;
                JSONArray hits = content.getJSONArray("hits");

                final List<Communities> hitList = new ArrayList<>();
                final CommunityAdapter adapter = new CommunityAdapter( hitList,getActivity());
                for (int i = 0; i < hits.length(); i++) {
                    JSONObject jsonObject = hits.getJSONObject(i);


                    String name = jsonObject.getString("name");
                    String profileUrl = jsonObject.getString("profilePhotoUrl");
                    String userid = jsonObject.getString("objectID");



                    hitList.add(new Communities(name,profileUrl));
                    adapter.notifyDataSetChanged();
                }


                recyclerView.setHasFixedSize(true);

                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setAdapter(adapter);

               adapter.setOnItemClickListener(new CommunityAdapter.OnItemClickListener() {
                   @Override
                   public void onItemClick(int position) {

                   }
               });

            } catch (Exception e1) {
                Log.d(TAG, "requestCompleted: " + e1.getMessage());




            }
        });
    }
}