package com.example.onsitetask3;

public class Upload {

    private String name, imageUri, key;

    public Upload() {
        //empty constructor needed
    }

    public Upload(String n, String uri) {
        if(n.trim().equals("")) {
            n = "No Name";
        }

        name = n;
        imageUri = uri;

    }

    public String getName() {
        return name;
    }

    public void setName(String n) {
        name = n;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String uri) {
        imageUri = uri;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
