package com.Syrine.mnart.Utils.DataManager;

import com.Syrine.mnart.Models.Category;
import com.Syrine.mnart.Models.CommentUser;
import com.Syrine.mnart.Models.CoursPost;
import com.Syrine.mnart.Models.Notification;
import com.Syrine.mnart.Models.Post;
import com.Syrine.mnart.Models.PostByCategory;
import com.Syrine.mnart.Models.PostLike;
import com.Syrine.mnart.Models.PostLikesResponse;
import com.Syrine.mnart.Models.SubComment;
import com.Syrine.mnart.Models.User;
import com.Syrine.mnart.Models.UserResponse;

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
    Flowable<UserResponse> Authenticate(
            @Field("email") String email,
            @Field("password") String password
    );


    @POST("users/getUserById")
    @FormUrlEncoded
    Flowable<UserResponse> getUserById(
            @Field("id") int idUser
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

    @POST("postLikes/getLikessByPost")
    @FormUrlEncoded
    Flowable<List<PostLikesResponse>> getLikessByPost(
            @Field("idPost") String idPost
    );

    @GET("postLikes/getNotifications")
    Flowable<List<Notification>> getNotifications(

    );

    @PUT("postLikes/updateNotifisCheckedState")
    @FormUrlEncoded
    Flowable<ResponseBody> updateIsNotifCheckedState(
            @Field("idpostLiked") int idPostLiked
    );


    @POST("comments/addComment")
    @FormUrlEncoded
    Flowable<ResponseBody> addComment(
            @Field("idUser") int idUser,
            @Field("idPost") String idPost,
            @Field("comnt") String comment

            );


    @POST("comments/postCommentsNbr")
    @FormUrlEncoded
    Flowable<CommentUser> countPostComments(
            @Field("idpost") String idPost

    );

    @POST("comments/getCommentsByPost")
    @FormUrlEncoded
    Flowable<List<CommentUser>> getCommentsByPost(
            @Field("idPost") String idPost

    );


    @POST("subcomments/addSubComment")
    @FormUrlEncoded
    Flowable<ResponseBody> addsubComment(
            @Field("idUser_Cmnt_Owner") int idUser_Comment_Owner,
            @Field("idComnt") int idComment,
            @Field("idUser_Cmnter") int userCommenter,
            @Field("comnt") String comment


    );


    @POST("subcomments/postsubCommentsNbr")
    @FormUrlEncoded
    Flowable<SubComment> countsubComments(
            @Field("idUser_Cmnt_Owner") int idUser_Comment_Owner,
            @Field("idComnt") int idComment


            );

    @POST("subcomments/getsubCommentsPerUser")
    @FormUrlEncoded
    Flowable<List<SubComment>> getsubCommentsByUser(
            @Field("idUser_Cmnt_Owner") int idUser_Comment_Owner,
            @Field("idComnt") int idComment

    );


    @POST("followrequests/userToUserFollowingState")
    @FormUrlEncoded
    Flowable<String> getUserToUserRelationState(
            @Field("idUserRequest") int thisSideUser,
            @Field("idUserToRespond") int otherSideUser

    );


    @POST("followrequests/followRequest")
    @FormUrlEncoded
    Flowable<ResponseBody> AddFriendRequest(
            @Field("idUserRequest") int thisSideUser,
            @Field("idUserToRespond") int otherSideUser

    );


    @POST("followrequests/cancelfollowRequest")
    @FormUrlEncoded
    Flowable<ResponseBody> CancelFriendRequest(
            @Field("idUserRequest") int thisSideUser,
            @Field("idUserToRespond") int otherSideUser

    );


    @POST("followrequests/acceptfollowRequest")
    @FormUrlEncoded
    Flowable<ResponseBody> AcceptFriendRequest(
            @Field("idUserRequest") int thisSideUser,
            @Field("idUserToRespond") int otherSideUser

    );


    @POST("followings/deleteFollowing")
    @FormUrlEncoded
    Flowable<ResponseBody> DeleteFriendShipRelation(
            @Field("idUserBidOne") int idUserBidOne,
            @Field("idUserBidTwo") int idUserBidTwo

    );

}
