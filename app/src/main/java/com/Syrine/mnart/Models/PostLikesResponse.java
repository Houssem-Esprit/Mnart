package com.Syrine.mnart.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostLikesResponse {

    @SerializedName("id")
    @Expose
    private int idLike;

    @SerializedName("idPost")
    @Expose
    private String idPost;

    @SerializedName("idPostOwner")
    @Expose
    private int idPostOwner;

    @SerializedName("idUser")
    @Expose
    private int idUserLiker;

    @SerializedName("timestamp")
    @Expose
    private  String timestamp;

    @SerializedName("firstName")
    @Expose
    private  String firstName;

    @SerializedName("lastName")
    @Expose
    private  String lastName;

    @SerializedName("image")
    @Expose
    private  String imageUser;


    public PostLikesResponse() {
    }

    public PostLikesResponse(int idLike, String idPost, int idPostOwner, int idUserLiker, String timestamp, String firstName, String lastName, String imageUser) {
        this.idLike = idLike;
        this.idPost = idPost;
        this.idPostOwner = idPostOwner;
        this.idUserLiker = idUserLiker;
        this.timestamp = timestamp;
        this.firstName = firstName;
        this.lastName = lastName;
        this.imageUser = imageUser;
    }


    public int getIdLike() {
        return idLike;
    }

    public void setIdLike(int idLike) {
        this.idLike = idLike;
    }

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public int getIdPostOwner() {
        return idPostOwner;
    }

    public void setIdPostOwner(int idPostOwner) {
        this.idPostOwner = idPostOwner;
    }

    public int getIdUserLiker() {
        return idUserLiker;
    }

    public void setIdUserLiker(int idUserLiker) {
        this.idUserLiker = idUserLiker;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getImageUser() {
        return imageUser;
    }

    public void setImageUser(String imageUser) {
        this.imageUser = imageUser;
    }
}
