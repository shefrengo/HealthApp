package com.shefrengo.health.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shefrengo.health.model.Comment;
import com.shefrengo.health.model.Users;
import com.shefrengo.health.R;
import com.shefrengo.health.SetTime;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolder> {

    private List<Comment> commentList;
    private static final String TAG = "ReplyAdapter";
    private Context context;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public ReplyAdapter(List<Comment> commentList, Context context) {
        this.commentList = commentList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.reply_item, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String description = commentList.get(position).getDescription();
        String userid = commentList.get(position).getUserid();
        holder.description.setText(description);
        try {
            String timestamp = commentList.get(position).getTimestamp().toString();
            holder.timestamp.setText(SetTime.TwitterTimeDifferentitaion(timestamp));
        } catch (Exception e) {
            Log.e(TAG, "onBindViewHolder: ", e);
            holder.timestamp.setText("Just now");
        }

        getInfo(userid, holder);
    }

    private void getInfo(String userid, ViewHolder holder) {
        CollectionReference collectionReference = firebaseFirestore.collection("Users");
        collectionReference.document(userid).get().addOnSuccessListener(documentSnapshot -> {

            Users users = documentSnapshot.toObject(Users.class);
            assert users != null;
            String username = users.getUsername();
            String photo = users.getProfilePhotoUrl();
            holder.username.setText(username);
            Glide.with(context).asBitmap().placeholder(R.drawable.ic_app_background).load(photo).into(holder.circleImageView);


        }).addOnFailureListener(e -> Log.e(TAG, "onFailure: ", e));
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView username, description, timestamp;
        private CircleImageView circleImageView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            description = itemView.findViewById(R.id.description);
            timestamp = itemView.findViewById(R.id.timestamp);
            circleImageView = itemView.findViewById(R.id.profile_pic);
        }
    }
}
