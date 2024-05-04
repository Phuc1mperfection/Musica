package com.example.musica.Model;

public class PlaylistModel {
    private String name;
    private int id;
    private String imageUrl;
    private int numberOfSongs;
    private String description;

    public PlaylistModel(String name, int id, String imageUrl, int numberOfSongs, String description) {
        this.name = name;
        this.id = id;
        this.imageUrl = imageUrl;
        this.numberOfSongs = numberOfSongs;
        this.description = description;
    }

    // Getters và setters cho các thuộc tính

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getNumberOfSongs() {
        return numberOfSongs;
    }

    public void setNumberOfSongs(int numberOfSongs) {
        this.numberOfSongs = numberOfSongs;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
