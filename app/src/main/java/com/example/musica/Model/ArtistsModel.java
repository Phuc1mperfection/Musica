package com.example.musica.Model;

public class ArtistsModel {
    private String name;
    private String imgUrl;

    public ArtistsModel() {
        // Empty constructor required for Firebase
    }

    public ArtistsModel(String name, String imgUrl) {
        this.name = name;
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
