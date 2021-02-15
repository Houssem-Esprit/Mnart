package com.Syrine.mnart.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CoursPost {

    @SerializedName("idCours")
    @Expose
    private int idCours;

    @SerializedName("image")
    @Expose
    private String userImg;

    @SerializedName("firstName")
    @Expose
    private String firstName;

    @SerializedName("lastName")
    @Expose
    private String lastName;

    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("description")
    @Expose
    private String coursDesc;


    @SerializedName("title")
    @Expose
    private String tile;

    @SerializedName("video")
    @Expose
    private String coursVideo;

    @SerializedName("views")
    @Expose
    private int views;

    public CoursPost(int idCours,String userImg, String firstName, String lastName, String date, String coursDesc, String tile, String coursVideo,int views) {
        this.idCours = idCours;
        this.userImg = userImg;
        this.firstName = firstName;
        this.lastName = lastName;
        this.date = date;
        this.coursDesc = coursDesc;
        this.tile = tile;
        this.coursVideo = coursVideo;
        this.views=views;
    }

    public CoursPost() {
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getIdCours() {
        return idCours;
    }

    public void setIdCours(int idCours) {
        this.idCours = idCours;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCoursDesc() {
        return coursDesc;
    }

    public void setCoursDesc(String coursDesc) {
        this.coursDesc = coursDesc;
    }

    public String getTile() {
        return tile;
    }

    public void setTile(String tile) {
        this.tile = tile;
    }

    public String getCoursVideo() {
        return coursVideo;
    }

    public void setCoursVideo(String coursVideo) {
        this.coursVideo = coursVideo;
    }
}
