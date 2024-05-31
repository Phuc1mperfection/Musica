package com.example.musica.Utils;

import java.util.ArrayList;
import java.util.List;

public class PlayerStateManager {
    private static PlayerStateManager instance;
    private boolean isPlaying;
    private final List<OnPlayerStateChangeListener> listeners;

    private PlayerStateManager() {
        listeners = new ArrayList<>();
    }

    public static PlayerStateManager getInstance() {
        if (instance == null) {
            instance = new PlayerStateManager();
        }
        return instance;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
        notifyListeners();
    }

    public void addListener(OnPlayerStateChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(OnPlayerStateChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (OnPlayerStateChangeListener listener : listeners) {
            listener.onPlayerStateChanged(isPlaying);
        }
    }

    public interface OnPlayerStateChangeListener {
        void onPlayerStateChanged(boolean isPlaying);
    }
}
