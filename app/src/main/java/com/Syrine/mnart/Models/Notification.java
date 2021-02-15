package com.Syrine.mnart.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Notification {

    @SerializedName("likeID")
    @Expose
    private int likeId;

    @SerializedName("userID")
    @Expose
    private int userId;

    @SerializedName("firstName")
    @Expose
    private String firstName;

    @SerializedName("lastName")
    @Expose
    private String lastName;

    @SerializedName("image")
    @Expose
    private String userImage;

    @SerializedName("idpost")
    @Expose
    private String idPost;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("date")
    @Expose
    private String reactionDate;


    @SerializedName("isNotifChecked")
    @Expose
    private int isNotifChecked;

    @SerializedName("idCategory")
    @Expose
    private int idCategory;


    public Notification(int likeId, int userId, String firstName, String lastName, String userImage, String idPost, String title, String reactionDate, int isChecked, int idCategory) {
        this.likeId = likeId;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userImage = userImage;
        this.idPost = idPost;
        this.title = title;
        this.reactionDate = reactionDate;
        this.isNotifChecked = isChecked;
        this.idCategory = idCategory;
    }

    public Notification() {
    }


    public int getLikeId() {
        return likeId;
    }

    public void setLikeId(int likeId) {
        this.likeId = likeId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReactionDate() {
        return reactionDate;
    }

    public void setReactionDate(String reactionDate) {
        this.reactionDate = reactionDate;
    }

    public int getIsNotifChecked() {
        return isNotifChecked;
    }

    public void setIsNotifChecked(int isNotifChecked) {
        this.isNotifChecked = isNotifChecked;
    }

    public int getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(int idCategory) {
        this.idCategory = idCategory;
    }
}
