package com.example.mahdi.acuity.models;

import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class User {
    public String username;
    public String email;
    public String photoUrl;

    public User(String username, String email, String photoUrl) {
        this.username = username;
        this.email = email;
        this.photoUrl=photoUrl;
    }
    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
