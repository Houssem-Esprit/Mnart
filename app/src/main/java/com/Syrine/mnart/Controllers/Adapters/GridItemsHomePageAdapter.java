package com.Syrine.mnart.Controllers.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Syrine.mnart.Models.Category;
import com.Syrine.mnart.Models.Post;
import com.Syrine.mnart.R;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.util.List;

public class GridItemsHomePageAdapter extends RecyclerView.Adapter<GridItemsHomePageAdapter.myViewHolder> {
    Context mContext;
    List<Post> PostList;

    public GridItemsHomePageAdapter(Context context, List<Post> list){
        this.mContext = context;
        this.PostList=list;
    }

    @NonNull
    @Override
    public GridItemsHomePageAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =LayoutInflater.from(mContext);
        View v=inflater.inflate(R.layout.homepage_grid_item,parent,false);
        return new myViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GridItemsHomePageAdapter.myViewHolder holder, int position) {
        try{
            String url = "http://localhost:3000/public/images/posts/"+PostList.get(position).getImgPost();
            Glide.with(mContext)
                    .load(url)
                    .into(holder.PostImg);
            holder.PostTitle.setText(PostList.get(position).getTitle());
            holder.PostPrice.setText(PostList.get(position).getPrice()+"Dt");

        }catch (Exception e){e.printStackTrace();}

    }

    @Override
    public int getItemCount() {
        return PostList.size();
    }



    public class myViewHolder extends  RecyclerView.ViewHolder{
        TextView PostTitle;
        TextView PostPrice;
        ImageView PostImg;
        View view;

        public myViewHolder( View itemView) {
            super(itemView);
            view=itemView;
            PostTitle = itemView.findViewById(R.id.postTitle);
            PostPrice= itemView.findViewById(R.id.postPrice);
            PostImg= itemView.findViewById(R.id.postImg);
        }
        public View getView()
        {
            return view;
        }
    }
}

