package com.example.musica;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.musica.Fragment.BottomMenuFragment.HomeFragment;
import com.example.musica.Fragment.BottomMenuFragment.LibraryFragment;
import com.example.musica.Fragment.BottomMenuFragment.SearchFragment;
import com.example.musica.Fragment.BottomMenuFragment.UserFragment;
import com.example.musica.Fragment.SubFragment.AddSongToPlaylistFragment;
import com.example.musica.Model.SongModel;
import com.example.musica.Object.MyExoplayer;
import com.example.musica.Utils.PlayerStateManager;
import com.example.musica.databinding.ActivityMainBinding;
import com.example.musica.databinding.MiniPlayerLayoutBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private ImageView miniImgSongs, miniPausePlay;
    private TextView miniNameSongs, miniArtistsSongs;
    private ImageView pausePlayButton;
    private PlayerStateManager playerStateManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        View miniPlayer = binding.miniPlayer.getRoot();
        MiniPlayerLayoutBinding miniPlayerBinding = MiniPlayerLayoutBinding.bind(miniPlayer);
        miniImgSongs = miniPlayerBinding.miniImgSongs;
        miniPausePlay = miniPlayerBinding.miniPausePlay;
        miniNameSongs = miniPlayerBinding.miniNameSongs;
        miniArtistsSongs = miniPlayerBinding.miniArtistsSongs;
        playerStateManager = PlayerStateManager.getInstance();

        // Register the state listener
        playerStateManager.addListener(this::updatePausePlayButtonState);
        miniPausePlay.setImageResource(R.drawable.baseline_play_circle_24);
        if (currentUser != null) {
            String userId = currentUser.getUid();
            Log.d("MainActivity", "User ID: " + userId);
            // Gọi fragment LibraryFragment và truyền userId vào đó
            LibraryFragment libraryFragment = new LibraryFragment();
            AddSongToPlaylistFragment addSongToPlaylistFragment = new AddSongToPlaylistFragment();

            Bundle bundle = new Bundle();
            bundle.putString("userId", userId);

            libraryFragment.setArguments(bundle);
            addSongToPlaylistFragment.setArguments(bundle);
            replaceFragment(libraryFragment);
            replaceFragment(addSongToPlaylistFragment);

        } else {
            Log.d("MainActivity", "User not logged in");
        }

        // Kiểm tra intent để xác định xem cần mở HomeFragment hay không
        if (getIntent().getBooleanExtra("goToHomeFragment", false)) {
            replaceFragment(new HomeFragment());
        } else {
            replaceFragment(new HomeFragment());
        }
        binding.bottomNavigationView.setBackground(null);
        setupBottomNavigation();
        SongModel currentSong = MyExoplayer.getCurrentSong();

        miniPlayer.setOnClickListener(v -> {
            Log.d("MainActivity", "Mini player clicked. Starting MusicPlayerActivity...");
            miniPlayer.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_in_bottom));
            Intent intent = new Intent(MainActivity.this, MusicPlayerActivity.class);
            startActivity(intent);
            overridePendingTransition(0,R.anim.slide_in_bottom); // Apply the slide-in animation
        });
        miniPausePlay.setOnClickListener(v -> togglePause());
        CollectionReference songsRef = FirebaseFirestore.getInstance().collection("songs");
        songsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<SongModel> playlistFromFirebase = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Lấy dữ liệu của mỗi bài hát từ Firestore và thêm vào danh sách bài hát
                    SongModel song = document.toObject(SongModel.class);
                    playlistFromFirebase.add(song);
                }
                // Sau khi lấy được danh sách bài hát từ Firebase, tiếp tục xử lý
                handlePlaylistFromFirebase(playlistFromFirebase);
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
        MyExoplayer.initializePlayer(this);
        MyExoplayer.getExoPlayer().addListener(new Player.Listener() {
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                Player.Listener.super.onMediaItemTransition(mediaItem, reason);
                updateUIWithCurrentSong();
            }
        });
        if (currentSong != null) {
            miniNameSongs.setText(currentSong.getName());
            miniArtistsSongs.setText(currentSong.getArtists());
            Glide.with(this)
                    .load(currentSong.getImgUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(miniImgSongs);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        // Update the button state based on the current player state
        updatePausePlayButtonState(playerStateManager.isPlaying());
    }
    private void handlePlaylistFromFirebase(List<SongModel> playlistFromFirebase) {
        // Lấy bài hát đầu tiên từ danh sách làm bài hát mặc định (nếu có)
        SongModel defaultSong = null;
        if (!playlistFromFirebase.isEmpty()) {
            defaultSong = playlistFromFirebase.get(7); //Your Love Is King
        }
        MyExoplayer.initializePlayer(this);
        MyExoplayer.setSongList(playlistFromFirebase);
        if (defaultSong != null) {
            MyExoplayer.startPlaying(this, defaultSong, playlistFromFirebase);
            MyExoplayer.pause();
            miniPausePlay.setImageResource(R.drawable.baseline_play_circle_24);
        }
    }
    private void updateUIWithCurrentSong() {
        SongModel currentSong = MyExoplayer.getCurrentSong();
        if (currentSong != null && !isDestroyed() && !isFinishing()) {
            Log.d("MainActivity", "Updating UI with current song: " + currentSong.getName());

            miniNameSongs.setText(currentSong.getName());
            miniArtistsSongs.setText(currentSong.getArtists());
            Glide.with(this)
                    .load(currentSong.getImgUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(miniImgSongs);

            // Set the initial state to play
            miniPausePlay.setImageResource(R.drawable.baseline_pause_circle_outline_24);
            Log.d("MainActivity", "Mini Player updated with song: " + currentSong.getName());
        } else {
            Log.d("MainActivity", "No current song found or Activity is destroyed");
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.frame_layout);

        if (currentFragment instanceof HomeFragment) {
            Log.d("MainActivity", "HomeFragment is visible");
        } else {
            // If there are more than one fragments in the back stack, pop the back stack
            if (fragmentManager.getBackStackEntryCount() > 1) {
                fragmentManager.popBackStack();
            } else {
                // If only one fragment is left (HomeFragment), then finish the activity
                finish();
            }
        }
    }
    private void updatePausePlayButtonState(boolean isPlaying) {
        int resId = isPlaying ? R.drawable.baseline_pause_circle_outline_24 : R.drawable.baseline_play_circle_24;
        miniPausePlay.setImageResource(resId);
        if (pausePlayButton != null) {
            pausePlayButton.setImageResource(resId);
        }
    }

    private void togglePause() {
        ExoPlayer player = MyExoplayer.getExoPlayer();
        if (player != null) {
            if (player.isPlaying()) {
                player.pause();
                playerStateManager.setPlaying(false);
            } else {
                player.play();
                playerStateManager.setPlaying(true);
            }
        }
    }
    private void setupBottomNavigation() {
        binding.bottomNavigationView.setBackground(null);
//        binding.floatingbtn.setOnClickListener(v -> openCreatePlaylistAlertDialog());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Store the resource ID in a variable

            if (itemId == R.id.homepage) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.subscriptions) {
                replaceFragment(new SearchFragment());
            } else if (itemId == R.id.library) {
                replaceFragment(new LibraryFragment());
            } else if (itemId == R.id.user) {
                replaceFragment(new UserFragment());
            }
            return true;
        });
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            Bundle bundle = new Bundle();
            bundle.putString("userId", userId);

            fragment.setArguments(bundle);

            fragmentTransaction.replace(R.id.frame_layout, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            Log.d("MainActivity", "No current user");
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        playerStateManager.removeListener(this::updatePausePlayButtonState);
    }
}