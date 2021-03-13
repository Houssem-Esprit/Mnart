package com.Syrine.mnart.Controllers.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Syrine.mnart.Controllers.Adapters.LikesAdapter;
import com.Syrine.mnart.Models.PostLikesResponse;
import com.Syrine.mnart.Models.UserResponse;
import com.Syrine.mnart.R;
import com.Syrine.mnart.Utils.DataManager.UserApi;
import com.Syrine.mnart.Utils.DataManager.UtilApi;
import com.Syrine.mnart.Utils.Session;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ProfileGuestPage extends Fragment {


    private static final String TAG = ProfileGuestPage.class.getCanonicalName();
    CompositeDisposable getUser_disposable = new CompositeDisposable();
    CompositeDisposable getFriendshipState_disposable = new CompositeDisposable();
    CompositeDisposable addFriendship_disposable = new CompositeDisposable();
    CompositeDisposable cancelFriendship_disposable = new CompositeDisposable();
    CompositeDisposable acceptFriendship_disposable = new CompositeDisposable();
    CompositeDisposable getUserDetails_disposable = new CompositeDisposable();
    private UserApi userApi;
    LikesAdapter likesAdapter;
    UserResponse userResponse;

    /**
     *
     */

    RecyclerView likesRecyclerView;
    LinearLayoutManager linearLayoutManager;
    ImageView backButton;
    private CircleImageView userImg;
    private TextView userName;
    private MaterialButton friendshipState;
    private ImageView userCoverImg;
    CardView user_To_user_setting_state_CardView;
    BottomSheetBehavior mbSheetBehavior;
    TextView delete_or_cancel_user_tv;
    TextView block_user_tv;


    // ** //
    View celebrationDialogView;
    CircleImageView myImg;
    CircleImageView myFriendImg;
    TextView greetingTv;
    MaterialButton shareButton;
    MaterialAlertDialogBuilder materialAlertDialogBuilder;

    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            int flags = View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

            Activity activity = getActivity();
            if (activity != null
                    && activity.getWindow() != null) {
                activity.getWindow().getDecorView().setSystemUiVisibility(flags);
            }
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }

        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };
    private View mContentView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };






    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_guest_profile, container, false);

        /**
         * ! UI vars binding
         */

        //likesRecyclerView = view.findViewById(R.id.likes_fragement_recyclerView);
        backButton = view.findViewById(R.id.likes_fragement_back_arrow);
        userImg = view.findViewById(R.id.guestprofile_userImg);
        friendshipState = view.findViewById(R.id.guest_profile_relationState_button);
        userName = view.findViewById(R.id.guestprofile_userName);
        userCoverImg = view.findViewById(R.id.guestprofile_userCoverImg);
        user_To_user_setting_state_CardView = view.findViewById(R.id.user_To_user_setting_state_CardView);

        // bottom sheet attrs
        mbSheetBehavior = BottomSheetBehavior.from(user_To_user_setting_state_CardView);
        delete_or_cancel_user_tv = view.findViewById(R.id.delete_or_cancel_user_tv);
        block_user_tv = view.findViewById(R.id.block_user_tv);

        /**
        * * Utils vars definition
        */

        mbSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        int idUser = getArguments().getInt("userID");
        String userName = getArguments().getString("userName");
        String userImg = getArguments().getString("userImg");


        userApi = UtilApi.getUserApi();

        linearLayoutManager =new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);


        /**
         * ? methods declarations & operation
         */

         GetUserDetails(idUser);
        //likesRecyclerView.setLayoutManager(linearLayoutManager);

        getFriendshipState(Session.getInstance().getUser().getIdUser(),
                idUser,friendshipState, mbSheetBehavior,userImg,userName);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        mbSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState){
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.d("SHEET EXP CALLBACK ","BottomSheetCallback");
                        //user_To_user_setting_state_CardView.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.d("SHEET HIDD CALLBACK ","BottomSheetCallback");
                        //user_To_user_setting_state_CardView.setVisibility(View.GONE);
                        break;


                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        return view;
    }


    /**
     *  ? Methods:
     * @param
     */

    public void GetUserDetails(int userID){

        userApi.getUserById(userID)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserResponse>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        Log.d(TAG,"onSubscribe: created");
                        getUserDetails_disposable.add(d);
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull UserResponse obj) {
                        Log.d(TAG,"onNext: Relation state ");
                        userResponse = obj;
                        String url = "http://localhost:3000/public/images/users/"+ obj.getResult().getImage();
                        //userImgName = obj.getResult().getImage();

                        Glide.with(getContext())
                                .load(url)
                                .into(userImg);

                        userName.setText(obj.getResult().getFirstName()+" "+obj.getResult().getLastName());
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


    public void getFriendshipState(int ThisuserSide,int OtherSideUser,MaterialButton mb,BottomSheetBehavior bs,String FriendImg, String FriendName){

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
                            mb.setIconTint(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.M_colorWhite)));
                            mb.setStrokeWidth(0);
                            mb.setStrokeColor(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.M_hyperlink_blue)));
                            mb.setBackgroundColor(getActivity().getResources().getColor(R.color.M_hyperlink_blue));
                            mb.setTextColor(getActivity().getResources().getColor(R.color.M_colorWhite));
                            mb.setText("ADD");
                            mb.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AddFriendRequest(ThisuserSide,OtherSideUser,mb);
                                }
                            });
                        }else if(Integer.parseInt(obj)==1){

                            mb.setIconResource(R.drawable.user_friend);
                            mb.setIconTint(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.M_hyperlink_blue)));
                            mb.setStrokeWidth(1);
                            mb.setStrokeColor(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.M_hyperlink_blue)));
                            mb.setBackgroundColor(getActivity().getResources().getColor(R.color.M_colorWhite));
                            mb.setTextColor(getActivity().getResources().getColor(R.color.M_hyperlink_blue));
                            mb.setText("Friends");
                            mb.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    LunchBottomSheet(FriendName,mb, bs , ThisuserSide,OtherSideUser,0);

                                }
                            });

                        }

                        else if(Integer.parseInt(obj)==2){
                            mb.setStrokeWidth(0);
                            mb.setStrokeColor(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.S_colorGray)));
                            mb.setBackgroundColor(getActivity().getResources().getColor(R.color.S_colorGray));
                            mb.setTextColor(getActivity().getResources().getColor(R.color.M_colorWhite));
                            mb.setText("Cancel");
                            mb.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.d("CANCEL  BUTTON CLICKED","CANCEL CLICKED");
                                    LunchBottomSheet(FriendName,mb,bs,ThisuserSide,OtherSideUser,1);
                                }
                            });

                        }else if (Integer.parseInt(obj)==3){
                            mb.setIconResource(R.drawable.add_friend_icon);
                            mb.setIconTint(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.M_colorBlackDark)));
                            mb.setStrokeWidth(1);
                            mb.setStrokeColor(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.M_colorBlackDark)));
                            mb.setBackgroundColor(getActivity().getResources().getColor(R.color.M_colorWhite));
                            mb.setText("Accept");
                            mb.setTextColor(getActivity().getResources().getColor(R.color.M_colorBlackDark));
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
                        mb.setStrokeColor(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.S_colorGray)));
                        mb.setBackgroundColor(getActivity().getResources().getColor(R.color.S_colorGray));
                        mb.setTextColor(getActivity().getResources().getColor(R.color.M_colorWhite));
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


    public void DeleteFriendShipRelation(int ThisuserSide,int OtherSideUser,MaterialButton mb){

        userApi.DeleteFriendShipRelation(ThisuserSide,OtherSideUser)
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
                        mbSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        mb.setIconResource(R.drawable.add_friend_icon);
                        mb.setIconTint(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.M_colorWhite)));
                        mb.setStrokeWidth(0);
                        mb.setText("ADD");
                        mb.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        mb.setStrokeColor(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.M_hyperlink_blue)));
                        mb.setBackgroundColor(getActivity().getResources().getColor(R.color.M_hyperlink_blue));
                        mb.setTextColor(getActivity().getResources().getColor(R.color.M_colorWhite));
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
                        mb.setStrokeColor(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.M_colorBlackDark)));
                        mb.setBackgroundColor(getActivity().getResources().getColor(R.color.M_colorWhite));
                        mb.setTextColor(getActivity().getResources().getColor(R.color.M_colorBlackDark));
                        mb.setText("Mention");
                        mb.setIconResource(R.drawable.comment_buble);
                        mb.setIconTint(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.M_colorBlackDark)));
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
                        mbSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        mb.setIconResource(R.drawable.add_friend_icon);
                        mb.setIconTint(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.M_colorWhite)));
                        mb.setStrokeWidth(0);
                        mb.setText("ADD");
                        mb.setStrokeColor(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.M_hyperlink_blue)));
                        mb.setBackgroundColor(getActivity().getResources().getColor(R.color.M_hyperlink_blue));
                        mb.setTextColor(getActivity().getResources().getColor(R.color.M_colorWhite));
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


    public void LunchFrindShipEventDialog(String FriendImg,String Friendname){

        String Friendurl = "http://localhost:3000/public/images/users/"+FriendImg;
        String Myurl = "http://localhost:3000/public/images/users/"+Session.getInstance().getUser().getImage();

        userApi = UtilApi.getUserApi();



        materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
        celebrationDialogView = LayoutInflater.from(getContext()).inflate(R.layout.celebration_dialog_view,null,false);
        myImg = celebrationDialogView.findViewById(R.id.celebration_dialog_myImg);
        myFriendImg = celebrationDialogView.findViewById(R.id.celebration_dialog_myFriendImg);
        greetingTv = celebrationDialogView.findViewById(R.id.celebration_dialog_greeting);
        shareButton = celebrationDialogView.findViewById(R.id.celebration_dialog_sharebutton);

        greetingTv.setText("You and "+Friendname+" have become new Friends");

        Glide.with(getContext())
                .load(Myurl)
                .into(myImg);
        Glide.with(getContext())
                .load(Friendurl)
                .into(myFriendImg);
        materialAlertDialogBuilder.setView(celebrationDialogView).show();

    }


    public void LunchFrindRequestDeleteDialog(String Friendname,MaterialButton mb, int ThisuserSide,int OtherSideUser){

        materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getContext());

        materialAlertDialogBuilder.setMessage("Are you sure you want to cancel your friend request sent to "+Friendname+" ?")
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


    public void LunchFrindShipDeleteDialog(String Friendname,MaterialButton mb, int ThisuserSide,int OtherSideUser){

        materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getContext());

        materialAlertDialogBuilder.setMessage("Are you sure you want to delete "+Friendname+" from your friend list ?")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteFriendShipRelation(ThisuserSide, OtherSideUser, mb);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();

    }


    public void LunchBottomSheet(String Friendname,MaterialButton mb, BottomSheetBehavior bs , int ThisuserSide,int OtherSideUser,int choice){

        bs.setState(BottomSheetBehavior.STATE_EXPANDED);

        if(choice == 0){

            delete_or_cancel_user_tv.setText("Delete "+Friendname+" from your friend list");

            delete_or_cancel_user_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LunchFrindShipDeleteDialog(Friendname,mb,ThisuserSide,OtherSideUser);

                }
            });

        }else {

            Log.d("SHEET EXPANDED ","CANCEL SECTION");
            delete_or_cancel_user_tv.setText("Cancel your friend request to "+Friendname);

            delete_or_cancel_user_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LunchFrindRequestDeleteDialog(Friendname,mb,ThisuserSide,OtherSideUser);

                }
            });

        }

    }












    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mVisible = true;

        mContentView = view.findViewById(R.id.fullscreen_content);

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //view.findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && getActivity().getWindow() != null) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null && getActivity().getWindow() != null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            // Clear the systemUiVisibility flag
            getActivity().getWindow().getDecorView().setSystemUiVisibility(0);
        }
        show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContentView = null;
        cancelFriendship_disposable.clear();
        acceptFriendship_disposable.clear();
        cancelFriendship_disposable.clear();
        addFriendship_disposable.clear();
        getUserDetails_disposable.clear();
        getFriendshipState_disposable.clear();

    }

    @Override
    public void onStop() {
        super.onStop();
        cancelFriendship_disposable.clear();
        acceptFriendship_disposable.clear();
        cancelFriendship_disposable.clear();
        addFriendship_disposable.clear();
        getUserDetails_disposable.clear();
        getFriendshipState_disposable.clear();

    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Nullable
    private ActionBar getSupportActionBar() {
        ActionBar actionBar = null;
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            actionBar = activity.getSupportActionBar();
        }
        return actionBar;
    }
}