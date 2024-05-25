package com.example.musica;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import java.util.concurrent.atomic.AtomicInteger;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.musica.Adapter.SongListAdapter;
import com.example.musica.Fragment.SubFragment.AddSongListFragment;
import com.example.musica.databinding.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlaylistDetailActivity extends AppCompatActivity {

    private ActivityPlaylistDetailBinding binding;
    private List<String> songIdList;
    private FirebaseFirestore db;
    private  SongListAdapter songListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlaylistDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        songIdList = new ArrayList<>(); // Initialize songIdList
        binding.musicRecyclerViewPlaylist.setLayoutManager(new LinearLayoutManager(this));
        songListAdapter = new SongListAdapter(songIdList);
        binding.musicRecyclerViewPlaylist.setAdapter(new SongListAdapter(songIdList));
        Intent intent = getIntent();
        if (intent != null) {
            Log.d(TAG, "Intent received");
            String playlistName = intent.getStringExtra("playlistName");
            if (playlistName != null) {
                binding.playlistName.setText(playlistName); // Update playlist name using data binding
            } else {
                Log.d(TAG, "Playlist name not found in Intent");
            }
            String userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
            db.collection("playlists")
                    .whereEqualTo("userID", userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            songIdList.clear();  // Clear existing playlists
                            if (task.getResult().isEmpty()) {
                                Toast.makeText(PlaylistDetailActivity.this, "You don't have any playlists yet.", Toast.LENGTH_SHORT).show();
                            } else {
                                for (DocumentSnapshot doc : task.getResult()) {
                                    // Check if document name matches playlist name from Intent (optional)
                                    if (Objects.equals(doc.getString("name"), playlistName)) {
                                        List<String> retrievedSongIdList = (List<String>) doc.get("songs");
                                        if (retrievedSongIdList != null) {
                                            songIdList.addAll(retrievedSongIdList);
                                        } else {
                                            Log.d(TAG, "Songs not found in playlist document");
                                        }
                                        break;
                                    }
                                }
                            }
                            Objects.requireNonNull(binding.musicRecyclerViewPlaylist.getAdapter()).notifyDataSetChanged(); // Update RecyclerView
                        } else {
                            Log.w(TAG, "Error getting playlists: ", task.getException());
                        }
                    });

        } else {
            Log.w(TAG, "Intent is null");
        }
        binding.addSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddSongListFragment();
            }
        });
        binding.backBtn.setOnClickListener(v -> finish());
        binding.searchBtn.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchSongs(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                searchSongs(newText);
                return true;
            }
        });
    }
    private void showAddSongListFragment() {
        AddSongListFragment addSongListFragment = new AddSongListFragment(); // Create the fragment instance
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(android.R.id.content, addSongListFragment); // Add to activity's content area
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void searchSongs(String keyword) {
        // Lấy tên của playlist từ giao diện người dùng
        String playlistName = binding.playlistName.getText().toString();

        // Truy vấn cơ sở dữ liệu để lấy danh sách bài hát của playlist đó
        db.collection("playlists")
                .whereEqualTo("name", playlistName)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            // Lấy danh sách bài hát từ tài liệu của playlist
                            List<String> playlistSongs = (List<String>) document.get("songs");
                            if (playlistSongs != null) {
                                // Sau khi lấy được danh sách bài hát của playlist, tiến hành tìm kiếm
                                searchSongsInPlaylist(playlistSongs, keyword);
                            }
                        }
                    } else {
                        Toast.makeText(this, "Failed to search songs", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void searchSongsInPlaylist(List<String> playlistSongs, String keyword) {
        List<String> filteredSongIdList = new ArrayList<>();

        // Sử dụng AtomicInteger để theo dõi số lượng bài hát đã xử lý
        AtomicInteger processedCount = new AtomicInteger(0);

        for (String songId : playlistSongs) {
            db.collection("songs").document(songId)
                    .get()
                    .addOnSuccessListener(songDocument -> {
                        String songName = songDocument.getString("name");
                        String songArtists = songDocument.getString("artists");
                        if (songName != null && songName.toLowerCase().contains(keyword.toLowerCase())
                                || songArtists != null && songArtists.toLowerCase().contains(keyword.toLowerCase())) {
                            filteredSongIdList.add(songId);
                        }

                        // Log lại bài hát đang được xử lý
                        Log.d(TAG, "Processed song: " + songName);

                        // Cập nhật RecyclerView sau khi mỗi bài hát được xử lý
                        updateRecyclerView(filteredSongIdList);

                        // Tăng số lượng bài hát đã xử lý
                        int count = processedCount.incrementAndGet();

                        // Kiểm tra xem đã xử lý hết tất cả các bài hát chưa
                        if (count == playlistSongs.size()) {
                            Log.d(TAG, "All songs processed. Keyword: " + keyword);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error getting song document", e);
                    });
        }
    }
    private void updateRecyclerView(List<String> filteredSongIdList) {
        // Cập nhật RecyclerView với danh sách bài hát đã lọc
        songListAdapter.setSongIdList(filteredSongIdList);
        songListAdapter.notifyDataSetChanged();
    }

}

