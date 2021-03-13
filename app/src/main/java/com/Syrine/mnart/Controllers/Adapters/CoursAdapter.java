package com.Syrine.mnart.Controllers.Adapters;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.Syrine.mnart.Models.CoursPost;
import com.Syrine.mnart.R;
import com.Syrine.mnart.Utils.Const;
import com.Syrine.mnart.Utils.DataManager.UserApi;
import com.Syrine.mnart.Utils.DataManager.UtilApi;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
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
import okhttp3.ResponseBody;

public class CoursAdapter extends RecyclerView.Adapter<CoursAdapter.myViewHolder> implements LifecycleObserver {
    Context mContext;
    Activity activity;
    myViewHolder mholder ;
    List<CoursPost> coursPostsList;
    private UserApi userApi;
    CompositeDisposable disposable = new CompositeDisposable();
    static final String TAG = "COURS_ADAPTER";
    long currentPosition;



    public CoursAdapter(Context context, Activity activity, List<CoursPost> list){
        this.mContext = context;
        this.activity=activity;
        this.coursPostsList=list;
    }


    @NonNull
    @Override
    public CoursAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =LayoutInflater.from(mContext);
        View v=inflater.inflate(R.layout.cours_card,parent,false);
        return new myViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CoursAdapter.myViewHolder holder, int position) {
        mholder = holder;
        userApi = UtilApi.getUserApi();

        // UserImg
        String url = "http://localhost:3000/public/images/users/"+coursPostsList.get(position).getUserImg();
        Glide.with(mContext)
                .load(url)
                .into(holder.userImg);
         String url_video ="http://localhost:3000/public/videos/cours/"+coursPostsList.get(position).getCoursVideo();
        // UserName
        holder.userName.setText(coursPostsList.get(position).getFirstName()+" "+coursPostsList.get(position).getLastName());

        // Cours Date
        try {
            holder.date.setText(Const.generateDateTimeFromTimestamp(coursPostsList.get(position).getDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Cours Description
        holder.coursDesc.setText(coursPostsList.get(position).getCoursDesc());
        // Cours Title
        holder.coursTitle.setText(coursPostsList.get(position).getTile());
        // Cours video
        Uri uri = Uri.parse(url_video);
        holder.playerView.setVideoURI(uri);
        //holder.playerView.seekTo(3);
        holder.playerView.setZOrderOnTop(true);
        // Cours Views
        holder.coursViews.setText(coursPostsList.get(position).getViews()+"");
        // Cours Video
        //long thumb = getLayoutPosition()*1000;
        RequestOptions options = new RequestOptions().frame(10);
        Glide.with(mContext)
                .load(uri)
                .into(holder.videoThumbnail);

        holder.playVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userApi.updateViews(coursPostsList.get(position).getIdCours())
                        .toObservable()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<ResponseBody>() {
                            @Override
                            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                                Log.d(TAG,"onSubscribe: created");
                                disposable.add(d);
                            }

                            @Override
                            public void onNext(@io.reactivex.rxjava3.annotations.NonNull ResponseBody cat) {
                                Log.d(TAG,"onNext: ");

                            }

                            @Override
                            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                                Log.d(TAG,"onError: ",e);
                            }

                            @Override
                            public void onComplete() {
                                Log.d(TAG,"onComplete: created");
                            }
                        });
                        holder.playVideo.setVisibility(View.GONE);
                        holder.videoThumbnail.setVisibility(View.GONE);
                        holder.playerView.start();


            }
        });




        holder.playerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.playerView.stopPlayback();
                Bundle bundle= new Bundle();
                bundle.putString("url_video",url_video);
                bundle.putLong("vedioPosition",currentPosition);
                final NavController navController = Navigation.findNavController(activity,R.id.nav_host_fragment);
                navController.navigate(R.id.action_video_to_videofragment,bundle);
            }
        });



    }

    @Override
    public int getItemCount() {
        return coursPostsList.size();
    }




    public class myViewHolder extends  RecyclerView.ViewHolder{

        CircleImageView userImg;
        TextView userName;
        TextView date;
        TextView coursDesc;
        TextView coursTitle;
        VideoView playerView;
        FloatingActionButton playVideo;
        ImageButton goToDetails;
        TextView coursViews;
        ImageView videoThumbnail;


        View view;

        public myViewHolder( View itemView) {
            super(itemView);
            view = itemView;
            userImg = itemView.findViewById(R.id.cours_userImg);
            userName = itemView.findViewById(R.id.cours_username);
            date = itemView.findViewById(R.id.cours_date);
            coursDesc = itemView.findViewById(R.id.cours_desc);
            coursTitle = itemView.findViewById(R.id.cours_title);
            playerView = itemView.findViewById(R.id.video_view);
            playVideo = itemView.findViewById(R.id.cours_play_video);
            goToDetails = itemView.findViewById(R.id.cours_go_to_details);
            coursViews=itemView.findViewById(R.id.cours_views);
            videoThumbnail = itemView.findViewById(R.id.videoThumbnail);


        }

        public View getView()
        {
            return view;
        }

    }



    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onStart() {
        disposable.clear();
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void onPause() {

    }
}

