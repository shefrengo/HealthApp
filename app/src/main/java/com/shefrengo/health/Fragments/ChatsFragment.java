package com.shefrengo.health.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import com.google.firebase.messaging.FirebaseMessaging;
import com.shefrengo.health.Activities.ChatList;
import com.shefrengo.health.Activities.MessagesActivity;
import com.shefrengo.health.Adapters.MainChatAdapter;
import com.shefrengo.health.MainActivity;
import com.shefrengo.health.Models.Chats;
import com.shefrengo.health.Models.Users;
import com.shefrengo.health.Notifications.Token;
import com.shefrengo.health.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.shefrengo.health.Utils.Constants.EXTRA_IS_ROOT_FRAGMENT;


public class ChatsFragment extends Fragment implements View.OnClickListener, MainChatAdapter.OnMessageClickListerer {
    private RecyclerView recyclerView;
    private MainChatAdapter adapter;
    private List<String> dataList;
    private TextView textView;
    private List<Users> usersList;
    private List<Integer> messageCount;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FloatingActionButton createMessageButton;
    private static final String TAG = "ChatsFragment";
    private AppCompatActivity appCompatActivity;

    public static ChatsFragment newInstance(boolean isRoot) {
        Bundle args = new Bundle();
        args.putBoolean(EXTRA_IS_ROOT_FRAGMENT, isRoot);
        ChatsFragment fragment = new ChatsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        appCompatActivity = (AppCompatActivity)view.getContext();
        dataList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.chats_recyclerview);
        createMessageButton = view.findViewById(R.id.create_message);
        textView = view.findViewById(R.id.no_messages);
        createMessageButton.setOnClickListener(this);

        FirebaseMessaging.getInstance().getToken()

                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    // Get new FCM registration token
                    String token = task.getResult();

                 //   updateToken(token);
                });


        setData();

        return view;
    }

    private void setRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(appCompatActivity));
        recyclerView.setAdapter(adapter);
        adapter.setOnNmeaMessageListener(this);
    }

    private void updateToken(String token) {
        CollectionReference collectionReference = db.collection("Token");
        Token token1 = new Token(token);
        assert user != null;
        collectionReference.document(user.getUid()).set(token1, SetOptions.merge())
                .addOnFailureListener(e -> Log.e(TAG, "onFailure: ", e));

    }

    private void setData() {
        assert user != null;
        CollectionReference collectionReference = db.collection("Chats");
        collectionReference.addSnapshotListener(
                (value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "onEvent: ", error);
                        showToast("error occurred: " + error.getLocalizedMessage());
                        return;
                    }
                    assert value != null;
                    for (QueryDocumentSnapshot queryDocumentSnapshot : value) {
                        String id = queryDocumentSnapshot.getId();
                        Chats chats = queryDocumentSnapshot.toObject(Chats.class).withId(id);
                        if (chats.getMyUserid().equals(user.getUid())) {
                            dataList.add(chats.getChatUserid());
                        }
                        if (chats.getChatUserid().equals(user.getUid())) {
                            dataList.add(chats.getMyUserid());
                        }
                    }
                    readChats();
                });

    }

    private void readChats() {
        usersList = new ArrayList<>();
        messageCount = new ArrayList<>();
        CollectionReference collectionReference = db.collection("Users");
        collectionReference.addSnapshotListener(appCompatActivity,(value, error) -> {
            usersList.clear();
            assert value != null;
            //display 1 user from chats
            for (QueryDocumentSnapshot queryDocumentSnapshot : value) {
                Users users = queryDocumentSnapshot.toObject(Users.class);
                for (String id : dataList) {

                    if (users.getUserId().equals(id)) {

                        textView.setVisibility(View.GONE);
                        if (usersList.size() != 0) {

                            textView.setVisibility(View.GONE);
                            try {
                                for (Users users1 : usersList) {
                                    if (!users.getUserId().equals(users1.getUserId())) {

                                        usersList.add(users);
                                    }
                                }
                            } catch (Exception e) {
                                showToast(e.getMessage());
                            }

                        } else {
                            usersList.add(users);
                        }
                    } else {
                        textView.setVisibility(View.VISIBLE);
                    }

                }
            }

            adapter = new MainChatAdapter(appCompatActivity, usersList);
            adapter.setMessageCount(messageCount);
            setRecyclerView();
        });
    }

    @Override
    public void onClick(View v) {
        if (v == createMessageButton) {
            startActivity(new Intent(appCompatActivity, ChatList.class));
        }
    }

    @Override
    public void onMessageClick(int position) {
        String userid = usersList.get(position).getUserId();
        String name = usersList.get(position).getUsername();
        String photo = usersList.get(position).getProfilePhotoUrl();
        Intent intent = new Intent(getActivity(), MessagesActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("photo", photo);
        intent.putExtra("userid", userid);
        startActivity(intent);
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}