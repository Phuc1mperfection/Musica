package com.example.musica.Fragment.BottomMenuFragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musica.Adapter.SongListAdapter;
import com.example.musica.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private RecyclerView searchRecyclerView;
    private EditText searchView;
    private ImageView searchIcon;
    private SongListAdapter songListAdapter;
    private List<String> songIdList;
    private FirebaseFirestore db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        songIdList = new ArrayList<>();
        songListAdapter = new SongListAdapter(songIdList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchRecyclerView = view.findViewById(R.id.searchRecyclerView);
        searchView = view.findViewById(R.id.searchView);
        searchIcon = view.findViewById(R.id.search);

        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchRecyclerView.setAdapter(songListAdapter);

        loadAllSongs();

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = searchView.getText().toString().trim();
                if (!TextUtils.isEmpty(keyword)) {
                    searchSongs(keyword);
                } else {
                    Toast.makeText(getContext(), "Please enter a keyword", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }


    private void loadAllSongs() {
        db.collection("songs")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        songIdList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            songIdList.add(document.getId());
                        }
                        songListAdapter.notifyDataSetChanged();  // Update the adapter
                    } else {
                        Toast.makeText(getContext(), "Failed to load songs", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void searchSongs(String keyword) {
        db.collection("songs")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        songIdList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            String songName = document.getString("name");
                            String songArtists = document.getString("artists");
                            // Perform search within song name and artists
                            if (songName != null && songName.toLowerCase().contains(keyword.toLowerCase())
                                    || songArtists != null && songArtists.toLowerCase().contains(keyword.toLowerCase())) {
                                songIdList.add(document.getId());
                            }
                        }
                        if(songIdList.isEmpty()) {
                            Toast.makeText(getContext(), "No matching songs found", Toast.LENGTH_SHORT).show();
                        }
                        songListAdapter.notifyDataSetChanged();  // Update the adapter
                    } else {
                        Toast.makeText(getContext(), "Failed to search songs", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}