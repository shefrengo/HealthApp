package com.shefrengo.health.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shefrengo.health.Models.Data;
import com.shefrengo.health.Models.Posts;
import com.shefrengo.health.R;
import com.shefrengo.health.SetTime;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private OnItemClickListener onItemClickListener;
    private List<Posts> postsList;
    private Context context;
    private List<com.shefrengo.health.Models.Data> dataList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public HomeAdapter(List<Posts> postsList, Context context, List<Data> dataList) {
        this.postsList = postsList;
        this.context = context;
        this.dataList = dataList;
        setHasStableIds(true);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String image = postsList.get(position).getImageUrl();

        String timestamp = postsList.get(position).getTimestamp().toString();
        String category = postsList.get(position).getCategory();

        int replyCount = postsList.get(position).getReplyCount();
        String replyText = postsList.get(position).getReplyCount() + " Replies";

        if (replyCount == 0) {
            holder.replyCount.setText(context.getResources().getString(R.string.first_reply));
        } else {
            holder.replyCount.setText(replyText);
        }

        holder.title.setText(postsList.get(position).getTitle());
        holder.description.setText(postsList.get(position).getDescription());
        if (image.isEmpty()) {
            holder.imageView.setVisibility(View.GONE);
        }
        Glide.with(context).asBitmap().load(postsList.get(position).getImageUrl()).into(holder.imageView);

        String username = dataList.get(position).getUsername();
        String profilePhotoUrl = dataList.get(position).getProfilePhotoUrl();

        String text = " By @" + username + " in " + category + "'s forum ..." + SetTime.TwitterTimeDifferentitaion(timestamp);
        holder.category.setText(text);
        Glide.with(context).asBitmap().load(profilePhotoUrl).into(holder.circleImageView);

    }

    @Override
    public int getItemCount() {

        return postsList.size();
    }

    @SuppressLint("SetTextI18n")
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title, description, category, replyCount;
        private CircleImageView circleImageView;
        private ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.short_description);
            category = itemView.findViewById(R.id.category);
            replyCount = itemView.findViewById(R.id.reply_count);
            circleImageView = itemView.findViewById(R.id.profile_pic);
            imageView = itemView.findViewById(R.id.image);
            itemView.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {

            if (onItemClickListener != null) {
                int postition = getAdapterPosition();
                if (postition != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(postition);
                }
            }
        }
    }

    public interface OnItemClickListener {

        void onItemClick(int position);
    }
}