package com.example.onsitetask3;

public class DrawNote {

    private String title;
    private String imagePath;

    public DrawNote(String title, String imagePath) {
        this.title = title;
        this.imagePath = imagePath;
    }

    public String getDrawTitle() {
        return title;
    }

    public String getImagePath() {
        return imagePath;
    }

}
