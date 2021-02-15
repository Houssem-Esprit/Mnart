package com.Syrine.mnart.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Category {
    @SerializedName("id")
    @Expose
    private int idCat;
    @SerializedName("categoryName")
    @Expose
    private String CatName;

    @SerializedName("categoryImg")
    @Expose
    private String CatImg;

    public Category(int idCat, String catName, String catImg) {
        this.idCat = idCat;
        CatName = catName;
        CatImg = catImg;
    }

    public Category() {
    }

    public int getIdCat() {
        return idCat;
    }

    public void setIdCat(int idCat) {
        this.idCat = idCat;
    }

    public String getCatName() {
        return CatName;
    }

    public void setCatName(String catName) {
        CatName = catName;
    }



    public String getCatImg() {
        return CatImg;
    }

    public void setCatImg(String catImg) {
        CatImg = catImg;
    }
}
