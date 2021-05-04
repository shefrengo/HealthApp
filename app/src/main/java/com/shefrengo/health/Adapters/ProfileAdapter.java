package com.shefrengo.health.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shefrengo.health.Models.Data;
import com.shefrengo.health.Models.Posts;
import com.shefrengo.health.R;
import com.shefrengo.health.SetTime;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder>{

    private OnItemClickListener onItemClickListener;
    private List<Data>dataList;
    private List<Posts> postsList;
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    public ProfileAdapter(List<Posts> postsList, Context context,List<Data>dataList) {
        this.postsList = postsList;
        this.context = context;
        this.dataList = dataList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String image = postsList.get(position).getImageUrl();
        String userid = postsList.get(position).getUserid();
        String timestamp = postsList.get(position).getTimestamp().toString();
        String category = postsList.get(position).getCategory();
        String  username = dataList.get(position).getUsername();
        String photo = dataList.get(position).getProfilePhotoUrl();

        int replyCount = postsList.get(position).getReplyCount();
        String replyText = postsList.get(position).getReplyCount()+" Replies";

        if (replyCount ==0){
            holder.replyCount.setText(context.getResources().getString(R.string.first_reply));
        }else {
            holder.replyCount.setText(replyText);
        }

        holder.title.setText(postsList.get(position).getTitle());
        holder.description.setText(postsList.get(position).getDescription());
        if (image.isEmpty()){
            holder.imageView.setVisibility(View.GONE);
        }
        Glide.with(context).asBitmap().load(postsList.get(position).getImageUrl()).into(holder.imageView);
        Glide.with(context).asBitmap().load(photo).into(holder.circleImageView);


        String text =" By @"+username+" in "+category +"'s forum .."+ SetTime.TwitterTimeDifferentitaion(timestamp);
        holder.category.setText(text.toLowerCase());

    }


    @Override
    public int getItemCount() {
        return postsList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView title,description,category,replyCount;
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

            if (onItemClickListener !=null){
                int postition =getAdapterPosition();
                if (postition!=RecyclerView.NO_POSITION){
                    onItemClickListener.onItemClick(postition);
                }
            }
        }
    }

    public interface OnItemClickListener{

        void onItemClick(int position);
    }
}
