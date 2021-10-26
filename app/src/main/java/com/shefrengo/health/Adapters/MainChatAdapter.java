package com.shefrengo.health.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shefrengo.health.model.Conversations;
import com.shefrengo.health.model.Data;
import com.shefrengo.health.model.Users;
import com.shefrengo.health.R;
import com.shefrengo.health.SetTime;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainChatAdapter extends RecyclerView.Adapter<MainChatAdapter.ViewHolder> {

    private OnMessageClickListerer onNmeaMessageListener;
    private Context context;
    private List<Conversations> chatsList;
    private List<Data> dataList;
    private List<Users> usersList;
    private List<Integer> messageCount;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "MainChatAdapter";

    public MainChatAdapter(Context context, List<Conversations> chatsList, List<Users> usersList) {
        this.context = context;
        this.chatsList = chatsList;
        this.usersList = usersList;
    }

    public void setMessageCount(List<Integer> messageCount) {
        this.messageCount = messageCount;
    }

    public void setOnNmeaMessageListener(OnMessageClickListerer onNmeaMessageListener) {
        this.onNmeaMessageListener = onNmeaMessageListener;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chats_main_layout, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {


        String username = usersList.get(position).getUsername();
        String photo = usersList.get(position).getProfilePhotoUrl();
        int messagecount = chatsList.get(position).getUnreadChatCount();
        holder.name.setText(username);
        holder.message.setText(chatsList.get(position).getLastMessage());
        if (messagecount == 0) {
            holder.textCount.setVisibility(View.INVISIBLE);
        } else {
            holder.textCount.setVisibility(View.VISIBLE);
        }
        holder.textCount.setText(String.valueOf(messagecount));

        Glide.with(context).asBitmap().load(photo).into(holder.circleImageView);



        Date timestamp  = chatsList.get(position).getTimestamp();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

       // String dateString = formatter.format(new Date(String.valueOf(timestamp)));

        holder.timestamp.setText(SetTime.setMessageTime(timestamp.toString()));
        //int messages = messageCount.get(position);
        // holder.textCount.setText(String.valueOf(messages));
    }

    @Override
    public int getItemCount() {
        return chatsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final private CircleImageView circleImageView;
        final private TextView name, message, textCount, timestamp;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.chats_profile_image);
            name = itemView.findViewById(R.id.chats_username);
            message = itemView.findViewById(R.id._chat_message);
            textCount = itemView.findViewById(R.id.message_count);
            timestamp = itemView.findViewById(R.id.timestamp);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (onNmeaMessageListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onNmeaMessageListener.onMessageClick(position);
                }
            }
        }

    }

    public interface OnMessageClickListerer {
        void onMessageClick(int position);
    }
}
