package com.shefrengo.health.Adapters;

import android.content.Context;
import android.location.OnNmeaMessageListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shefrengo.health.Models.Chats;
import com.shefrengo.health.Models.Data;
import com.shefrengo.health.Models.Users;
import com.shefrengo.health.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainChatAdapter extends RecyclerView.Adapter<MainChatAdapter.ViewHolder> {

    private OnMessageClickListerer onNmeaMessageListener;
    private Context context;
    private List<Chats> chatsList;
    private List<Data> dataList;
    private List<Users> usersList;
    private List<Integer> messageCount;

    public MainChatAdapter(Context context, List<Users> usersList) {
        this.context = context;
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

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        String photo = usersList.get(position).getProfilePhotoUrl();
        String username = usersList.get(position).getUsername();
        //int messages = messageCount.get(position);
       // holder.textCount.setText(String.valueOf(messages));
        holder.name.setText(username);
        Glide.with(context).asBitmap().load(photo).into(holder.circleImageView);
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final private CircleImageView circleImageView;
        final private TextView name, message, textCount;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.chats_profile_image);
            name = itemView.findViewById(R.id.chats_username);
            message = itemView.findViewById(R.id._chat_message);
            textCount = itemView.findViewById(R.id.messageCount);
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
