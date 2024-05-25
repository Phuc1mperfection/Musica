package com.example.musica.Object;

import android.content.Context;

import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

import com.example.musica.Model.SongModel;

import java.util.ArrayList;
import java.util.List;

public class MyExoplayer {
    private static List<SongModel> songList = new ArrayList<>();
    private static int currentSongIndex = 0;
    private static ExoPlayer exoPlayer;

    public static void startPlaying(Context context, SongModel song, List<SongModel> playlist) {
        if (exoPlayer == null) {
            initializePlayer(context);
        }
        setSongList(playlist); // Set the playlist
        playSong(song);
    }

    public static void initializePlayer(Context context) {
        if (exoPlayer == null) {
            exoPlayer = new ExoPlayer.Builder(context).build();
        }
    }

    public static void playNextSong() {
        if (!songList.isEmpty()) {
            currentSongIndex = (currentSongIndex + 1) % songList.size();
            SongModel nextSong = songList.get(currentSongIndex);
            playSong(nextSong);
        }
    }

    public static void playPreviousSong() {
        if (!songList.isEmpty()) {
            currentSongIndex = (currentSongIndex - 1 + songList.size()) % songList.size();
            SongModel previousSong = songList.get(currentSongIndex);
            playSong(previousSong);
        }
    }


    public static void playSong(SongModel song) {
        if (exoPlayer != null) {
            exoPlayer.stop();
            MediaItem mediaItem = MediaItem.fromUri(song.getSongUrl());
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
            exoPlayer.play();
        }
    }

    public static SongModel getCurrentSong() {
        if (songList.isEmpty()) {
            return null;
        }
        return songList.get(currentSongIndex);
    }

    public static void release() {
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    public static void setSongList(List<SongModel> songs) {
        songList = songs;
    }
    public static ExoPlayer getExoPlayer() {
        return exoPlayer;
    }

    public static void handleLogout(Context context) {
        release();
    }

}