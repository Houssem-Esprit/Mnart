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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.Syrine.mnart.Models.Category;
import com.Syrine.mnart.Models.Post;
import com.Syrine.mnart.R;
import com.Syrine.mnart.Utils.DataManager.UserApi;
import com.Syrine.mnart.Utils.DataManager.UtilApi;
import com.Syrine.mnart.Utils.Session;
import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
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

public class MyworkAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    Activity activity;
    List<Post> PostsList;
    private final int SHOW_MENU = 1;
    private final int HIDE_MENU = 2;
    CompositeDisposable disposable = new CompositeDisposable();
    private static final String TAG = "MY-WORKS_FRAGMENT";

    private UserApi userApi;


    public MyworkAdapter(Context context, Activity activity, List<Post> list){
        this.mContext = context;
        this.activity=activity;
        this.PostsList=list;
    }

    @Override
    public int getItemViewType(int position) {
        if(PostsList.get(position).getShowMenu()==true){
            return SHOW_MENU;

        }else {
            return HIDE_MENU;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =LayoutInflater.from(mContext);
        View v ;
        if(viewType==SHOW_MENU){
            v = inflater.inflate(R.layout.mywork_menu,parent,false);
            return new MenuViewHolder(v);
        }else{
            v = inflater.inflate(R.layout.my_work_card,parent,false);
            return new myViewHolder(v);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof myViewHolder){
            String url_img_post = "http://localhost:3000/public/images/posts/"+PostsList.get(position).getImgPost();
            String url_img_user = "http://localhost:3000/public/images/users/"+ Session.getInstance().getUser().getImage();
            Glide.with(mContext)
                    .load(url_img_post)
                    .into(((myViewHolder)holder).postImg);
            Glide.with(mContext)
                    .load(url_img_user)
                    .into(((myViewHolder)holder).userImg);

            ((myViewHolder)holder).postTitle.setText(PostsList.get(position).getTitle());


            ((myViewHolder)holder).getView().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showMenu(position);
                    return true;
                }
            });

        }

        if (holder instanceof MenuViewHolder){
            userApi = UtilApi.getUserApi();

            ((MenuViewHolder)holder).DeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext);
                    builder.setTitle(R.string.alerte_title);
                    builder.setMessage(R.string.alerte_support_text);
                    builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            userApi.DeletePostById(PostsList.get(position).getId())
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
                                            removeAt(position);
                                        }
                                    });
                        }
                    });

                    builder.setNegativeButton("DISMISS", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {


                        }
                    });

                    builder.show();
                }
            });


            ((MenuViewHolder)holder).EditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Bundle bundle= new Bundle();
                    bundle.putString("postID",PostsList.get(position).getId());
                    final NavController navController = Navigation.findNavController(activity,R.id.nav_host_fragment);
                    navController.navigate(R.id.action_myWorksPage_to_editPage,bundle);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return PostsList.size();
    }



    public class myViewHolder extends  RecyclerView.ViewHolder{
        ImageView postImg;
        CircleImageView userImg;
        TextView postTitle;
        TextView postDate;
        Chip chip;
        View view;

        public myViewHolder( View itemView) {
            super(itemView);
            view = itemView;
            postImg = itemView.findViewById(R.id.myWork_postImg);
            userImg = itemView.findViewById(R.id.myWork_userImg);
            postDate= itemView.findViewById(R.id.myWork_postDate);
            postTitle= itemView.findViewById(R.id.myWork_postTitle);
            chip= itemView.findViewById(R.id.chip_1);
        }
        public View getView()
        {
            return view;
        }
    }


    public class MenuViewHolder extends  RecyclerView.ViewHolder{

        FloatingActionButton DeleteButton;
        FloatingActionButton EditButton;
        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            DeleteButton = itemView.findViewById(R.id.Delet_Button);
            EditButton = itemView.findViewById(R.id.Edit_Button);
        }
    }



    public void showMenu(int position) {
        for(int i=0; i<PostsList.size(); i++){
            PostsList.get(i).setShowMenu(false);
        }
        PostsList.get(position).setShowMenu(true);
        notifyDataSetChanged();
    }


    public boolean isMenuShown() {
        for(int i=0; i<PostsList.size(); i++){
            if(PostsList.get(i).getShowMenu()==true){
                return true;
            }
        }
        return false;
    }

    public void closeMenu() {
        for(int i=0; i<PostsList.size(); i++){
            PostsList.get(i).setShowMenu(false);
        }
        notifyDataSetChanged();
    }

    public void removeAt(int position) {
        PostsList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, PostsList.size());
    }
}

