package com.example.musica.Object;

import android.content.Context;

import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

import com.example.musica.Model.SongModel;

public class MyExoplayer {
    private static ExoPlayer exoPlayer;
    private static SongModel currentSong;

    public static ExoPlayer getInstance() {
        return exoPlayer;
    }

    public static void startPlaying(Context context, SongModel song) {
        if (exoPlayer == null) {
            exoPlayer = new ExoPlayer.Builder(context).build();
        }

        currentSong = song;
        if (currentSong != null && currentSong.getSongUrl() != null) {
            MediaItem mediaItem = MediaItem.fromUri(currentSong.getSongUrl());
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
            exoPlayer.play();
        }
    }
    public static void release() {
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }
    public static SongModel getCurrentSong() {
        return currentSong;
    }
}
