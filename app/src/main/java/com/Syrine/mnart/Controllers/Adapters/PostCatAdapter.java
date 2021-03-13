package com.Syrine.mnart.Controllers.Adapters;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.Syrine.mnart.Controllers.Interfaces.SocketCallbackInterface;
import com.Syrine.mnart.Models.CommentUser;
import com.Syrine.mnart.Models.PostByCategory;
import com.Syrine.mnart.Models.PostLike;
import com.Syrine.mnart.R;
import com.Syrine.mnart.Utils.Const;
import com.Syrine.mnart.Utils.DataManager.UserApi;
import com.Syrine.mnart.Utils.DataManager.UtilApi;
import com.Syrine.mnart.Utils.Session;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.button.MaterialButton;
import com.ramotion.foldingcell.FoldingCell;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.socket.client.Socket;
import okhttp3.ResponseBody;

public class PostCatAdapter extends RecyclerView.Adapter implements LifecycleObserver {
    Context mContext;
    Activity activity;
    myViewHolder mholder ;
    List<PostByCategory> coursPostsList;
    private UserApi userApi;
    Animation animation;
    MediaPlayer mediaPlayer;
    CompositeDisposable disposable = new CompositeDisposable();
    CompositeDisposable addLikeDisposable = new CompositeDisposable();
    CompositeDisposable unlikedisposable = new CompositeDisposable();
    CompositeDisposable isCheckedDisposable = new CompositeDisposable();
    CompositeDisposable count_comment_disposable = new CompositeDisposable();
    Socket mSocket;
    int ID_Cat;
    static final String TAG = "POSTCATADAPTER_ADAPTER";
    SocketCallbackInterface callback ;
    public static final int ViewType_Posts = 0;
    public static final int ViewType_CreatePost = 1;
    public int likesNumber;

    public PostCatAdapter(){
    }
    public void setSocketCallbackInterface(SocketCallbackInterface callback){
        if(callback!= null){
            this.callback = callback;
        }
    }

    public PostCatAdapter(Context context, Activity activity, List<PostByCategory> list,int idCat){
        this.mContext = context;
        this.activity=activity;
        this.coursPostsList=list;
        ID_Cat = idCat;
    }


    @Override
    public int getItemViewType(int position) {
        if (position+1 <= coursPostsList.size()){
            return ViewType_Posts;
        }else {
            return ViewType_CreatePost;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case ViewType_Posts:
                view = LayoutInflater.from(mContext).inflate(R.layout.cell_folding_ccard,parent,false);
                return new myViewHolder(view);
            case  ViewType_CreatePost:
                view = LayoutInflater.from(mContext).inflate(R.layout.add_post_illustration_card,parent,false);
                return new AddNewPostViewHolder(view);
        }
        return null;


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        userApi = UtilApi.getUserApi();
        mSocket = Session.getInstance().getSocket();

        if (position+1 <= coursPostsList.size()){

            // UserImg
            String url = "http://localhost:3000/public/images/users/"+coursPostsList.get(position).getUserImg();
            Glide.with(mContext)
                    .load(url)
                    .into(((myViewHolder)holder).cell_content_UserImg);

            Glide.with(mContext)
                    .load(url)
                    .into(((myViewHolder)holder).cell_title_userImg);

            String url_post ="http://localhost:3000/public/images/posts/"+coursPostsList.get(position).getImgPost();
            Glide.with(mContext)
                    .load(url_post)
                    .into(((myViewHolder)holder).cell_content_PostImg);
            // Cell_title attr
            ((myViewHolder)holder).cell_title_userName.setText("@"+coursPostsList.get(position).getFirstName()+" "+coursPostsList.get(position).getLastName());
            ((myViewHolder)holder).cell_title_postTitle.setText(coursPostsList.get(position).getTitle());
            ((myViewHolder)holder).cell_title_postDesc.setText(coursPostsList.get(position).getDescription());
            //holder.cell_title_postPrice.setText(""+coursPostsList.get(position).getPrice());

            ((myViewHolder)holder).cell_title_postDate.setText(Const.generateElapsedTime(coursPostsList.get(position).getDatePost()));

            int CatID = coursPostsList.get(position).getIdCategory();
            if(CatID == 1){
                ((myViewHolder)holder).cell_title_categoryImg.setBackground(mContext.getResources().getDrawable(R.drawable.photography_post));
            }else if(CatID == 2){
                ((myViewHolder)holder).cell_title_categoryImg.setBackground(mContext.getResources().getDrawable(R.drawable.paint_post));
            }else if(CatID == 3){
                ((myViewHolder)holder).cell_title_categoryImg.setBackground(mContext.getResources().getDrawable(R.drawable.scupture_logo));
            }else if(CatID == 4){
                ((myViewHolder)holder).cell_title_categoryImg.setBackground(mContext.getResources().getDrawable(R.drawable.donation_post));
            }else if(CatID == 5){
                ((myViewHolder)holder).cell_title_categoryImg.setBackground(mContext.getResources().getDrawable(R.drawable.auction_post));
            }

            ((myViewHolder)holder).cell_title_userName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle= new Bundle();
                    bundle.putInt("userID",coursPostsList.get(position).getIdUser());
                    bundle.putString("userImg",coursPostsList.get(position).getUserImg());
                    bundle.putString("userName",coursPostsList.get(position).getFirstName()+" "+coursPostsList.get(position).getLastName());

                    final NavController navController = Navigation.findNavController(activity,R.id.nav_host_fragment);
                    navController.navigate(R.id.action_allPosts_to_profileGuestPage,bundle);
                }
            });

            // Cell_content attr

            ((myViewHolder)holder).cell_content_postTitle.setText(coursPostsList.get(position).getTitle());
            ((myViewHolder)holder).cell_content_postPrice.setText(""+coursPostsList.get(position).getPrice()+"$");
            ((myViewHolder)holder).cell_content_postDesc.setText(coursPostsList.get(position).getDescription());
            ((myViewHolder)holder).cell_content_userName.setText(coursPostsList.get(position).getFirstName()+" "+coursPostsList.get(position).getLastName());
            ((myViewHolder)holder).cell_content_postDesc.setText(coursPostsList.get(position).getDescription());
            ((myViewHolder)holder).cell_content_postDate.setText(Const.generateElapsedTime(coursPostsList.get(position).getDatePost()));


            ((myViewHolder)holder).cell_content_userName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle= new Bundle();
                    bundle.putInt("userID",coursPostsList.get(position).getIdUser());
                    bundle.putString("userImg",coursPostsList.get(position).getUserImg());
                    bundle.putString("userName",coursPostsList.get(position).getFirstName()+" "+coursPostsList.get(position).getLastName());

                    final NavController navController = Navigation.findNavController(activity,R.id.nav_host_fragment);
                    navController.navigate(R.id.action_allPosts_to_profileGuestPage,bundle);
                }
            });


           // TODO countComments();

            ((myViewHolder)holder).foldingCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((myViewHolder)holder).foldingCell.toggle(false);

                }
            });
            // Like button Implementation
            // vars

            animation = AnimationUtils.loadAnimation(mContext,R.anim.like_button);
            mediaPlayer = MediaPlayer.create(mContext,R.raw.like_button_sound);

            getpostLikes(coursPostsList.get(position).getId(),((myViewHolder)holder).Likes,((myViewHolder)holder).cell_title_postLikes);

            countComments(coursPostsList.get(position).getId(),((myViewHolder)holder).cell_title_postComments,((myViewHolder)holder).cell_content_comments);
            // set the checkbox initial state
            //holder.likeButton.setOnCheckedChangeListener(null);

            isButtonLikeCkecked(coursPostsList.get(position).getId(),Session.getInstance().getUser().getIdUser(),((myViewHolder)holder).likeButton);

            ((myViewHolder)holder).likeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        if(buttonView.isPressed()){
                            ((myViewHolder)holder).likeButton.setBackgroundResource(R.drawable.like_button_ckecked);
                            ((myViewHolder)holder).likeButton.startAnimation(animation);
                            mediaPlayer.start();
                            ((myViewHolder)holder).Likes.setText((Integer.parseInt(((myViewHolder)holder).Likes.getText().toString()))+1 +"");
                            Log.d(TAG,"USEEEEEEEEEEEEEEEEEEER IDDDDDDDD: "+coursPostsList.get(position).getIdUser());
                            addLike(coursPostsList.get(position).getId(),coursPostsList.get(position).getIdUser(), Session.getInstance().getUser().getIdUser());

                        }


                    }
                    else {
                        if(buttonView.isPressed()){
                            ((myViewHolder)holder).likeButton.setBackgroundResource(R.drawable.like_button);
                            ((myViewHolder)holder).likeButton.startAnimation(animation);
                            ((myViewHolder)holder).Likes.setText((Integer.parseInt(((myViewHolder)holder).Likes.getText().toString()))-1 +"");
                            UnLike(coursPostsList.get(position).getId(), Session.getInstance().getUser().getIdUser());
                        }


                    }
                }
            });


            ((myViewHolder)holder).commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Bundle bundle= new Bundle();
                    bundle.putString("idPost",coursPostsList.get(position).getId());
                    bundle.putInt("likesNumber",likesNumber);
                    final NavController navController = Navigation.findNavController(activity,R.id.nav_host_fragment);
                    navController.navigate(R.id.action_allPosts_to_commentPage,bundle);
                }
            });

        }

        else {
            switch (ID_Cat){
                case 1:
                    ((AddNewPostViewHolder)holder).add_post_illustration_card_layout.setVisibility(View.VISIBLE);
                    ((AddNewPostViewHolder)holder).categoryPost_illustration.setImageResource(R.drawable.photography_illustration);
                    ((AddNewPostViewHolder)holder).greeting_text_tv.setText("publish your photography so people can see it.");
                    break;
                case 2:
                    ((AddNewPostViewHolder)holder).add_post_illustration_card_layout.setVisibility(View.VISIBLE);
                    ((AddNewPostViewHolder)holder).categoryPost_illustration.setImageResource(R.drawable.paint0_illustration);
                    ((AddNewPostViewHolder)holder).greeting_text_tv.setText("publish your paintings so people can see it.");
                    break;
                case 3:
                    ((AddNewPostViewHolder)holder).add_post_illustration_card_layout.setVisibility(View.VISIBLE);
                    ((AddNewPostViewHolder)holder).categoryPost_illustration.setImageResource(R.drawable.sculpture_illustration);
                    ((AddNewPostViewHolder)holder).greeting_text_tv.setText("publish your sculptures so people can see it.");
                    break;
                default:
                    ((AddNewPostViewHolder)holder).add_post_illustration_card_layout.setVisibility(View.GONE);
                    break;
            }


        }



    }

    @Override
    public int getItemCount() {
        return coursPostsList.size()+1;
    }




    public class myViewHolder extends  RecyclerView.ViewHolder{

        CircleImageView cell_content_UserImg;
        ImageView cell_title_categoryImg;
        Button  cours_go_to_details;
        TextView cell_title_userName;
        TextView cell_title_postTitle;
        TextView cell_title_postDate;
        TextView cell_title_postDesc;
        CircleImageView cell_title_userImg;
        TextView cell_title_postComments, cell_title_postLikes;
        TextView cell_content_postTitle;
        ImageView cell_content_PostImg;
        TextView cell_content_postDate;
        TextView cell_content_postPrice;
        TextView cell_content_postDesc;
        TextView cell_content_userName;
        TextView cell_content_comments;
        FoldingCell foldingCell;
        CheckBox likeButton;
        TextView Likes;
        Button commentButton;

        View view;

        public myViewHolder( View itemView) {
            super(itemView);
            view = itemView;
            cell_title_categoryImg = itemView.findViewById(R.id.titleLayoutLogo);
            cell_title_userName = itemView.findViewById(R.id.cell_title_userName);
            cell_title_postTitle = itemView.findViewById(R.id.cell_title_postTitle);
            cell_title_postDate = itemView.findViewById(R.id.cell_title_postDate);
            cell_title_userImg = itemView.findViewById(R.id.cell_title_userImg);
            cell_title_postDesc = itemView.findViewById(R.id.cell_title_postDesc);
            cell_title_postLikes = itemView.findViewById(R.id.cell_title_postLikes);
            cell_title_postComments = itemView.findViewById(R.id.cell_title_postComments);
            cell_content_postTitle = itemView.findViewById(R.id.Post_details_Title);
            cell_content_PostImg = itemView.findViewById(R.id.Post_details_postImg);
            cell_content_postDate = itemView.findViewById(R.id.Post_details_date);
            cell_content_postPrice = itemView.findViewById(R.id.Post_details_price);
            cell_content_postDesc = itemView.findViewById(R.id.Post_details_desc);
            cell_content_userName = itemView.findViewById(R.id.Post_details_username);
            cell_content_UserImg = itemView.findViewById(R.id.Post_details_userImg);
            cell_content_comments = itemView.findViewById(R.id.cell_content_comments);

            cours_go_to_details = itemView.findViewById(R.id.cours_go_to_details);
            foldingCell = itemView.findViewById(R.id.foldingCell);
            likeButton = itemView.findViewById(R.id.LikeButton);
            Likes = itemView.findViewById(R.id.likes_TV);
            commentButton = itemView.findViewById(R.id.commentButton);
        }

        public View getView()
        {
            return view;
        }

    }




    public class AddNewPostViewHolder extends  RecyclerView.ViewHolder{

        ImageView categoryPost_illustration;
        TextView greeting_text_tv;
        MaterialButton create_poste;
        LinearLayout add_post_illustration_card_layout;


        View view;

        public AddNewPostViewHolder( View itemView) {
            super(itemView);
            view = itemView;
            categoryPost_illustration = itemView.findViewById(R.id.postCategory_illustration);
            create_poste = itemView.findViewById(R.id.create_post);
            greeting_text_tv = itemView.findViewById(R.id.greeting_text_tv);
            add_post_illustration_card_layout = itemView.findViewById(R.id.add_post_illustration_card_layout);
        }

        public View getView()
        {
            return view;
        }

    }












    public void addLike(String idpost,int idPostOwner,int idUser){
        Log.d(TAG,"OOOOOOOOOOOOOOOOOOOOOOOOOOOOO idPostOWner : "+idPostOwner);
        userApi.addLike(idpost,idPostOwner,idUser,0)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        Log.d(TAG,"onSubscribe: created");
                        addLikeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull ResponseBody cat) {
                        Log.d(TAG,"onNext: "+cat);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Log.d(TAG,"onError: ",e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG,"onComplete: created");
                        //addLikeDisposable.clear();

                        mSocket.emit("join", Session.getInstance().getUser().getIdUser());
                        ((SocketCallbackInterface)activity).onNotifSocketEmitedToServer();
                    }
                });
    }

    public void UnLike(String idpost,int idUser){
        userApi.Unlike(idpost,idUser)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        Log.d(TAG,"onSubscribe: created");
                        unlikedisposable.add(d);
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull ResponseBody cat) {
                        Log.d(TAG,"onNext: "+cat);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Log.d(TAG,"onError: ",e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG,"onComplete: created");
                        //unlikedisposable.clear();
                    }
                });
    }

    public void getpostLikes(String idpost, TextView likes, TextView cell_title_likes){
        userApi.getPostLikes(idpost)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PostLike>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        Log.d(TAG,"onSubscribe: created");
                        disposable.add(d);
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull PostLike Likes) {
                        Log.d(TAG,"onNext: "+Likes.getLikes());
                        if(Likes.getLikes()== 0){
                            likes.setText(""+ 0);
                            cell_title_likes.setText(0+" Likes");
                            likesNumber=Likes.getLikes();
                        }else {
                            likes.setText(""+Likes.getLikes());
                            likesNumber=Likes.getLikes();
                            if(Likes.getLikes()==1){
                                cell_title_likes.setText(0+" Like");
                                likesNumber=Likes.getLikes();
                            }else {
                                cell_title_likes.setText(Likes.getLikes()+" Likes");
                                likesNumber=Likes.getLikes();
                            }
                        }

                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Log.d(TAG,"onError: ",e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG,"onComplete: created");
                        //disposable.clear();
                    }
                });
    }

    public void isButtonLikeCkecked(String idPost,int idUser, CheckBox likeButton){

        userApi.isButtonLikeChecked(idPost,idUser)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        Log.d(TAG,"onSubscribe: created");
                        isCheckedDisposable.add(d);
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull String bool) {
                        Log.d(TAG,"onNext: "+bool);
                        if(Boolean.valueOf(bool)==true){
                            likeButton.setChecked(true);
                            likeButton.setBackgroundResource(R.drawable.like_button_ckecked);

                        }else {
                            likeButton.setChecked(false);
                            likeButton.setBackgroundResource(R.drawable.like_button);                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Log.d(TAG,"onError: ",e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG,"onComplete: created");
                        //isCheckedDisposable.clear();
                    }
                });
    }

    public void countComments(String idPost, TextView ct,TextView cc){

        userApi.countPostComments(idPost)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CommentUser>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        Log.d(TAG,"onSubscribe: created");
                        count_comment_disposable.add(d);
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull CommentUser obj) {
                        Log.d(TAG,"onNext: "+obj);
                        if(obj.getComments_Nbr_Per_Post() == 1){
                            ct.setText("1 Comment");
                            cc.setText("1");
                        }else {
                            ct.setText(obj.getComments_Nbr_Per_Post()+" Comments");
                            cc.setText(obj.getComments_Nbr_Per_Post()+"");
                        }

                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Log.d(TAG,"onError: ",e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG,"onComplete: created");
                        ///count_comment_disposable.clear();
                    }
                });
    }






    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void onCreate() {
        //..
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onStart() {
        if (Util.SDK_INT > 23) {
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onStop() {
        if (Util.SDK_INT > 23) {
            count_comment_disposable.clear();
            isCheckedDisposable.clear();
            disposable.clear();
            unlikedisposable.clear();
            addLikeDisposable.clear();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void onPause() {
        if (Util.SDK_INT <= 23) {
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void onResume() {
        if ((Util.SDK_INT <= 23)) {
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy() {
        if ((Util.SDK_INT <= 23)) {
            count_comment_disposable.clear();
            isCheckedDisposable.clear();
            disposable.clear();
            unlikedisposable.clear();
            addLikeDisposable.clear();
        }
    }
}

