package com.example.musica.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.musica.Model.SongModel;
import com.example.musica.MusicPlayerActivity;
import com.example.musica.Object.MyExoplayer;
import com.example.musica.R; // Replace with your actual R.java file
import com.example.musica.databinding.SongsItemRowBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso; // Include Picasso library dependency

import java.util.List;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.MyViewHolder> {

    private final List<String> songIdList;

    public SongListAdapter(List<String> songIdList) {
        this.songIdList = songIdList;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private final SongsItemRowBinding binding;

        public MyViewHolder(SongsItemRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindData(String songId) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("songs")
                    .document(songId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                SongModel song = documentSnapshot.toObject(SongModel.class);
                                if (song != null) {
                                    // Update UI elements based on retrieved song data
                                    binding.nameSongs.setText(song.getName());
                                    binding.artistsSongs.setText(song.getArtists());
                                    Glide.with(binding.getRoot().getContext())
                                            .load(song.getImgUrl())
                                            .apply(RequestOptions.centerCropTransform()
                                                    .transform(new RoundedCorners(32)))
                                            .into(binding.imgSongs);
                                    binding.getRoot().setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Context context = v.getContext();
                                            MyExoplayer.startPlaying(context, song);
                                            context.startActivity(new Intent(context, MusicPlayerActivity.class));

                                        }
                                    });
                                }
                            } else {
                                // Handle the case where the document doesn't exist
                            }
                        }
                    });
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        SongsItemRowBinding binding = SongsItemRowBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bindData(songIdList.get(position));
    }

    @Override
    public int getItemCount() {
        return songIdList.size();
    }
}
