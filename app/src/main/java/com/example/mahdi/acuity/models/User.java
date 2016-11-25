package com.example.mahdi.acuity.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;


@IgnoreExtraProperties
public class User {
    public String uid;
    public String username;
    public String email;
    public String photoUrl;

    public User() {
    }

    public User(String uid, String username, String email, String photoUrl) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.photoUrl=photoUrl;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("username", username);
        result.put("email", email);
        result.put("photoUrl",photoUrl);
        return result;
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
