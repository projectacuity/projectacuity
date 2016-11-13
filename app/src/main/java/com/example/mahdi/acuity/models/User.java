package com.example.mahdi.acuity.models;

import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class User {
    protected String username;
    protected String email;
    protected Post post;

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public User(String username, String email, Post post) {
        this.username = username;
        this.email = email;
        this.post=post;
    }
    public User() {
        this.username = "";
        this.email = "";
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

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
