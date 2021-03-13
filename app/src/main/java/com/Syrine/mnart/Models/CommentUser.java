package com.Syrine.mnart.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommentUser {

    @SerializedName("id_comment")
    @Expose
    private int id_comment;

    @SerializedName("id_user")
    @Expose
    private int id_user;

    @SerializedName("id_post")
    @Expose
    private String id_post;

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
    private int comments_Nbr_Per_Post;

    @SerializedName("nbrsubComnts")
    @Expose
    private int subcomments_Nbr_Per_Post;

    public CommentUser() {
    }

    public CommentUser(int id_comment, int id_user, String id_post, String comment, String date_comment, String user_firstName, String user_lastName, String user_img, int subcomments_Nbr_Per_Post) {
        this.id_comment = id_comment;
        this.id_user = id_user;
        this.id_post = id_post;
        this.comment = comment;
        this.date_comment = date_comment;
        this.user_firstName = user_firstName;
        this.user_lastName = user_lastName;
        this.user_img = user_img;
        this.subcomments_Nbr_Per_Post = subcomments_Nbr_Per_Post;
    }

    public CommentUser(int comments_Nbr_Per_Post) {
        this.comments_Nbr_Per_Post = comments_Nbr_Per_Post;
    }

    public int getId_comment() {
        return id_comment;
    }

    public void setId_comment(int id_comment) {
        this.id_comment = id_comment;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getId_post() {
        return id_post;
    }

    public void setId_post(String id_post) {
        this.id_post = id_post;
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

    public int getComments_Nbr_Per_Post() {
        return comments_Nbr_Per_Post;
    }

    public void setComments_Nbr_Per_Post(int comments_Nbr_Per_Post) {
        this.comments_Nbr_Per_Post = comments_Nbr_Per_Post;
    }

    public int getSubcomments_Nbr_Per_Post() {
        return subcomments_Nbr_Per_Post;
    }

    public void setSubcomments_Nbr_Per_Post(int subcomments_Nbr_Per_Post) {
        this.subcomments_Nbr_Per_Post = subcomments_Nbr_Per_Post;
    }
}
