package com.Syrine.mnart.Controllers.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Syrine.mnart.Models.CommentUser;
import com.Syrine.mnart.Models.SubComment;
import com.Syrine.mnart.R;
import com.Syrine.mnart.Utils.Const;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SubCommentAdapter extends RecyclerView.Adapter<SubCommentAdapter.myViewHolder> {
    Context mContext;
    Activity activity;
    List<SubComment> SubCommentList;

    public SubCommentAdapter(Context context, Activity activity, List<SubComment> list){
        this.mContext = context;
        this.activity=activity;
        this.SubCommentList=list;
    }

    @NonNull
    @Override
    public SubCommentAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =LayoutInflater.from(mContext);
        View v=inflater.inflate(R.layout.subcomment_card,parent,false);
        return new myViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SubCommentAdapter.myViewHolder holder, int position) {

        String url = "http://localhost:3000/public/images/users/"+SubCommentList.get(position).getUser_img();
        Glide.with(mContext)
                .load(url)
                .into(holder.userImg);

        holder.subCommentUser_name.setText(SubCommentList.get(position).getUser_firstName()+" "+SubCommentList.get(position).getUser_lastName());
        holder.subComment_commentText.setText(SubCommentList.get(position).getComment());
        holder.subComment_date.setText(Const.generateElapsedTime(SubCommentList.get(position).getDate_comment()));
    }

    @Override
    public int getItemCount() {
        return SubCommentList.size();
    }



    public class myViewHolder extends  RecyclerView.ViewHolder{
        CircleImageView userImg;
        TextView subCommentUser_name,subComment_commentText,subComment_date;
        View view;

        public myViewHolder( View itemView) {
            super(itemView);
            view = itemView;
            userImg = itemView.findViewById(R.id.subCommentUser_img);
            subCommentUser_name= itemView.findViewById(R.id.subCommentUser_name);
            subComment_commentText= itemView.findViewById(R.id.subComment_commentText);
            subComment_date = itemView.findViewById(R.id.subComment_date);
        }
        public View getView()
        {
            return view;
        }
    }
}

