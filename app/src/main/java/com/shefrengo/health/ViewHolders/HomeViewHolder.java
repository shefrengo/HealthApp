package com.shefrengo.health.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shefrengo.health.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView title, description, category, replyCount;
    public CircleImageView circleImageView;
    public ImageView imageView;

    public HomeViewHolder(@NonNull View itemView) {
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

    }
}
