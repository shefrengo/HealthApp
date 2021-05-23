package com.shefrengo.health.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.shefrengo.health.Models.Chats;
import com.shefrengo.health.Models.Data;
import com.shefrengo.health.R;
import com.shefrengo.health.SetTime;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int LEFT_MESSAGE = 0;
    private static final String TAG = "MessageAdapter";
    public static final int RIGHT_MESSAGE = 1;
    private final List<Chats> chatsList;
    private Context context;
    private  String imageUrl;

    public MessageAdapter(List<Chats> chatsList, Context context) {
        this.chatsList = chatsList;
        this.context = context;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == RIGHT_MESSAGE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_right, parent, false);
            return new SenderViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_left, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {

        if (holder.getItemViewType() == RIGHT_MESSAGE) {
            SenderViewHolder holder1 = (SenderViewHolder) holder;
            configureRightMessage(holder1, position);
        } else {
            ReceiverViewHolder holder1 = (ReceiverViewHolder) holder;
            configureLeftMessage(holder1, position);
        }
    }

    @Override
    public int getItemCount() {
        return chatsList.size();
    }


    static class SenderViewHolder extends RecyclerView.ViewHolder {
        private final ConstraintLayout constraintLayout;
        private CircleImageView circleImageView;
        private final TextView timestamp;
        private final TextView message;

        public SenderViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.righ_message);
            timestamp = itemView.findViewById(R.id.timestamp);
            constraintLayout = itemView.findViewById(R.id.layout_first);
            LinearLayout layout = itemView.findViewById(R.id.layout_chat);
        }
    }

    private void configureRightMessage(SenderViewHolder holder, int position) {

        Chats chats = chatsList.get(position);
        if (chats != null) {
            holder.message.setText(chats.getMessage());
            try {
                holder.timestamp.setText(SetTime.TwitterTimeDifferentitaion(chats.getTimestamp().toString()));
            } catch (Exception e) {
                holder.timestamp.setText("Now");
            }

            holder.message.post(() -> {
                int linecount = holder.message.getLineCount();
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(holder.constraintLayout);
                if (linecount == 1) {
                    constraintSet.connect(R.id.layout_chat, ConstraintSet.END, R.id.timestamp, ConstraintSet.START, 10);
                    constraintSet.applyTo(holder.constraintLayout);
                }
                Log.i(TAG, "Lines : " + linecount);
            });

        }
    }

    @Override
    public int getItemViewType(int position) {
        Chats chat = chatsList.get(position);
        if (tes(chat)) {
            return RIGHT_MESSAGE;
        } else {
            return LEFT_MESSAGE;
        }

    }

    private boolean tes(Chats chat) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        return Objects.requireNonNull(mAuth.getCurrentUser()).getUid().equalsIgnoreCase(chat.getMyUserid());
    }

    private void configureLeftMessage(ReceiverViewHolder holder, int position) {
        Chats chats = chatsList.get(position);
        if (chats != null) {
            holder.message.setText(chats.getMessage());
           // Glide.with(context).asBitmap().load(imageUrl).into(holder.circleImageView);
            try {
                holder.timestamp.setText(SetTime.TwitterTimeDifferentitaion(chats.getTimestamp().toString()));
            } catch (Exception e) {
                holder.timestamp.setText("Now");
            }

            holder.message.post(() -> {
                int linecount = holder.message.getLineCount();
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(holder.constraintLayout);
                if (linecount == 1) {
                    constraintSet.connect(R.id.layout_chat_incoming, ConstraintSet.END, R.id.timestamp, ConstraintSet.START, 10);
                    constraintSet.applyTo(holder.constraintLayout);
                }
                Log.i(TAG, "Lines : " + linecount);
            });
        }

    }

    static class ReceiverViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView circleImageView;
        private final TextView message;
        private final TextView timestamp;
        private final ConstraintLayout constraintLayout;


        public ReceiverViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
           // circleImageView = itemView.findViewById(R.id.message_profiel);
            timestamp = itemView.findViewById(R.id.timestamp);
            message = itemView.findViewById(R.id.left_message);
            constraintLayout = itemView.findViewById(R.id.layout_first_incoming);


        }
    }
}
