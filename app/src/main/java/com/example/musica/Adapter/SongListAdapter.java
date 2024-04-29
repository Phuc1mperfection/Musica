package com.example.musica.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musica.Model.SongModel;
import com.example.musica.R; // Replace with your actual R.java file
import com.squareup.picasso.Picasso; // Include Picasso library dependency

import java.util.List;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.SongViewHolder> {

    private Context context;
    private List<SongModel> songList;

    public SongListAdapter(Context context, List<SongModel> songList) {
        this.context = context;
        this.songList = songList;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_song_list, parent, false); // Replace with your actual song_item_layout.xml file
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        SongModel song = songList.get(position);

        // Set song data to views
        holder.txtSongName.setText(song.getName());
        holder.txtArtists.setText(song.getArtists());

        // Load and set song image using Picasso (replace with your image loading library)
        Picasso.get()
                .load(song.getImgUrl())
                .placeholder(R.drawable.ic_launcher_foreground) // Placeholder image while loading
                .error(R.drawable.playing) // Optional error image in case of loading issues
                .into(holder.imgSong);
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    // Define the ViewHolder class
    static class SongViewHolder extends RecyclerView.ViewHolder {

        TextView txtSongName, txtArtists;
        ImageView imgSong;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);

            txtSongName = itemView.findViewById(R.id.nameSongs);
            txtArtists = itemView.findViewById(R.id.artistsSongs);
            imgSong = itemView.findViewById(R.id.imgSongs);
        }
    }
}
