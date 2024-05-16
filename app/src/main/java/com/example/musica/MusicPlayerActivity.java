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
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.exoplayer.ExoPlayer;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.musica.Model.SongModel;
import com.example.musica.Object.MyExoplayer;
import com.example.musica.databinding.ActivityMusicPlayerBinding;
import com.example.musica.databinding.MiniPlayerLayoutBinding;

public class MusicPlayerActivity extends AppCompatActivity {
    private SeekBar seekBar;
    FirebaseUser users;
    FirebaseAuth auth;
    private TextView currentTimeTextView;
    private TextView totalTimeTextView;
    private final boolean isSeekBarDragging = false;
    private final Handler handler = new Handler();
    private ImageView imgSongs, pausePlayButton;
    private final boolean isPlaying = false;
    private ActivityMusicPlayerBinding binding;
    private ExoPlayer exoPlayer;
    private ImageView backBtn;
    private View fullPlayer, miniPlayer;
    private ImageView miniImgSongs, miniPausePlay;
    private TextView miniNameSongs, miniArtistsSongs;
    private GestureDetector gestureDetector;
    private RelativeLayout parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize binding before calling setContentView
        binding = ActivityMusicPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize views
        fullPlayer = binding.fullPlayer;
        miniPlayer = binding.miniPlayer.getRoot();
        imgSongs = binding.imgSongs;
        pausePlayButton = binding.pausePlay;
        seekBar = binding.seekBar;
        currentTimeTextView = binding.currentTime;
        totalTimeTextView = binding.totalTime;
        backBtn = binding.backBtn;
        parentLayout = binding.parentLayout;

        // Mini player views
        MiniPlayerLayoutBinding miniPlayerBinding = MiniPlayerLayoutBinding.bind(miniPlayer);
        miniImgSongs = miniPlayerBinding.miniImgSongs;
        miniPausePlay = miniPlayerBinding.miniPausePlay;
        miniNameSongs = miniPlayerBinding.miniNameSongs;
        miniArtistsSongs = miniPlayerBinding.miniArtistsSongs;

        // Back button listener
        backBtn.setOnClickListener(view -> minimizePlayer());

        // Set up gesture detector for swipe down to minimize
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1.getY() - e2.getY() > 50) {
                    minimizePlayer();
                    return true;
                }
                return false;
            }
        });

        parentLayout.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

        // Set touch listener for full player to detect swipe gestures
        fullPlayer.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

        // Set click listener for pause/play button
        pausePlayButton.setOnClickListener(v -> togglePause());

        // Mini player click listener to expand full player
        miniPlayer.setOnClickListener(v -> expandFullPlayer());
        miniPausePlay.setOnClickListener(v -> togglePause());

        PlaylistAdapter playlistAdapter = new PlaylistAdapter(playlistList, this);
        SongModel currentSong = MyExoplayer.getCurrentSong();
        String songId = getIntent().getStringExtra("songId");
        Bundle bundle = new Bundle();
        bundle.putString("songId", songId);
        playlistAdapter.setSongId(songId);
        playlistAdapter.setCurrentSongId(songId);

        AddSongToPlaylistFragment addSongListFragment = new AddSongToPlaylistFragment();
        addSongListFragment.setArguments(bundle);

        // Log to check if songId has been retrieved
        Log.d("MusicPlayerActivity", "Song ID: " + songId);

        if (currentSong != null) {
            binding.nameSongs.setText(currentSong.getName());
            binding.artistsSongs.setText(currentSong.getArtists());
            Glide.with(binding.getRoot().getContext())
                    .load(currentSong.getImgUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.imgSongs);
        }

        if (currentSong != null) {
            miniNameSongs.setText(currentSong.getName());
            miniArtistsSongs.setText(currentSong.getArtists());
            Glide.with(this)
                    .load(currentSong.getImgUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(miniImgSongs);
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

        pausePlayButton.setOnClickListener(v -> togglePause());

        // Set up SeekBar change listener
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        // Update SeekBar and time TextViews
        updateSeekBar();

        // Set up handler to update SeekBar and time TextViews periodically
        handler.postDelayed(seekBarUpdater, 1000); // Update every second
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
                miniPausePlay.setImageResource(R.drawable.baseline_play_circle_24);
            } else {
                exoPlayer.play();
                pausePlayButton.setImageResource(R.drawable.baseline_pause_circle_outline_24);
                miniPausePlay.setImageResource(R.drawable.baseline_pause_circle_outline_24);
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

    private void minimizePlayer() {
        fullPlayer.setVisibility(View.GONE);
        miniPlayer.setVisibility(View.VISIBLE);
        miniPlayer.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in));
    }

    // Method to expand full player

    private void expandFullPlayer() {
        miniPlayer.setVisibility(View.GONE);
        fullPlayer.setVisibility(View.VISIBLE);
        fullPlayer.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out));
    }
}
