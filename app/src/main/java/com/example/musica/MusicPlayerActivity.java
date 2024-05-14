package com.example.musica;
import static com.example.musica.Adapter.PlaylistAdapter.playlistList;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.media3.exoplayer.ExoPlayer;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.musica.Adapter.PlaylistAdapter;
import com.example.musica.Fragment.SubFragment.AddSongListFragment;
import com.example.musica.Fragment.SubFragment.AddSongToPlaylistFragment;
import com.example.musica.Model.SongModel;
import com.example.musica.Object.MyExoplayer;
import com.example.musica.databinding.ActivityMusicPlayerBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MusicPlayerActivity extends AppCompatActivity {
    private SeekBar seekBar;
    FirebaseUser users;
    FirebaseAuth auth;
    private TextView currentTimeTextView;
    private TextView totalTimeTextView;
    private final boolean isSeekBarDragging = false;
    private final Handler handler = new Handler();
    private ImageView pausePlayButton;
    private final boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PlaylistAdapter playlistAdapter = new PlaylistAdapter(playlistList, this);
        com.example.musica.databinding.ActivityMusicPlayerBinding binding = ActivityMusicPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SongModel currentSong = MyExoplayer.getCurrentSong();
        String songId = getIntent().getStringExtra("songId");
        Bundle bundle = new Bundle();
        bundle.putString("songId", songId);
        playlistAdapter.setSongId(songId);
        playlistAdapter.setCurrentSongId(songId);

        AddSongToPlaylistFragment addSongListFragment = new AddSongToPlaylistFragment();
        addSongListFragment.setArguments(bundle);
        // Log để kiểm tra songId đã lấy được hay chưa
        Log.d("MusicPlayerActivity", "Song ID: " + songId);

        // Initialize views
        pausePlayButton = binding.pausePlay;
        seekBar = binding.seekBar;
        currentTimeTextView = binding.currentTime;
        totalTimeTextView = binding.totalTime;
        ImageView backBtn = binding.backBtn;
        if (bundle != null) {
            String userId = bundle.getString("userId");

            if (userId != null) {
                users = FirebaseAuth.getInstance().getCurrentUser();

            }
        } else {
            // Bundle không tồn tại, xử lý tương ứng nếu cần
            Log.d("MusicPlayerActivity", "Bundle is null");
        }
        binding.moreBtn.setOnClickListener(v -> {
            // Create a dialog and set the layout
            Dialog dialog = new Dialog(MusicPlayerActivity.this);
            dialog.setContentView(R.layout.more_option_dialog);
            // Set dialog properties
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setGravity(Gravity.BOTTOM);
            dialog.getWindow().setDimAmount(0.8f);
            dialog.setCancelable(true);

            LinearLayout layoutAdd = dialog.findViewById(R.id.layoutAdd);
            ImageView backBtn1 = dialog.findViewById(R.id.backBtn1);
            layoutAdd.setOnClickListener(v1 -> {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.add(android.R.id.content, addSongListFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                dialog.dismiss();
            });
            // Show the dialog
            dialog.show();
        });
        backBtn.setOnClickListener(v -> finish());
        pausePlayButton.setOnClickListener(v -> togglePause());

        // Set up SeekBar change listener
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        backBtn = findViewById(R.id.backBtn);
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
