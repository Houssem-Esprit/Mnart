package com.Syrine.mnart.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostByCategory {

    @SerializedName("idpost")
    @Expose
    private String id;
    @SerializedName("id")
    @Expose
    private int idUser;
    @SerializedName("idCategory")
    @Expose
    private int idCategory;

    @SerializedName("firstName")
    @Expose
    private String firstName;

    @SerializedName("lastName")
    @Expose
    private String lastName;

    @SerializedName("image")
    @Expose
    private String userImg;

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("price")
    @Expose
    private int price;
    @SerializedName("img")
    @Expose
    private String imgPost;
    @SerializedName("date")
    @Expose
    private String DatePost;


    public PostByCategory(String id, int idUser, int idCategory, String firstName, String lastName, String userImg, String title, String description, int price, String imgPost, String datePost) {
        this.id = id;
        this.idUser = idUser;
        this.idCategory = idCategory;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userImg = userImg;
        this.title = title;
        this.description = description;
        this.price = price;
        this.imgPost = imgPost;
        DatePost = datePost;
    }

    public PostByCategory() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(int idCategory) {
        this.idCategory = idCategory;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImgPost() {
        return imgPost;
    }

    public void setImgPost(String imgPost) {
        this.imgPost = imgPost;
    }

    public String getDatePost() {
        return DatePost;
    }

    public void setDatePost(String datePost) {
        DatePost = datePost;
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

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }
}
