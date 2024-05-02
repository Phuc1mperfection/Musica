package com.example.musica;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.exoplayer.ExoPlayer;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.musica.Object.MyExoplayer;
import com.example.musica.databinding.ActivityMusicPlayerBinding;
import com.example.musica.Model.SongModel; // Import the SongModel class

public class MusicPlayerActivity extends AppCompatActivity {

    private ActivityMusicPlayerBinding binding;
    private ExoPlayer exoPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMusicPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get the current song from MyExoplayer
        SongModel currentSong = MyExoplayer.getCurrentSong();

        // Update UI with song title and artist
        if (currentSong != null) {
            binding.nameSongs.setText(currentSong.getName());
            binding.artistsSongs.setText(currentSong.getArtists());
            Glide.with(binding.getRoot().getContext())
                    .load(currentSong.getImgUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.imgSongs);

            // Get the ExoPlayer instance from MyExoplayer
            exoPlayer = MyExoplayer.getInstance();

            // Check if exoPlayer is null
            if (exoPlayer == null) {
                // Handle the case where exoPlayer is null
                // For example, show an error message or take appropriate action
            } else {
                binding.playerView.setPlayer(exoPlayer);
            }
        }
    }

}
