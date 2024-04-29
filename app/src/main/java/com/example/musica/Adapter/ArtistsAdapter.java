package com.example.musica.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musica.Model.ArtistsModel;
import com.example.musica.R;

import java.util.List;

public class ArtistsAdapter extends RecyclerView.Adapter<ArtistsAdapter.MyViewHolder> {

    private List<ArtistsModel> artistsList;
    private Context context;

    public ArtistsAdapter(Context context, List<ArtistsModel> artistsList) {
        this.context = context;
        this.artistsList = artistsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.artists_item_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ArtistsModel artist = artistsList.get(position);
        holder.nameTextView.setText(artist.getName());
        // Load image using Glide (see previous example)
        Glide.with(context)
                .load(artist.getImgUrl())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return artistsList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private ImageView imageView;

        MyViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            imageView = itemView.findViewById(R.id.imgArtists);
        }
    }
}
