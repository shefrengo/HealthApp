package com.shefrengo.health.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.shefrengo.health.Adapters.MessageAdapter;
import com.shefrengo.health.Models.Chats;
import com.shefrengo.health.Models.Users;
import com.shefrengo.health.Notifications.ApiService;
import com.shefrengo.health.Notifications.Client;
import com.shefrengo.health.Notifications.MyResponse;
import com.shefrengo.health.Notifications.NotificationsData;
import com.shefrengo.health.Notifications.Sender;
import com.shefrengo.health.Notifications.Token;
import com.shefrengo.health.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessagesActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private List<Chats> chatsList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MessageAdapter adapter;
    private String userid;
    private CircleImageView circleImageView;
    private ImageView backarrow;
    private FloatingActionButton sendButton;
    private EditText editText;
    private String name;
    private static final String TAG = "MessagesActivity";
    private String photo;
    private ApiService apiService;
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(ApiService.class);
        recyclerView = findViewById(R.id.messages_recyclerview);
        editText = findViewById(R.id.message_editText);
        sendButton = findViewById(R.id.send_message);
        circleImageView = findViewById(R.id.avatar_chat_room);
        TextView userName = findViewById(R.id.username_chat_room);
        backarrow = findViewById(R.id.toolbar_back_button);
        sendButton.setOnClickListener(this);
        backarrow.setOnClickListener(this);
        circleImageView.setOnClickListener(this);
        setIntents();
        userName.setText(name);
        Glide.with(this).asBitmap().load(photo).into(circleImageView);
        setChatsList();

    }

    private void setRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        if (chatsList.size() != 0) {
            recyclerView.smoothScrollToPosition(chatsList.size() - 1);
        }

        recyclerView.setAdapter(adapter);
    }

    private void setChatsList() {
        CollectionReference collectionReference = db.collection("Chats");
        chatsList = new ArrayList<>();
        Query query = collectionReference.orderBy("timestamp", Query.Direction.ASCENDING);
        query.addSnapshotListener((value, error) -> {
            chatsList.clear();
            if (error != null) {
                Log.e(TAG, "onEvent: ", error);
                Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show();
                return;
            }
            assert value != null;
            if (value.isEmpty()) {
                Log.d(TAG, "setChatsList: empty");
            } else {
                for (QueryDocumentSnapshot queryDocumentSnapshot : value) {

                    Chats chats = queryDocumentSnapshot.toObject(Chats.class);
                    if (chats.getChatUserid().equals(user.getUid()) && chats.getMyUserid().equals(userid)
                            || chats.getChatUserid().equals(userid) && chats.getMyUserid().equals(user.getUid())
                    ) {
                        chatsList.add(chats);
                    }


                }
                adapter = new MessageAdapter(chatsList, this);
                setRecyclerView();

            }

        });
    }

    private void setIntents() {
        Intent intent = getIntent();
        if (intent.hasExtra("name")) {

            name = intent.getStringExtra("name");
        }
        if (intent.hasExtra("photo")) {
            photo = intent.getStringExtra("photo");
        }
        if (intent.hasExtra("userid")) {
            userid = intent.getStringExtra("userid");
        }

    }

    @Override
    public void onClick(View v) {
        if (v == sendButton) {
            notify = true;
            validate();
        } else if (v == backarrow) {
            onBackPressed();
        } else if (v == circleImageView) {
            onBackPressed();
        }
    }

    private void validate() {
        String message = editText.getText().toString().trim();
        if (message.isEmpty()) {
            Toast.makeText(this, "Empty Message", Toast.LENGTH_SHORT).show();
            return;
        }
        sendMessage(message);
    }

    private void sendMessage(String message) {
        editText.setText("");
        Chats senderChats = new Chats();
        senderChats.setChatUserid(userid);
        senderChats.setMessage(message);
        senderChats.setMyUserid(user.getUid());

        CollectionReference senderRef = db.collection("Chats");

        senderRef.add(senderChats).addOnSuccessListener(documentReference -> {

        }).addOnFailureListener(e -> Log.e(TAG, "onFailure: ", e));

        final String msg = message;

        CollectionReference collectionReference = db.collection("Users");
        collectionReference.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e(TAG, "sendMessage: ", error);
                return;
            }
            assert value != null;
            for (QueryDocumentSnapshot queryDocumentSnapshot : value) {
                Users users = queryDocumentSnapshot.toObject(Users.class);

/**
                if (notify){
                    sendNotification(userid, users.getUsername(), msg);
                }

                notify = false;**/
            }
        });
    }

    private void sendNotification(String userid, String username, String message) {
       DocumentReference tokeRef = db.collection("Token")
                .document(userid);


       tokeRef.addSnapshotListener((value, error) -> {
           if (error!=null){
               Log.e(TAG, "sendNotification: ",error );
               return;
           }

           assert value != null;
           Token token = value.toObject(Token.class);
           NotificationsData data = new NotificationsData();
           data.setIcon(R.drawable.icc);
           data.setTitle("New Message");
           data.setBody(username + ": " + message);
           data.setSented(user.getUid());
           data.setUser(userid);

           assert token != null;
           Sender sender = new Sender(data, token.getToken());
           apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
               @Override
               public void onResponse(@NotNull Call<MyResponse> call, @NotNull Response<MyResponse> response) {
                   if (response.code() == 200) {

                       Log.d(TAG, "onResponse: "+response.code());
                       assert response.body() != null;
                       if (response.body().success != 1) {
                           Toast.makeText(MessagesActivity.this, "failed", Toast.LENGTH_SHORT).show();
                       }
                   }
               }

               @Override
               public void onFailure(@NotNull Call<MyResponse> call, @NotNull Throwable t) {
                   Log.d(TAG, "onFailure: " + t.getMessage());
               }
           });


       });

    }
}