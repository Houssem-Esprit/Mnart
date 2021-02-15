package com.Syrine.mnart.Controllers.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.Syrine.mnart.Controllers.Activities.MainActivity;
import com.Syrine.mnart.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.Syrine.mnart.Models.Category;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Vector;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.myViewHolder> {
    Context mContext;
    Activity activity;
    List<Category> CategoryList;

    public CategoryAdapter(Context context,Activity activity, List<Category> list){
        this.mContext = context;
        this.activity=activity;
        this.CategoryList=list;
    }

    @NonNull
    @Override
    public CategoryAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =LayoutInflater.from(mContext);
        View v=inflater.inflate(R.layout.category_card,parent,false);
        return new myViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.myViewHolder holder, int position) {
        String url = "http://localhost:3000/public/images/categories/"+CategoryList.get(position).getCatImg();
        int IDCAT = CategoryList.get(position).getIdCat();
        Glide.with(mContext)
                .load(url)
                .into(holder.CategoryImg);
        holder.CategoryImg.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
        holder.CategoryImg.setImageTintList(ColorStateList.valueOf(Color.parseColor("#F27B63")));
        holder.CategoryName.setText(CategoryList.get(position).getCatName());


        holder.CategoryImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CCCCCCCCCCCCCCCCCCC","CCCCCCCCCCCCCCCCCC");
                holder.CategoryImg.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F27B63")));
                holder.CategoryImg.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                holder.CategoryName.setTextColor(Color.parseColor("#F27B63"));
                try {
                     Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final Bundle bundle= new Bundle();
                bundle.putInt("CatID",CategoryList.get(position).getIdCat());
                final NavController navController = Navigation.findNavController(activity,R.id.nav_host_fragment);
                navController.navigate(R.id.action_homePage_to_allPosts,bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return CategoryList.size();
    }



    public class myViewHolder extends  RecyclerView.ViewHolder{
        FloatingActionButton CategoryImg;
        TextView CategoryName;
        View view;

        public myViewHolder( View itemView) {
            super(itemView);
            view = itemView;
            CategoryImg = itemView.findViewById(R.id.CategoryImg);
            CategoryName= itemView.findViewById(R.id.CategoryName);
        }
        public View getView()
        {
            return view;
        }
    }
}

