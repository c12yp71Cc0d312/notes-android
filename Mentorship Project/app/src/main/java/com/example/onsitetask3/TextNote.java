package com.example.onsitetask3;

import com.google.firebase.Timestamp;

public class TextNote {

    private String title;
    private String content;

    private String imgUri;

    private Timestamp created;

    private int contentColor;
    private int titleColor;
    private int backgroundColor;

    public TextNote() {
        //empty constructor
    }

    public TextNote(String title, String content, Timestamp created, int contentColor, int titleColor, int backgroundColor) {
        this.title = title;
        this.content = content;
        this.created = created;
        this.contentColor = contentColor;
        this.titleColor = titleColor;
        this.backgroundColor = backgroundColor;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Timestamp getCreated() {
        return created;
    }

    public int getContentColor() {
        return contentColor;
    }

    public int getTitleColor() {
        return titleColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public String getImgUri() {
        return imgUri;
    }

    public void setImgUri(String imgUri) {
        this.imgUri = imgUri;
    }

}
