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
import com.example.musica.databinding.ArtistsItemRowBinding;

import java.util.List;

public class ArtistsAdapter extends RecyclerView.Adapter<ArtistsAdapter.MyViewHolder> {

    private List<ArtistsModel> artistsList;

    public ArtistsAdapter(List<ArtistsModel> artistsList) {
        this.artistsList = artistsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ArtistsItemRowBinding binding = ArtistsItemRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ArtistsModel artist = artistsList.get(position);
        holder.bind(artist);
    }

    @Override
    public int getItemCount() {
        return artistsList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private final ArtistsItemRowBinding binding;

        MyViewHolder(@NonNull ArtistsItemRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ArtistsModel artist) {
            binding.nameTextView.setText(artist.getName());
            Glide.with(binding.getRoot().getContext())
                    .load(artist.getImgUrl())
                    .into(binding.imgArtists);
        }
    }
}
