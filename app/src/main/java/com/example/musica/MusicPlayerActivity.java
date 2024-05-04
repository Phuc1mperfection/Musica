package com.example.musica;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.exoplayer.ExoPlayer;

import com.example.musica.Object.MyExoplayer;
import com.example.musica.R;
import com.example.musica.databinding.*;
import com.example.musica.Model.SongModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class MusicPlayerActivity extends AppCompatActivity {
    private SeekBar seekBar;
    private TextView currentTimeTextView;
    private TextView totalTimeTextView;
    private boolean isSeekBarDragging = false;
    private Handler handler = new Handler();
    private ActivityMusicPlayerBinding binding;
    private ExoPlayer exoPlayer;
    private ImageView imgSongs, pausePlayButton;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMusicPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SongModel currentSong = MyExoplayer.getCurrentSong();
        // Initialize views

        imgSongs = binding.imgSongs;
        pausePlayButton = binding.pausePlay;
        seekBar = binding.seekBar;
        currentTimeTextView = binding.currentTime;
        totalTimeTextView = binding.totalTime;

        // Set click listener for pause/play button
        pausePlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePause();
            }
        });

        // Set up SeekBar change listener
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        // Update SeekBar and time TextViews
        updateSeekBar();
        // Set up handler to update SeekBar and time TextViews periodically
        handler.postDelayed(seekBarUpdater, 1000); // Update every second
        if (currentSong != null) {
            binding.nameSongs.setText(currentSong.getName());
            binding.artistsSongs.setText(currentSong.getArtists());
            Glide.with(binding.getRoot().getContext())
                    .load(currentSong.getImgUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.imgSongs);
        }

        // Get the ExoPlayer instance from MyExoplayer
        exoPlayer = MyExoplayer.getInstance();
    }

    // SeekBar change listener
    private final SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                // User is dragging the SeekBar, seek to the selected position
                ExoPlayer exoPlayer = MyExoplayer.getInstance();
                if (exoPlayer != null) {
                    exoPlayer.seekTo(progress);
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // No action needed
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // No action needed
        }
    };

    // Runnable to update SeekBar and time TextViews periodically
    private final Runnable seekBarUpdater = new Runnable() {
        @Override
        public void run() {
            updateSeekBar();
            handler.postDelayed(this, 1000); // Update every second
        }
    };

    // Method to update SeekBar and time TextViews
    private void updateSeekBar() {
        ExoPlayer exoPlayer = MyExoplayer.getInstance();
        if (exoPlayer != null) {
            int currentTimeMillis = (int) exoPlayer.getCurrentPosition();
            int totalTimeMillis = (int) exoPlayer.getDuration();

            String currentTimeString = formatTime(currentTimeMillis);
            String totalTimeString = formatTime(totalTimeMillis);

            currentTimeTextView.setText(currentTimeString);
            totalTimeTextView.setText(totalTimeString);

            // Update seekbar progress
            seekBar.setMax(totalTimeMillis);
            seekBar.setProgress(currentTimeMillis);
        }
    }

    // Method to toggle pause/play
    private void togglePause() {
        ExoPlayer exoPlayer = MyExoplayer.getInstance();
        if (exoPlayer != null) {
            boolean isPlaying = exoPlayer.isPlaying();
            if (isPlaying) {
                exoPlayer.pause();
                pausePlayButton.setImageResource(R.drawable.baseline_play_circle_24);
            } else {
                exoPlayer.play();
                pausePlayButton.setImageResource(R.drawable.baseline_pause_circle_outline_24);
            }
        }
    }

    // Method to format time in mm:ss format
    private String formatTime(int millis) {
        int seconds = millis / 1000;
        int minutes = seconds / 60;
        int hours = minutes / 60;
        seconds %= 60;
        minutes %= 60;
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }


}
