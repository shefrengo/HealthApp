package com.shefrengo.health.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.shefrengo.health.Activities.ChatList;
import com.shefrengo.health.Activities.MessagesActivity;
import com.shefrengo.health.Adapters.MainChatAdapter;
import com.shefrengo.health.Models.Conversations;
import com.shefrengo.health.Models.Users;
import com.shefrengo.health.Notifications.Token;
import com.shefrengo.health.R;

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

    private List<Conversations> chatsList;

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
        appCompatActivity = (AppCompatActivity) view.getContext();
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

                    Log.d(TAG, "onCreateView: "+token);
                    updateToken(token);
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
        chatsList = new ArrayList<>();
        usersList = new ArrayList<>();

        CollectionReference collectionReference = db.collection("Conversation")
                .document(user.getUid()).collection("Conversation");


        Query query = collectionReference.orderBy("timestamp", Query.Direction.DESCENDING);
        query.addSnapshotListener(
                (value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "onEvent: ", error);
                        showToast("error occurred: " + error.getLocalizedMessage());
                        return;
                    }
                    assert value != null;


                    if (value.isEmpty()) {
                        textView.setVisibility(View.VISIBLE);
                    } else {
                        textView.setVisibility(View.GONE);
                        usersList.clear();
                        chatsList.clear();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : value) {
                            Conversations chats = queryDocumentSnapshot.toObject(Conversations.class);
                            String id = chats.getChatWithId();

                            db.collection("Users").document(id).addSnapshotListener((value1, error1) -> {
                                assert value1 != null;
                                Users users = value1.toObject(Users.class);
                                usersList.add(users);
                                chatsList.add(chats);
                                adapter = new MainChatAdapter(appCompatActivity, chatsList, usersList);
                                adapter.setMessageCount(messageCount);
                                setRecyclerView();
                            });

                        }


                    }


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
        clearUnreadChat(userid);
        Intent intent = new Intent(getActivity(), MessagesActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("photo", photo);
        intent.putExtra("userid", userid);
        startActivity(intent);
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void clearUnreadChat(String chatWithId) {
        assert user != null;
        CollectionReference collectionReference = db.collection("Conversation")
                .document(user.getUid()).collection("Conversation");
        collectionReference.document(chatWithId).update("unreadChatCount", 0);
    }
}