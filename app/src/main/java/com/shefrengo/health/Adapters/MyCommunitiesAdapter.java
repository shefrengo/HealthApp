package com.shefrengo.health.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shefrengo.health.Models.Communities;
import com.shefrengo.health.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyCommunitiesAdapter extends RecyclerView.Adapter<MyCommunitiesAdapter.ViewHolder> {
    private List<Communities> communities;
    private Context context;
    private CommunityAdapter.OnItemClickListener onItemClickListener;

    public MyCommunitiesAdapter(List<Communities> communities, Context context) {
        this.communities = communities;
        this.context = context;
    }

    public void setOnItemClickListener(CommunityAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_community, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
     Glide.with(context).asBitmap().load(communities.get(position).getImageUrl()).into(holder.circleImageView);
        holder.textView.setText(communities.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return communities.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CircleImageView circleImageView;
        private TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.notificatin_type);
            circleImageView = itemView.findViewById(R.id.notification_profile_pic2);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (onItemClickListener != null) {
                int position = getAbsoluteAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(position);
                }
            }

        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
