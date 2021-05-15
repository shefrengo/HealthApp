package com.shefrengo.health.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shefrengo.health.Adapters.ChatListAdapter;
import com.shefrengo.health.Adapters.MainChatAdapter;
import com.shefrengo.health.Models.Chats;
import com.shefrengo.health.Models.Data;
import com.shefrengo.health.Models.Users;
import com.shefrengo.health.R;

import java.util.ArrayList;
import java.util.List;

public class ChatList extends AppCompatActivity implements View.OnClickListener,
        ChatListAdapter.OnChatListListener {
    private RecyclerView recyclerView;
    private ChatListAdapter adapter;
    private List<Users> chatsList;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        chatsList = new ArrayList<>();
        recyclerView = findViewById(R.id.chats_recyclerview);
        adapter = new ChatListAdapter(chatsList, this);
        adapter.setOnChatListListener(this);
        setRecyclerView();
        setData();
    }

    private void setRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatList.this));
        recyclerView.setAdapter(adapter);
    }

    private void setData() {

        CollectionReference collectionReference = db.collection("Users");
        Query query = collectionReference.whereEqualTo("admin", true);
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                Users users = queryDocumentSnapshot.toObject(Users.class);
                chatsList.add(users);
                adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onClick(View v) {


    }


    @Override
    public void onChatListClick(int position) {
        String name = chatsList.get(position).getUsername();
        String photo = chatsList.get(position).getProfilePhotoUrl();
        String userid = chatsList.get(position).getUserId();
        Intent intent = new Intent(ChatList.this, MessagesActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("photo", photo);
        intent.putExtra("userid", userid);
        startActivity(intent);
    }
}