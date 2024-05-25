package com.example.musica;

import static com.example.musica.Adapter.PlaylistAdapter.playlistList;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.musica.Adapter.PlaylistAdapter;
import com.example.musica.Fragment.SubFragment.AddSongListFragment;
import com.example.musica.Fragment.SubFragment.AddSongToPlaylistFragment;
import com.example.musica.Model.SongModel;
import com.example.musica.Object.MyExoplayer;
import com.example.musica.databinding.ActivityMusicPlayerBinding;
import com.example.musica.databinding.MiniPlayerLayoutBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MusicPlayerActivity extends AppCompatActivity {
    private SeekBar seekBar;
    FirebaseUser users;
    FirebaseAuth auth;
    private TextView currentTimeTextView;
    private TextView totalTimeTextView;
    private boolean isSeekBarDragging = false;
    private final Handler handler = new Handler();
    private ImageView imgSongs, pausePlayButton;
    private final boolean isPlaying = false;
    private ActivityMusicPlayerBinding binding;
    private static ExoPlayer exoPlayer;
    private ImageView backBtn;
    private View fullPlayer, miniPlayer;
    private ImageView miniImgSongs, miniPausePlay;
    private TextView miniNameSongs, miniArtistsSongs;
    private GestureDetector gestureDetector;
    private RelativeLayout parentLayout;
    private ImageView nextBtn, previousBtn;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        exoPlayer = MyExoplayer.getExoPlayer();

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
        nextBtn = binding.next;
        previousBtn = binding.previous;
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

        // Set click listener for next and previous buttons
        nextBtn.setOnClickListener(v -> playNextSong());
        previousBtn.setOnClickListener(v -> playPreviousSong());

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
        handler.postDelayed(seekBarUpdater, 1000);

        if (MyExoplayer.getExoPlayer() != null) {
            MyExoplayer.getExoPlayer().addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int state) {
                    if (state == Player.STATE_ENDED) {
                        playNextSong();
                    }
                }
            });
        }
    }

    private final SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser && exoPlayer != null) {
                long duration = exoPlayer.getDuration();
                long newPosition = (duration * progress) / 100;
                exoPlayer.seekTo(newPosition);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isSeekBarDragging = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            isSeekBarDragging = false;
        }
    };

    private final Runnable seekBarUpdater = new Runnable() {
        @Override
        public void run() {
            updateSeekBar();
            handler.postDelayed(this, 1000);
        }
    };

    private void updateSeekBar() {
        if (exoPlayer != null && !isSeekBarDragging) {
            long currentPosition = exoPlayer.getCurrentPosition();
            long duration = exoPlayer.getDuration();

            if (duration > 0) {
                int progress = (int) ((currentPosition * 100) / duration);
                seekBar.setProgress(progress);
            }

            currentTimeTextView.setText(formatTime(currentPosition));
            totalTimeTextView.setText(formatTime(duration));
        }
    }

    @SuppressLint("DefaultLocale")
    private String formatTime(long timeInMillis) {
        int totalSeconds = (int) (timeInMillis / 1000);
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    private void togglePause() {
        ExoPlayer player = MyExoplayer.getExoPlayer();
        if (player != null) {
            if (player.isPlaying()) {
                player.pause();
                pausePlayButton.setImageResource(R.drawable.baseline_play_circle_24);
                miniPausePlay.setImageResource(R.drawable.baseline_play_circle_24);
            } else {
                player.play();
                pausePlayButton.setImageResource(R.drawable.baseline_pause_circle_outline_24);
                miniPausePlay.setImageResource(R.drawable.baseline_pause_circle_outline_24);
            }
        }
    }

    private void expandFullPlayer() {
        fullPlayer.setVisibility(View.VISIBLE);
        miniPlayer.setVisibility(View.GONE);
    }

    private void minimizePlayer() {
        fullPlayer.setVisibility(View.GONE);
        miniPlayer.setVisibility(View.VISIBLE);
    }

    private void playNextSong() {
        MyExoplayer.playNextSong();
        updateUIWithCurrentSong();

    }

    private void playPreviousSong() {
        MyExoplayer.playPreviousSong();
        updateUIWithCurrentSong();

    }

    private void updateUIWithCurrentSong() {
        SongModel currentSong = MyExoplayer.getCurrentSong();
        if (currentSong != null) {
            binding.nameSongs.setText(currentSong.getName());
            binding.artistsSongs.setText(currentSong.getArtists());
            Glide.with(this)
                    .load(currentSong.getImgUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.imgSongs);

            miniNameSongs.setText(currentSong.getName());
            miniArtistsSongs.setText(currentSong.getArtists());
            Glide.with(this)
                    .load(currentSong.getImgUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(miniImgSongs);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(seekBarUpdater);
    }
}