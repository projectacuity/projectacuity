package com.example.mahdi.acuity.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Post {

    public String uid;
    public String author;
    public String authorUrl;
    public String imageUrl;
    public String comment;
    public int likesCount =0;
    public int dislikesCount =0;
    public Map<String, Boolean> likes = new HashMap<>();
    public Map<String, Boolean> dislikes = new HashMap<>();

    public Post() {
    }

    public Post(String uid, String author, String authorUrl, String imageUrl, String comment) {
        this.uid =uid;
        this.author = author;
        this.authorUrl = authorUrl;
        this.imageUrl = imageUrl;
        this.comment = comment;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("authorUrl",authorUrl);
        result.put("imageUrl", imageUrl);
        result.put("comment",comment);
        result.put("likesCount", likesCount);
        result.put("dislikesCount", dislikesCount);
        result.put("likes", likes);
        result.put("dislikes", dislikes);

        return result;
    }
}
