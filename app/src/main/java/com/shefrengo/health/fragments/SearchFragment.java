package com.shefrengo.health.fragments;

import static com.shefrengo.health.utils.extentions.AppExtensionsKt.getIsVendor;
import static com.shefrengo.health.utils.extentions.AppExtensionsKt.getUserId;
//import static com.shefrengo.health.utils.extentions.AppExtensionsKt.getUserName;
import static com.shefrengo.health.utils.extentions.AppExtensionsKt.isLoggedIn;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.shefrengo.health.AppBaseActivity;
import com.shefrengo.health.Communities.MyCommunitiesDetails;
import com.shefrengo.health.R;
import com.shefrengo.health.activity.CommunityDetails;
import com.shefrengo.health.activity.SearchActivity;
import com.shefrengo.health.adapters.RecyclerView2Adapter;
import com.shefrengo.health.adapters.RecyclerViewAdapter;
import com.shefrengo.health.model.Communities;
import com.shefrengo.health.model.Data;
import com.shefrengo.health.utils.extentions.AppExtensionsKt;
import com.shefrengo.health.utils.extentions.ExtensionsKt;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.functions.Function4;


public class SearchFragment extends BaseFragment {
    private static final String TAG = "SearchFragment";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private androidx.appcompat.widget.SearchView searchView;
    private RecyclerView rvSearch;
    private ProgressBar pbLoader;
    private LinearLayout rlNoData;
    private RecyclerView2Adapter<Communities> adapter;
    private ArrayList<Communities> productArrayList;
    private ArrayList<Data>dataArrayList;    private FirebaseAnalytics firebaseAnalytics;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        setHasOptionsMenu(true);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        SearchActivity activity = (SearchActivity)requireActivity();
        activity.setToolbar(toolbar);
        searchView = view.findViewById(R.id.searchView);
        rvSearch = view.findViewById(R.id.rvSearch);
        dataArrayList = new ArrayList<>();
        pbLoader = view.findViewById(R.id.pbLoader);
        pbLoader.setVisibility(View.GONE);
        rlNoData = view.findViewById(R.id.rlNoData);
        searchView.onActionViewExpanded();
        productArrayList = new ArrayList<>();
        setSearchView();
        return view;
    }
    private void setSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchFirestore(query.toLowerCase());
                //searchAnalytics(query.toLowerCase());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void searchFirestore(String newText) {

        pbLoader.setVisibility(View.VISIBLE);
        productArrayList.clear();

        CollectionReference collectionReference = db.collection("Communities");
        Query query = collectionReference
                .whereArrayContains("search_keywords", newText).limit(10);
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {

            if (!queryDocumentSnapshots.isEmpty()){
                rlNoData.setVisibility(View.GONE);
                adapter = getAdapter();
              setOnClick();
                ExtensionsKt.setVerticalLayout(rvSearch, false);
                rvSearch.setAdapter(adapter);
                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                    Communities product = queryDocumentSnapshot.toObject(Communities.class);
                    productArrayList.add(product);
                    adapter.addItems(productArrayList);
                    pbLoader.setVisibility(View.GONE);

                }
            }else {
                rlNoData.setVisibility(View.VISIBLE);
            }

        }).addOnFailureListener(e -> {
            pbLoader.setVisibility(View.GONE);
            rlNoData.setVisibility(View.VISIBLE);
            Log.e(TAG, "searchFirestore: ", e);
            ExtensionsKt.snackBar(requireActivity(), Objects.requireNonNull(e.getLocalizedMessage()), Snackbar.LENGTH_LONG);
        });
    }
    private void setOnClick(){
        adapter.setOnItemClick(new Function3<Integer, View, Communities, Unit>() {
            @Override
            public Unit invoke(Integer integer, View view, Communities communities) {

                String title =communities.getName();
                String description = communities.getDescription();
                int members = communities.getMembers();
                int posts = communities.getPosts();
                String postid = communities.postId;
                String image = communities.getImageUrl();
                String communityId = communities.getCommunityId();
                List<String> admins = communities.getAdminUserid();

                Intent intent = new Intent(getActivity(), CommunityDetails.class);
                intent.putExtra("title", title);
                intent.putExtra("description", description);
                intent.putExtra("members", members);
                intent.putExtra("posts", posts);
                intent.putExtra("communityId", communityId);
                intent.putExtra("image", image);
                intent.putStringArrayListExtra("admins",(ArrayList)admins);
                intent.putExtra("postid", postid);
                startActivity(intent);
                return null;
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private RecyclerView2Adapter<Communities> getAdapter() {
        return new RecyclerView2Adapter<>(R.layout.item_community, new Function3<View, Communities, Integer, Unit>() {
            @Override
            public Unit invoke(View view, Communities communities, Integer integer) {
                CircleImageView circleImageView = view.findViewById(R.id.notification_profile_pic2);
                TextView textView = view.findViewById(R.id.notificatin_type);
                textView.setText(communities.getName());
                AppExtensionsKt.loadImageFromUrl(circleImageView, communities.getImageUrl(), R.drawable.ic_profile, R.drawable.ic_profile);
                return null;
            }
        });
    }
    private void searchAnalytics(String name){
        if (isLoggedIn()) {
            Bundle params = new Bundle();
          //  params.putString("username", getUserName());
            params.putString("userid", getUserId());
            params.putString("product_searched",name);
            params.putLong("timestamp", System.currentTimeMillis());

            params.putBoolean("is_vendor", getIsVendor());
            firebaseAnalytics.logEvent("product_search", params);
        } else {
            Bundle params = new Bundle();
            params.putString("username", null);
            params.putString("userid", null);
            params.putString("product_searched",name);
            params.putBoolean("is_vendor", false);
            params.putLong("timestamp", System.currentTimeMillis());
            firebaseAnalytics.logEvent("product_search", params);
        }
    }
}