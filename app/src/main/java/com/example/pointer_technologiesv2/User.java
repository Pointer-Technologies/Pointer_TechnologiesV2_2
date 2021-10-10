package com.example.pointer_technologiesv2;

import android.net.Uri;

public class User {
    private String name;
    private String email;
    private String IDToken;
    private Uri image;

    public User(String name, String email, String IDToken, Uri image) {
        this.name = name;
        this.email = email;
        this.IDToken = IDToken;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIDToken() {
        return IDToken;
    }

    public void setIDToken(String IDToken) {
        this.IDToken = IDToken;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", IDToken='" + IDToken + '\'' +
                ", image=" + image +
                '}';
    }
}
