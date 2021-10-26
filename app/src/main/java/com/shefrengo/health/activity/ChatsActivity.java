package com.shefrengo.health.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.shefrengo.health.Adapters.MainChatAdapter;
import com.shefrengo.health.AppBaseActivity;
import com.shefrengo.health.Notifications.Token;
import com.shefrengo.health.R;
import com.shefrengo.health.model.Conversations;
import com.shefrengo.health.model.Users;

import java.util.ArrayList;
import java.util.List;

public class ChatsActivity extends AppBaseActivity  implements View.OnClickListener, MainChatAdapter.OnMessageClickListerer  {
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


    private List<Conversations> chatsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        // Inflate the layout for this fragment

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Chats");
        setToolbar(toolbar);

        dataList = new ArrayList<>();
        recyclerView = findViewById(R.id.chats_recyclerview);
        createMessageButton = findViewById(R.id.create_message);
        textView = findViewById(R.id.no_messages);
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

    }

    private void setRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatsActivity.this));
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
                                adapter = new MainChatAdapter(ChatsActivity.this, chatsList, usersList);
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
            startActivity(new Intent(ChatsActivity.this, ChatList.class));
        }
    }

    @Override
    public void onMessageClick(int position) {
        String userid = usersList.get(position).getUserId();
        String name = usersList.get(position).getUsername();
        String photo = usersList.get(position).getProfilePhotoUrl();
        clearUnreadChat(userid);
        Intent intent = new Intent(ChatsActivity.this, MessagesActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("photo", photo);
        intent.putExtra("userid", userid);
        startActivity(intent);
    }

    private void showToast(String message) {
        Toast.makeText(ChatsActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void clearUnreadChat(String chatWithId) {
        assert user != null;
        CollectionReference collectionReference = db.collection("Conversation")
                .document(user.getUid()).collection("Conversation");
        collectionReference.document(chatWithId).update("unreadChatCount", 0);
    }
}