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
import com.bumptech.glide.request.RequestOptions;
import com.example.musica.Model.ArtistsModel;
import com.example.musica.R;
import com.example.musica.databinding.ItemArtistsBinding;

import java.util.List;

public class ArtistsAdapter extends RecyclerView.Adapter<ArtistsAdapter.MyViewHolder> {

    private List<ArtistsModel> artistsList;

    public ArtistsAdapter(List<ArtistsModel> artistsList) {
        this.artistsList = artistsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemArtistsBinding binding = ItemArtistsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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

        private final ItemArtistsBinding binding;

        MyViewHolder(@NonNull ItemArtistsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ArtistsModel artist) {
            binding.nameTextView.setText(artist.getName());

            RequestOptions requestOptions = new RequestOptions()
                    .circleCrop(); // Apply the circular crop transformation

            Glide.with(binding.getRoot().getContext())
                    .load(artist.getImgUrl())
                    .apply(requestOptions) // Apply the requestOptions to the Glide request
                    .into(binding.imgArtists);
        }

    }
}
