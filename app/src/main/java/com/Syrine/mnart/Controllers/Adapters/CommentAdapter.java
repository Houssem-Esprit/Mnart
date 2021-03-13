package com.Syrine.mnart.Controllers.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Syrine.mnart.Controllers.Fragments.CommentPage;
import com.Syrine.mnart.Models.Category;
import com.Syrine.mnart.Models.CommentUser;
import com.Syrine.mnart.Models.SubComment;
import com.Syrine.mnart.R;
import com.Syrine.mnart.Utils.Const;
import com.Syrine.mnart.Utils.DataManager.UserApi;
import com.Syrine.mnart.Utils.DataManager.UtilApi;
import com.Syrine.mnart.Utils.Session;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.myViewHolder>  implements LifecycleObserver {
    Context mContext;
    Activity activity;
    List<CommentUser> CommentList;
    private static final String TAG = CommentAdapter.class.getCanonicalName();
    CompositeDisposable add_subcomment_disposable = new CompositeDisposable();
    CompositeDisposable get_subcomment_disposable = new CompositeDisposable();
    CompositeDisposable count_subcomment_disposable = new CompositeDisposable();
    private UserApi userApi;
    LinearLayoutManager linearLayoutManager;
    SubCommentAdapter subCommentAdapter;



    public CommentAdapter(Context context, Activity activity, List<CommentUser> list){
        this.mContext = context;
        this.activity=activity;
        this.CommentList=list;
    }

    public CommentAdapter() {
    }

    @NonNull
    @Override
    public CommentAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =LayoutInflater.from(mContext);
        View v=inflater.inflate(R.layout.comment_card,parent,false);
        return new myViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.myViewHolder holder, int position) {

        /**
         * * Utils vars definition
         */

        linearLayoutManager =new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        String url = "http://localhost:3000/public/images/users/"+CommentList.get(position).getUser_img();
        String url2 = "http://localhost:3000/public/images/users/"+Session.getInstance().getUser().getImage();
        userApi = UtilApi.getUserApi();

        /**
         * ? methods declarations & operations
         */

        /**
         *  Comment context methods and operations
         */

        Glide.with(mContext)
                .load(url)
                .into(holder.userImg);

        holder.CommentUser_name.setText(CommentList.get(position).getUser_firstName()+" "+CommentList.get(position).getUser_lastName());
        holder.Comment_commentText.setText(CommentList.get(position).getComment());
        //holder.Comment_date.setText(Const.generateElapsedTime(CommentList.get(position).getDate_comment()));

        holder.CommentUser_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle= new Bundle();
                bundle.putInt("userID",CommentList.get(position).getId_user());
                bundle.putString("userImg",CommentList.get(position).getUser_img());
                bundle.putString("userName",CommentList.get(position).getUser_firstName()+" "+CommentList.get(position).getUser_lastName());

                final NavController navController = Navigation.findNavController(activity,R.id.nav_host_fragment);
                navController.navigate(R.id.action_allPosts_to_profileGuestPage,bundle);
            }
        });

        /**
         *  Sub Comment context methods and operations
         */


        if(CommentList.get(position).getSubcomments_Nbr_Per_Post()==0){

            holder.Comment_subComments_indicator.setVisibility(View.GONE);

        }else if (CommentList.get(position).getSubcomments_Nbr_Per_Post()==1){

            holder.Comment_subComments_indicator.setVisibility(View.VISIBLE);
            holder.Comment_subComments_indicator.setText("See 1 other reply...");

        }else {

            holder.Comment_subComments_indicator.setVisibility(View.VISIBLE);
            holder.Comment_subComments_indicator.setText("See "+CommentList.get(position).getSubcomments_Nbr_Per_Post()+" "+"other replies...");
        }

        holder.subCommentRecyclerView.setLayoutManager(linearLayoutManager);

        Glide.with(mContext)
                .load(url)
                .into(holder.user_commenter_img);



        getsubComments(CommentList.get(position).getId_user(),
                CommentList.get(position).getId_comment(),
                holder.subCommentRecyclerView);

        holder.Comment_subComments_indicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.subCommentRecyclerView.setVisibility(View.VISIBLE);
                //holder.subComment_commentInput_container.setVisibility(View.G);

                if (holder.Comment_subComments_indicator.getVisibility() == View.VISIBLE){

                    holder.Comment_subComments_indicator.setVisibility(View.GONE);
                }
            }
        });

        holder.Comment_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.subCommentRecyclerView.setVisibility(View.VISIBLE);
                holder.subComment_commentInput_container.setVisibility(View.VISIBLE);

                holder.cardView_CommentView.setVisibility(View.GONE);

                holder.subcomment_input.requestFocus();
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(holder.subcomment_input, InputMethodManager.SHOW_IMPLICIT);

                if (holder.Comment_subComments_indicator.getVisibility() == View.VISIBLE){

                    holder.Comment_subComments_indicator.setVisibility(View.GONE);
                }
            }
        });



            holder.subcomment_input_layout.setEndIconOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addsubComment(CommentList.get(position).getId_user(),
                            CommentList.get(position).getId_comment(),
                            holder.subcomment_input,holder.subcomment_input_layout,
                            holder.subCommentRecyclerView);
                }
            });

            holder.close_CommentPopup_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.subComment_commentInput_container.setVisibility(View.GONE);
                    holder.cardView_CommentView.setVisibility(View.VISIBLE);
                }
            });

    }

    @Override
    public int getItemCount() {
        return CommentList.size();
    }



    public class myViewHolder extends  RecyclerView.ViewHolder{

        CircleImageView userImg;

        TextView CommentUser_name,
                Comment_commentText,
                Comment_date,Comment_reply,
                Comment_subComments_indicator;

        RecyclerView subCommentRecyclerView;

        CardView subComment_commentInput_container,cardView_CommentView;

        TextInputLayout subcomment_input_layout;

        TextInputEditText subcomment_input;

        CircleImageView user_commenter_img;

        ImageView close_CommentPopup_button;

        View view;

        public myViewHolder( View itemView) {
            super(itemView);
            view = itemView;
            userImg = itemView.findViewById(R.id.CommentUser_img);
            CommentUser_name= itemView.findViewById(R.id.CommentUser_name);
            Comment_commentText= itemView.findViewById(R.id.Comment_commentText);
            Comment_date = itemView.findViewById(R.id.Comment_date);
            Comment_reply = itemView.findViewById(R.id.Comment_reply);
            Comment_subComments_indicator = itemView.findViewById(R.id.Comment_subComments_indicator);
            subCommentRecyclerView = itemView.findViewById(R.id.subComment_Recyclerview);
            subComment_commentInput_container = itemView.findViewById(R.id.subComment_commentInput_container);
            subcomment_input_layout = itemView.findViewById(R.id.subcommentPage_TextInputLayout);
            subcomment_input = itemView.findViewById(R.id.subCommentPage_textInput);
            user_commenter_img = itemView.findViewById(R.id.CreatesubComment_imgUser);
            cardView_CommentView = activity.findViewById(R.id.cardView_CommentView);
            close_CommentPopup_button = itemView.findViewById(R.id.closeCommentInput_popup);

        }
        public View getView()
        {
            return view;
        }
    }













    public void addsubComment(int idCmnt_owner,int idcomment,TextInputEditText Ti,TextInputLayout Tl, RecyclerView rv){
        int idUserCommenter = Session.getInstance().getUser().getIdUser();

        if (!TextUtils.isEmpty(Ti.getText())){

            userApi.addsubComment(idCmnt_owner,idcomment,idUserCommenter,Ti.getText().toString())
                    .toObservable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                            Log.d(TAG,"onSubscribe: created");
                            add_subcomment_disposable.add(d);
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
                            Ti.getText().clear();
                            getsubComments(idCmnt_owner,idcomment,rv);
                        }
                    });

        }else {
            Tl.setError("comment content is empty!");
        }
    }

    public void getsubComments(int idCmnt_owner,int idcomment,RecyclerView rv){

        userApi.getsubCommentsByUser(idCmnt_owner,idcomment)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<SubComment>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        Log.d(TAG,"onSubscribe: created");
                        get_subcomment_disposable.add(d);
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<SubComment> obj) {
                        Log.d(TAG,"onNext: "+obj);

                        subCommentAdapter = new SubCommentAdapter(mContext,activity,obj);
                        rv.setAdapter(subCommentAdapter);
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

    public void countsubComments(int idCmnt_owner,int idcomment,TextView Tv){

        userApi.countsubComments(idCmnt_owner,idcomment)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SubComment>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        Log.d(TAG,"onSubscribe: created");
                        count_subcomment_disposable.add(d);
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull SubComment obj) {
                        Log.d(TAG,"onNext: count comments "+obj.getSubcomments_Nbr_Per_Post());
                        Toast.makeText(mContext, "COUNT: "+obj.getSubcomments_Nbr_Per_Post(), Toast.LENGTH_SHORT).show();
                        if(obj.getSubcomments_Nbr_Per_Post()==0){
                            Tv.setVisibility(View.VISIBLE);
                            Tv.setText("See "+obj.getSubcomments_Nbr_Per_Post()+" other reply...");
                        }else if (obj.getSubcomments_Nbr_Per_Post()==1){
                            Tv.setVisibility(View.VISIBLE);
                            Tv.setText("See 1 other reply...");
                        }else {
                            Tv.setVisibility(View.VISIBLE);
                            Tv.setText("See"+obj.getSubcomments_Nbr_Per_Post()+" other replies...");
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Log.d(TAG,"onError: getComments",e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG,"onComplete: created");
                        count_subcomment_disposable.clear();
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
            add_subcomment_disposable.clear();
            get_subcomment_disposable.clear();
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
            add_subcomment_disposable.clear();
            get_subcomment_disposable.clear();
        }
    }


}

