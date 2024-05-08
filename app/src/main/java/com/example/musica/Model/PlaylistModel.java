package com.example.musica.Model;

import java.util.List;

public class PlaylistModel {
    private String name;
    private String userID;
    private String imgUrl;
    private List<String> songs;

    public PlaylistModel(String name, String userID, String imgUrl, List<String> songs) {
        this.name = name;
        this.userID = userID;
        this.imgUrl = imgUrl;
        this.songs = songs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public List<String> getSongs() {
        return songs;
    }

    public void setSongs(List<String> songs) {
        this.songs = songs;
    }
// Getters và setters cho các thuộc tính

}
