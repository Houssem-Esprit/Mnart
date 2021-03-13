package com.Syrine.mnart.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubComment {

    @SerializedName("id_subComment")
    @Expose
    private int id_subcomment;

    @SerializedName("id_comment_ref")
    @Expose
    private int id_comment;

    @SerializedName("id_user_comment_owner")
    @Expose
    private int id_user_comment_owner;

    @SerializedName("id_user_commenter")
    @Expose
    private int id_user_commenter;

    @SerializedName("comment")
    @Expose
    private String comment;

    @SerializedName("timestamp")
    @Expose
    private String date_comment;

    @SerializedName("firstName")
    @Expose
    private String user_firstName;

    @SerializedName("lastName")
    @Expose
    private String user_lastName;

    @SerializedName("image")
    @Expose
    private String user_img;

    @SerializedName("nbrComs")
    @Expose
    private int subcomments_Nbr_Per_Post;



    public SubComment() {
    }


    public SubComment(int id_subcomment, int id_comment, int id_user_comment_owner, int id_user_commenter, String comment, String date_comment, String user_firstName, String user_lastName, String user_img, int subcomments_Nbr_Per_Post) {
        this.id_subcomment = id_subcomment;
        this.id_comment = id_comment;
        this.id_user_comment_owner = id_user_comment_owner;
        this.id_user_commenter = id_user_commenter;
        this.comment = comment;
        this.date_comment = date_comment;
        this.user_firstName = user_firstName;
        this.user_lastName = user_lastName;
        this.user_img = user_img;
        this.subcomments_Nbr_Per_Post = subcomments_Nbr_Per_Post;
    }



    public SubComment(int subcomments_Nbr_Per_Post) {
        this.subcomments_Nbr_Per_Post = subcomments_Nbr_Per_Post;
    }


    public int getId_comment() {
        return id_comment;
    }

    public void setId_comment(int id_comment) {
        this.id_comment = id_comment;
    }

    public int getId_user_comment_owner() {
        return id_user_comment_owner;
    }

    public void setId_user_comment_owner(int id_user_comment_owner) {
        this.id_user_comment_owner = id_user_comment_owner;
    }

    public int getId_user_commenter() {
        return id_user_commenter;
    }

    public void setId_user_commenter(int id_user_commenter) {
        this.id_user_commenter = id_user_commenter;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate_comment() {
        return date_comment;
    }

    public void setDate_comment(String date_comment) {
        this.date_comment = date_comment;
    }

    public String getUser_firstName() {
        return user_firstName;
    }

    public void setUser_firstName(String user_firstName) {
        this.user_firstName = user_firstName;
    }

    public String getUser_lastName() {
        return user_lastName;
    }

    public void setUser_lastName(String user_lastName) {
        this.user_lastName = user_lastName;
    }

    public String getUser_img() {
        return user_img;
    }

    public void setUser_img(String user_img) {
        this.user_img = user_img;
    }

    public int getSubcomments_Nbr_Per_Post() {
        return subcomments_Nbr_Per_Post;
    }

    public void setSubcomments_Nbr_Per_Post(int subcomments_Nbr_Per_Post) {
        this.subcomments_Nbr_Per_Post = subcomments_Nbr_Per_Post;
    }

    public int getId_subcomment() {
        return id_subcomment;
    }

    public void setId_subcomment(int id_subcomment) {
        this.id_subcomment = id_subcomment;
    }
}
