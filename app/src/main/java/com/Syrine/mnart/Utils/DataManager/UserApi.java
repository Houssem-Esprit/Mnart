package com.Syrine.mnart.Utils.DataManager;

import com.Syrine.mnart.Models.Category;
import com.Syrine.mnart.Models.CoursPost;
import com.Syrine.mnart.Models.Notification;
import com.Syrine.mnart.Models.Post;
import com.Syrine.mnart.Models.PostByCategory;
import com.Syrine.mnart.Models.PostLike;
import com.Syrine.mnart.Models.User;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UserApi {

    @POST("users/authenticate")
    @FormUrlEncoded
    Flowable<User> Authenticate(
            @Field("email") String email,
            @Field("password") String password
    );

    @GET("categories/")
    Flowable<List<Category>> getcategories(

    );


    @GET("posts/")
    Flowable<List<Post>> get4Posts(

    );

    @Multipart
    @POST("posts/")
    Flowable<ResponseBody> addPost(
            @Part("idUser") RequestBody idUser,
            @Part("idCategory") RequestBody idCategory,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("price") RequestBody price,
            @Part MultipartBody.Part image,
            @Part("image") RequestBody imageName
            );



    @GET("cours/getCoursPosts")
    Flowable<List<CoursPost>> getCoursPosts();

    @PUT("cours/updateViews")
    @FormUrlEncoded
    Flowable<ResponseBody> updateViews(
            @Field("idCours") int idCours
    );

    @Multipart
    @POST("cours/")
    Flowable<ResponseBody> publishCours(
            @Part("idUser") RequestBody idUser,
            @Part("idCategoryCours") RequestBody idCategory,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("views") RequestBody views,
            @Part MultipartBody.Part video,
            @Part("video") RequestBody videoName
    );



    @POST("posts/getPostsByCategory")
    @FormUrlEncoded
    Flowable<List<PostByCategory>> getPostsByCategory(
            @Field("idCategory") int idCours
    );


    @POST("posts/getPostsByUserID")
    @FormUrlEncoded
    Flowable<List<Post>> getPostsByUserId(
            @Field("idUser") int idUser
    );

    @DELETE("posts/DeletebyPostID/{idpost}")
    Flowable<ResponseBody> DeletePostById(
            @Path("idpost") String idPost
    );


    @Multipart
    @PUT("posts/UpdatePost")
    Flowable<ResponseBody> updatePost(
            @Part("idpost") RequestBody idPost,
            @Part("idCategory") RequestBody idCategory,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("price") RequestBody price,
            @Part MultipartBody.Part image,
            @Part("image") RequestBody imageName
    );


    @POST("posts/getPostByID")
    @FormUrlEncoded
    Flowable<Post> getPostById(
            @Field("idpost") String idPost
    );


    @POST("postLikes/addLike")
    @FormUrlEncoded
    Flowable<ResponseBody> addLike(
            @Field("idpost") String idPost,
            @Field("idownerr") int idPostOwner,
            @Field("iduser") int idUser,
            @Field("ischecked") int isChecked
    );

    @DELETE("postLikes/deleteLike/{idpost}&{idUser}")
    Flowable<ResponseBody> Unlike(
            @Path("idpost") String idPost,
            @Path("idUser") int idUser
    );

    @POST("postLikes/getLikesPerPost")
    @FormUrlEncoded
    Flowable<PostLike> getPostLikes(
            @Field("idpost") String idPost
    );


    @POST("postLikes/isButtonLikeChecked")
    @FormUrlEncoded
    Flowable<String> isButtonLikeChecked(
            @Field("idpost") String idPost,
            @Field("iduser") int idUser
    );

    @GET("postLikes/getNotifications")
    Flowable<List<Notification>> getNotifications(

    );

    @PUT("postLikes/updateNotifisCheckedState")
    @FormUrlEncoded
    Flowable<ResponseBody> updateIsNotifCheckedState(
            @Field("idpostLiked") int idPostLiked
    );
}
