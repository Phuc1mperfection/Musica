package com.example.musica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class PlaylistDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_detail);

        // Lấy thông tin playlist từ Intent
        Intent intent = getIntent();
        if (intent != null) {
            String playlistName = intent.getStringExtra("playlistName");
            if (playlistName != null) {
                // Hiển thị tên playlist trên màn hình
                TextView textViewPlaylistName = findViewById(R.id.playlistName);
                textViewPlaylistName.setText(playlistName);
            }
        }
    }

}