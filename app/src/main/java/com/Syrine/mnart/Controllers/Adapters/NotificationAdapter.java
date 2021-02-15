package com.Syrine.mnart.Controllers.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.Syrine.mnart.Models.Category;
import com.Syrine.mnart.Models.Notification;
import com.Syrine.mnart.R;
import com.Syrine.mnart.Utils.DataManager.UserApi;
import com.Syrine.mnart.Utils.DataManager.UtilApi;
import com.Syrine.mnart.Utils.Session;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.myViewHolder> {
    Context mContext;
    Activity activity;
    List<Notification> NotificationList;
    private static final String TAG = "NOTIFICATIONS_ADAPTER";
    CompositeDisposable updateNotifdisposable = new CompositeDisposable();
    private UserApi userApi;

    public NotificationAdapter(Context context, Activity activity, List<Notification> list){
        this.mContext = context;
        this.activity=activity;
        this.NotificationList=list;
    }

    @NonNull
    @Override
    public NotificationAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =LayoutInflater.from(mContext);
        View v=inflater.inflate(R.layout.notification_card_layout,parent,false);
        return new myViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.myViewHolder holder, int position) {
        //vars
        userApi = UtilApi.getUserApi();
        String url = "http://localhost:3000/public/images/users/"+NotificationList.get(position).getUserImage();
        Glide.with(mContext)
                .load(url)
                .into(holder.userReactorImg);

        // notification content config
        String userReactorName = NotificationList.get(position).getFirstName()+" "+NotificationList.get(position).getLastName();
        String postTitle = NotificationList.get(position).getTitle();
        String notifContent = mContext.getString(R.string.notification_content).replace("%1$d",userReactorName).replace("%2$d",postTitle);
        Log.d(TAG,"SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS notif content: "+postTitle);
        int indexUserName = notifContent.indexOf(userReactorName);
        int length = userReactorName.length();

        SpannableString sp = new SpannableString(notifContent);
        sp.setSpan(new StyleSpan(Typeface.BOLD),indexUserName,indexUserName+length,SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new RelativeSizeSpan(1.2f),indexUserName,indexUserName+length,SPAN_EXCLUSIVE_EXCLUSIVE);

        // notification content in case it is a personal reaction

        String personalNotifcontent = mContext.getString(R.string.notification_content_personal).replace("%1$d",postTitle);
        int indexTitle = personalNotifcontent.indexOf(postTitle);
        int lengthTitle = postTitle.length();
        SpannableString sp2 = new SpannableString(personalNotifcontent);
        sp2.setSpan(new StyleSpan(Typeface.BOLD),indexTitle,indexTitle+lengthTitle,SPAN_EXCLUSIVE_EXCLUSIVE);

        if(NotificationList.get(position).getUserId() == Session.getInstance().getUser().getIdUser()){
            holder.NotificationContent.setText(sp2);
        }else {
            holder.NotificationContent.setText(sp);
        }

        // date config (timestamp convertion)
        long date = Long.parseLong(NotificationList.get(position).getReactionDate());
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date);
        Date d = c.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        holder.NotificationDate.setText(sdf.format(d).toString());

        // check if the notification is get viewed by user
        if(NotificationList.get(position).getIsNotifChecked()==1){
            holder.background.setBackgroundColor(mContext.getResources().getColor(R.color.M_disactive_pink));
        }else {
            holder.background.setBackgroundColor(mContext.getResources().getColor(R.color.M_colorWhite));
        }

        holder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Bundle bundle= new Bundle();
                bundle.putString("idPost",NotificationList.get(position).getIdPost());
                bundle.putInt("CatID",NotificationList.get(position).getIdCategory());
                final NavController navController = Navigation.findNavController(activity,R.id.nav_host_fragment);
                navController.navigate(R.id.action_notificationS_to_allPosts,bundle);
                updateNotificationCheckState(NotificationList.get(position).getLikeId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return NotificationList.size();
    }



    public class myViewHolder extends  RecyclerView.ViewHolder{
        CircleImageView userReactorImg;
        TextView NotificationContent;
        TextView NotificationDate;
        LinearLayout background;
        View view;

        public myViewHolder( View itemView) {
            super(itemView);
            view = itemView;
            userReactorImg = itemView.findViewById(R.id.notif_userReactorImg);
            NotificationContent = itemView.findViewById(R.id.notif_content);
            NotificationDate = itemView.findViewById(R.id.notif_date);
            background = itemView.findViewById(R.id.notif_card_id);
        }
        public View getView()
        {
            return view;
        }
    }



    public void  updateNotificationCheckState(int idPostLiked){
        userApi.updateIsNotifCheckedState(idPostLiked)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        Log.d(TAG,"onSubscribe: created");
                        updateNotifdisposable.add(d);
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
                        updateNotifdisposable.clear();
                    }
                });


    }
}

