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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.Syrine.mnart.Controllers.Interfaces.SocketCallbackInterface;
import com.Syrine.mnart.Models.CoursPost;
import com.Syrine.mnart.Models.PostByCategory;
import com.Syrine.mnart.Models.PostLike;
import com.Syrine.mnart.R;
import com.Syrine.mnart.Utils.DataManager.UserApi;
import com.Syrine.mnart.Utils.DataManager.UtilApi;
import com.Syrine.mnart.Utils.Session;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ramotion.foldingcell.FoldingCell;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.socket.client.Socket;
import okhttp3.ResponseBody;

public class PostCatAdapter extends RecyclerView.Adapter<PostCatAdapter.myViewHolder> implements LifecycleObserver {
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
    Socket mSocket;
    static final String TAG = "POSTCATADAPTER_ADAPTER";
    SocketCallbackInterface callback ;

    public PostCatAdapter(){
    }
    public void setSocketCallbackInterface(SocketCallbackInterface callback){
        if(callback!= null){
            this.callback = callback;
        }
    }

    public PostCatAdapter(Context context, Activity activity, List<PostByCategory> list){
        this.mContext = context;
        this.activity=activity;
        this.coursPostsList=list;
    }




    @NonNull
    @Override
    public PostCatAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =LayoutInflater.from(mContext);
        View v=inflater.inflate(R.layout.cell_folding_ccard,parent,false);
        return new myViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PostCatAdapter.myViewHolder holder, int position) {
        mholder = holder;
        userApi = UtilApi.getUserApi();
        mSocket = Session.getInstance().getSocket();
        // UserImg
        String url = "http://localhost:3000/public/images/users/"+coursPostsList.get(position).getUserImg();
        Glide.with(mContext)
                .load(url)
                .into(holder.cell_content_UserImg);
         String url_post ="http://localhost:3000/public/images/posts/"+coursPostsList.get(position).getImgPost();
        Glide.with(mContext)
                .load(url_post)
                .into(holder.cell_content_PostImg);
        // Cell_title attr
        holder.cell_title_userName.setText(coursPostsList.get(position).getFirstName()+" "+coursPostsList.get(position).getLastName());
        holder.cell_title_postTitle.setText(coursPostsList.get(position).getTitle());
        holder.cell_title_postPrice.setText(""+coursPostsList.get(position).getPrice());

        /*long date = Long.parseLong(coursPostsList.get(position).getDatePost());
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date);
        Date d = c.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        holder.cell_title_postDate.setText(sdf.format(d).toString()); */
        int CatID = coursPostsList.get(position).getIdCategory();
        if(CatID == 1){
            holder.cell_title_categoryImg.setBackground(mContext.getResources().getDrawable(R.drawable.photography_post));
        }else if(CatID == 2){
            holder.cell_title_categoryImg.setBackground(mContext.getResources().getDrawable(R.drawable.paint_post));
        }else if(CatID == 3){
            holder.cell_title_categoryImg.setBackground(mContext.getResources().getDrawable(R.drawable.scupture_logo));
        }else if(CatID == 4){
            holder.cell_title_categoryImg.setBackground(mContext.getResources().getDrawable(R.drawable.donation_post));
        }else if(CatID == 5){
            holder.cell_title_categoryImg.setBackground(mContext.getResources().getDrawable(R.drawable.auction_post));
        }

        // Cell_content attr

        holder.cell_content_postTitle.setText(coursPostsList.get(position).getTitle());
        holder.cell_content_postPrice.setText(""+coursPostsList.get(position).getPrice());
        holder.cell_content_postDesc.setText(coursPostsList.get(position).getDescription());
        holder.cell_content_userName.setText(coursPostsList.get(position).getFirstName()+" "+coursPostsList.get(position).getLastName());
        holder.cell_content_postDesc.setText(coursPostsList.get(position).getDescription());
        //holder.cell_content_postDate.setText(sdf.format(d).toString());


        holder.foldingCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.foldingCell.toggle(false);

            }
        });
        // Like button Implementation
        // vars
        animation = AnimationUtils.loadAnimation(mContext,R.anim.like_button);
        mediaPlayer = MediaPlayer.create(mContext,R.raw.like_button_sound);
        getpostLikes(coursPostsList.get(position).getId(),holder.Likes);
        // set the checkbox initial state
        //holder.likeButton.setOnCheckedChangeListener(null);
        isButtonLikeCkecked(coursPostsList.get(position).getId(),Session.getInstance().getUser().getIdUser(),holder.likeButton);
        holder.likeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if(buttonView.isPressed()){
                        holder.likeButton.setBackgroundResource(R.drawable.like_button_ckecked);
                        holder.likeButton.startAnimation(animation);
                        mediaPlayer.start();
                        holder.Likes.setText((Integer.parseInt(holder.Likes.getText().toString()))+1 +"");
                        Log.d(TAG,"USEEEEEEEEEEEEEEEEEEER IDDDDDDDD: "+coursPostsList.get(position).getIdUser());
                        addLike(coursPostsList.get(position).getId(),coursPostsList.get(position).getIdUser(), Session.getInstance().getUser().getIdUser());

                    }


                }
                else {
                    if(buttonView.isPressed()){
                        holder.likeButton.setBackgroundResource(R.drawable.like_button);
                        holder.likeButton.startAnimation(animation);
                        holder.Likes.setText((Integer.parseInt(holder.Likes.getText().toString()))-1 +"");
                        UnLike(coursPostsList.get(position).getId(), Session.getInstance().getUser().getIdUser());
                    }


                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return coursPostsList.size();
    }




    public class myViewHolder extends  RecyclerView.ViewHolder{

        CircleImageView cell_content_UserImg;
        ImageView cell_title_categoryImg;
        TextView cell_title_userName;
        TextView cell_title_postTitle;
        TextView cell_title_postDate;
        TextView cell_title_postPrice;
        TextView cell_title_postLikes;
        TextView cell_title_postComments;
        TextView cell_content_postTitle;
        ImageView cell_content_PostImg;
        TextView cell_content_postDate;
        TextView cell_content_postPrice;
        TextView cell_content_postDesc;
        TextView cell_content_userName;
        FoldingCell foldingCell;
        CheckBox likeButton;
        TextView Likes;

        View view;

        public myViewHolder( View itemView) {
            super(itemView);
            view = itemView;
            cell_title_categoryImg = itemView.findViewById(R.id.titleLayoutLogo);
            cell_title_userName = itemView.findViewById(R.id.cell_title_userName);
            cell_title_postTitle = itemView.findViewById(R.id.cell_title_postTitle);
            cell_title_postDate = itemView.findViewById(R.id.cell_title_postDate);
            cell_title_postPrice = itemView.findViewById(R.id.cell_title_postPrice);
            cell_title_postLikes = itemView.findViewById(R.id.cell_title_likes);
            cell_title_postComments = itemView.findViewById(R.id.cell_title_comments);
            cell_content_postTitle = itemView.findViewById(R.id.Post_details_Title);
            cell_content_PostImg = itemView.findViewById(R.id.Post_details_postImg);
            cell_content_postDate = itemView.findViewById(R.id.Post_details_date);
            cell_content_postPrice = itemView.findViewById(R.id.Post_details_price);
            cell_content_postDesc = itemView.findViewById(R.id.Post_details_desc);
            cell_content_userName = itemView.findViewById(R.id.Post_details_username);
            cell_content_UserImg = itemView.findViewById(R.id.Post_details_userImg);
            foldingCell = itemView.findViewById(R.id.foldingCell);
            likeButton = itemView.findViewById(R.id.LikeButton);
            Likes = itemView.findViewById(R.id.likes_TV);
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
                        addLikeDisposable.clear();

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
                        unlikedisposable.clear();
                    }
                });
    }

    public void getpostLikes(String idpost, TextView likes){
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
                        }else {
                            likes.setText(""+Likes.getLikes());
                        }

                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Log.d(TAG,"onError: ",e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG,"onComplete: created");
                        disposable.clear();
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
                        isCheckedDisposable.clear();
                    }
                });
    }







}

