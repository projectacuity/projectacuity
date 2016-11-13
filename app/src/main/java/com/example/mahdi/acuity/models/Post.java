package com.example.mahdi.acuity.models;

import com.google.firebase.database.Exclude;

public class Post {

    public String comment;
    public int dislikesCount;
    public String imageUrl;
    public int likesCount;
    public String postId;
    @Exclude
    public String posterId;

    public Post() {
    }
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getDislikesCount() {
        return dislikesCount;
    }

    public void setDislikesCount(int dislikesCount) {
        this.dislikesCount = dislikesCount;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public Post(String postId, String posterId, String imageUrl, String comment, int dislikesCount, int likesCount) {

        this.postId = postId;
        this.posterId = posterId;
        this.imageUrl = imageUrl;
        this.comment = comment;
        this.dislikesCount = dislikesCount;
        this.likesCount = likesCount;
    }
}
