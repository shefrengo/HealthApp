package com.shefrengo.health.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shefrengo.health.Models.Users;
import com.shefrengo.health.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GridViewAdapter extends RecyclerView.Adapter<GridViewAdapter.ViewHolder> {
    private List<Users> usersList;
    private Context context;
    LayoutInflater inflter;


    public GridViewAdapter(List<Users> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;
        inflter = (LayoutInflater.from(context));
    }



    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_us_grid_layout,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

        Glide.with(context).asBitmap().load(usersList.get(position).getProfilePhotoUrl()).into(holder.circleImageView);
        String names = usersList.get(position).getName();
        String surname = usersList.get(position).getSurname();

        holder.name.setText(new StringBuilder().append(names).append(" ").append(surname).toString());


      holder.  title.setText(R.string.administrator);

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView name, title;

        public ViewHolder(@NonNull @NotNull View view) {
            super(view);

            circleImageView = view.findViewById(R.id.contact_profile);
            name = view.findViewById(R.id.admin_name);
            title = view.findViewById(R.id.admin_title);
        }
    }
}
