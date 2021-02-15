package com.Syrine.mnart.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostLike {
    @SerializedName("likes")
    @Expose
    private int likes;


    public PostLike(int likes) {
        this.likes = likes;
    }

    public PostLike() {
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
