package com.Syrine.mnart.Controllers.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.Syrine.mnart.Models.Category;
import com.Syrine.mnart.Models.PostLikesResponse;
import com.Syrine.mnart.Models.SubComment;
import com.Syrine.mnart.R;
import com.Syrine.mnart.Utils.Const;
import com.Syrine.mnart.Utils.DataManager.UserApi;
import com.Syrine.mnart.Utils.DataManager.UtilApi;
import com.Syrine.mnart.Utils.Session;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class LikesAdapter extends RecyclerView.Adapter<LikesAdapter.myViewHolder>  implements LifecycleObserver {
    Context mContext;
    Activity activity;
    List<PostLikesResponse> LikesList;
    View celebrationDialogView;
    CircleImageView myImg;
    CircleImageView myFriendImg;
    TextView greetingTv;
    MaterialButton shareButton;
    MaterialAlertDialogBuilder materialAlertDialogBuilder;


    private static final String TAG = LikesAdapter.class.getCanonicalName();
    CompositeDisposable getFriendshipState_disposable = new CompositeDisposable();
    CompositeDisposable addFriendship_disposable = new CompositeDisposable();
    CompositeDisposable cancelFriendship_disposable = new CompositeDisposable();
    CompositeDisposable acceptFriendship_disposable = new CompositeDisposable();

    private UserApi userApi;

    public LikesAdapter(Context context, Activity activity, List<PostLikesResponse> list){
        this.mContext = context;
        this.activity=activity;
        this.LikesList=list;
    }

    public LikesAdapter() {
    }

    @NonNull
    @Override
    public LikesAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =LayoutInflater.from(mContext);
        View v=inflater.inflate(R.layout.like_card,parent,false);
        return new myViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LikesAdapter.myViewHolder holder, int position) {
        String url = "http://localhost:3000/public/images/users/"+LikesList.get(position).getImageUser();
        userApi = UtilApi.getUserApi();

        Glide.with(mContext)
                .load(url)
                .into(holder.likes_userReactorImg);

        getFriendshipState(Session.getInstance().getUser().getIdUser(),
                LikesList.get(position).getIdUserLiker(),
                holder.mentionUser_button,
                LikesList.get(position).getImageUser(),
                LikesList.get(position).getFirstName()+" "+LikesList.get(position).getLastName());


        holder.userName.setText(LikesList.get(position).getFirstName()+" "+LikesList.get(position).getLastName());
        holder.likes_date.setText(Const.generateElapsedTime(LikesList.get(position).getTimestamp()));




    }

    @Override
    public int getItemCount() {
        return LikesList.size();
    }



    public class myViewHolder extends  RecyclerView.ViewHolder{
        CircleImageView likes_userReactorImg;
        TextView userName, likes_date;
        MaterialButton mentionUser_button;
        View view;

        public myViewHolder( View itemView) {
            super(itemView);
            view = itemView;
            likes_date = itemView.findViewById(R.id.likes_date);
            userName= itemView.findViewById(R.id.userName);
            likes_userReactorImg = itemView.findViewById(R.id.likes_userReactorImg);
            mentionUser_button = itemView.findViewById(R.id.mentionUser_button);

        }
        public View getView()
        {
            return view;
        }
    }





    public void getFriendshipState(int ThisuserSide,int OtherSideUser,MaterialButton mb,String FriendImg, String FriendName){

        userApi.getUserToUserRelationState(ThisuserSide,OtherSideUser)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        Log.d(TAG,"onSubscribe: created");
                        getFriendshipState_disposable.add(d);
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull String obj) {
                        Log.d(TAG,"onNext: Relation state "+obj.toString());

                        if(Integer.parseInt(obj)==0){
                            mb.setIconResource(R.drawable.add_friend_icon);
                            mb.setIconTint(ColorStateList.valueOf(activity.getResources().getColor(R.color.M_colorWhite)));
                            mb.setStrokeWidth(0);
                            mb.setStrokeColor(ColorStateList.valueOf(activity.getResources().getColor(R.color.M_hyperlink_blue)));
                            mb.setBackgroundColor(activity.getResources().getColor(R.color.M_hyperlink_blue));
                            mb.setTextColor(activity.getResources().getColor(R.color.M_colorWhite));
                            mb.setText("ADD");
                            mb.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AddFriendRequest(ThisuserSide,OtherSideUser,mb);
                                }
                            });
                        }else if(Integer.parseInt(obj)==2){
                            mb.setStrokeWidth(0);
                            mb.setStrokeColor(ColorStateList.valueOf(activity.getResources().getColor(R.color.S_colorGray)));
                            mb.setBackgroundColor(activity.getResources().getColor(R.color.S_colorGray));
                            mb.setTextColor(activity.getResources().getColor(R.color.M_colorWhite));
                            mb.setText("Cancel");
                            mb.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    LunchFrindShipDeleteDialog(FriendName,mb,ThisuserSide,OtherSideUser);
                                }
                            });

                        }else if (Integer.parseInt(obj)==3){
                            mb.setIconResource(R.drawable.add_friend_icon);
                            mb.setIconTint(ColorStateList.valueOf(activity.getResources().getColor(R.color.M_colorBlackDark)));
                            mb.setStrokeWidth(1);
                            mb.setStrokeColor(ColorStateList.valueOf(activity.getResources().getColor(R.color.M_colorBlackDark)));
                            mb.setBackgroundColor(activity.getResources().getColor(R.color.M_colorWhite));
                            mb.setText("Accept");
                            mb.setTextColor(activity.getResources().getColor(R.color.M_colorBlackDark));
                            mb.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AcceptFriendRequest(ThisuserSide,OtherSideUser,mb,FriendImg, FriendName);
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Log.d(TAG,"onError: userToUserFriendshipState",e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG,"onComplete: created");
                        //getFriendshipState_disposable.clear();
                    }
                });
    }



    public void AddFriendRequest(int ThisuserSide,int OtherSideUser,MaterialButton mb){

        userApi.AddFriendRequest(ThisuserSide,OtherSideUser)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        Log.d(TAG,"onSubscribe: created");
                        addFriendship_disposable.add(d);
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull ResponseBody obj) {
                        Log.d(TAG,"onNext: Relation state ");
                       // mb.setIconResource(R.drawable.add_friend_icon);
                       // mb.setIconTint(ColorStateList.valueOf(activity.getResources().getColor(R.color.M_colorWhite)));
                        mb.setStrokeWidth(0);
                        mb.setStrokeColor(ColorStateList.valueOf(activity.getResources().getColor(R.color.S_colorGray)));
                        mb.setBackgroundColor(activity.getResources().getColor(R.color.S_colorGray));
                        mb.setTextColor(activity.getResources().getColor(R.color.M_colorWhite));
                        mb.setText("Cancel");
                        mb.setIcon(null);
                        mb.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelFriendRequest(ThisuserSide, OtherSideUser, mb);
                            }
                        });
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Log.d(TAG,"onError: getComments",e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG,"onComplete: created");

                    }
                });
    }


    public void cancelFriendRequest(int ThisuserSide,int OtherSideUser,MaterialButton mb){

        userApi.CancelFriendRequest(ThisuserSide,OtherSideUser)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        Log.d(TAG,"onSubscribe: created");
                        cancelFriendship_disposable.add(d);
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull ResponseBody obj) {
                        Log.d(TAG,"onNext: Relation state ");
                        // mb.setIconResource(R.drawable.add_friend_icon);
                        // mb.setIconTint(ColorStateList.valueOf(activity.getResources().getColor(R.color.M_colorWhite)));
                        mb.setIconResource(R.drawable.add_friend_icon);
                        mb.setIconTint(ColorStateList.valueOf(activity.getResources().getColor(R.color.M_colorWhite)));
                        mb.setStrokeWidth(0);
                        mb.setText("ADD");
                        mb.setStrokeColor(ColorStateList.valueOf(activity.getResources().getColor(R.color.M_hyperlink_blue)));
                        mb.setBackgroundColor(activity.getResources().getColor(R.color.M_hyperlink_blue));
                        mb.setTextColor(activity.getResources().getColor(R.color.M_colorWhite));
                        mb.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AddFriendRequest(ThisuserSide,OtherSideUser,mb);
                            }
                        });
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Log.d(TAG,"onError: getComments",e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG,"onComplete: created");

                    }
                });
    }


    public void AcceptFriendRequest(int ThisuserSide,int OtherSideUser,MaterialButton mb,String FriendImg, String FriendName){

        userApi.AcceptFriendRequest(ThisuserSide,OtherSideUser)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        Log.d(TAG,"onSubscribe: created");
                        acceptFriendship_disposable.add(d);
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull ResponseBody obj) {
                        Log.d(TAG,"onNext: Relation state ");

                        LunchFrindShipEventDialog(FriendImg,FriendName);

                        mb.setStrokeWidth(1);
                        mb.setStrokeColor(ColorStateList.valueOf(activity.getResources().getColor(R.color.M_colorBlackDark)));
                        mb.setBackgroundColor(activity.getResources().getColor(R.color.M_colorWhite));
                        mb.setTextColor(activity.getResources().getColor(R.color.M_colorBlackDark));
                        mb.setText("Mention");
                        mb.setIconResource(R.drawable.comment_buble);
                        mb.setIconTint(ColorStateList.valueOf(activity.getResources().getColor(R.color.M_colorBlackDark)));
                        mb.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        });
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Log.d(TAG,"onError: getComments",e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG,"onComplete: created");

                    }
                });
    }



    public void LunchFrindShipEventDialog(String FriendImg,String Friendname){

        String Friendurl = "http://localhost:3000/public/images/users/"+FriendImg;
        String Myurl = "http://localhost:3000/public/images/users/"+Session.getInstance().getUser().getImage();

        userApi = UtilApi.getUserApi();



        materialAlertDialogBuilder = new MaterialAlertDialogBuilder(mContext);
        celebrationDialogView = LayoutInflater.from(mContext).inflate(R.layout.celebration_dialog_view,null,false);
        myImg = celebrationDialogView.findViewById(R.id.celebration_dialog_myImg);
        myFriendImg = celebrationDialogView.findViewById(R.id.celebration_dialog_myFriendImg);
        greetingTv = celebrationDialogView.findViewById(R.id.celebration_dialog_greeting);
        shareButton = celebrationDialogView.findViewById(R.id.celebration_dialog_sharebutton);

        greetingTv.setText("You and "+Friendname+" have become new Friends");

        Glide.with(mContext)
                .load(Myurl)
                .into(myImg);
        Glide.with(mContext)
                .load(Friendurl)
                .into(myFriendImg);
        materialAlertDialogBuilder.setView(celebrationDialogView).show();

    }


    public void LunchFrindShipDeleteDialog(String Friendname,MaterialButton mb, int ThisuserSide,int OtherSideUser){

        materialAlertDialogBuilder = new MaterialAlertDialogBuilder(mContext);

        materialAlertDialogBuilder.setMessage("Do you realy want to cancel your friend request to "+Friendname+" ?")
           .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancelFriendRequest(ThisuserSide, OtherSideUser, mb);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();

    }



    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy() {
        if ((Util.SDK_INT <= 23)) {
            getFriendshipState_disposable.clear();
            addFriendship_disposable.clear();
            cancelFriendship_disposable.clear();
            acceptFriendship_disposable.clear();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onStop() {
        if (Util.SDK_INT > 23) {
            getFriendshipState_disposable.clear();
            addFriendship_disposable.clear();
            cancelFriendship_disposable.clear();
            acceptFriendship_disposable.clear();

        }
    }
}

