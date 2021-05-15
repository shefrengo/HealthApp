package com.shefrengo.health.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shefrengo.health.Models.Users;
import com.shefrengo.health.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
    private OnChatListListener onChatListListener;
    private List<Users>usersList;
    private Context context;

    public ChatListAdapter(List<Users> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;
    }

    public void setOnChatListListener(OnChatListListener onChatListListener) {
        this.onChatListListener = onChatListListener;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chatlist_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        String photo = usersList.get(position).getProfilePhotoUrl();
        String name = usersList.get(position).getUsername();
        holder.name.setText(name);
        Glide.with(context).asBitmap().load(photo).into(holder.circleImageView);

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    class  ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private CircleImageView circleImageView;
        private TextView name;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.chats_profile_image);
            name = itemView.findViewById(R.id.chats_username);
          itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (onChatListListener!=null){
                int position = getAdapterPosition();
                if (position!=RecyclerView.NO_POSITION){
                    onChatListListener.onChatListClick(position);
                }
            }
        }

    }
    public interface OnChatListListener{
        void onChatListClick(int position);
    }
}
