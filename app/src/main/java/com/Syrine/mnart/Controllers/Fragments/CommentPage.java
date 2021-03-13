package com.Syrine.mnart.Controllers.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.Syrine.mnart.Controllers.Adapters.CommentAdapter;
import com.Syrine.mnart.Controllers.Adapters.PostCatAdapter;
import com.Syrine.mnart.Models.CommentUser;
import com.Syrine.mnart.R;
import com.Syrine.mnart.Utils.DataManager.UserApi;
import com.Syrine.mnart.Utils.DataManager.UtilApi;
import com.Syrine.mnart.Utils.Session;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
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


public class CommentPage extends Fragment {

    private static final String TAG = CommentPage.class.getCanonicalName();
    CompositeDisposable add_comment_disposable = new CompositeDisposable();
    CompositeDisposable get_comment_disposable = new CompositeDisposable();
    private UserApi userApi;

    /**
     *
     */

    TextInputLayout comment_input_layout;
    TextInputEditText comment_input_text;
    RecyclerView recyclerViewComments;
    LinearLayoutManager linearLayoutManager;
    CommentAdapter commentAdapter;
    CircleImageView CreateComment_imgUser;
    ImageView background_img;
    TextView likesIndicator_Tv;


    public CommentPage() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewroot= inflater.inflate(R.layout.fragement_comment, container, false);

        /**
         * ! UI vars binding
         */

        comment_input_layout = viewroot.findViewById(R.id.commentPage_TextInputLayout);
        comment_input_text = viewroot.findViewById(R.id.CommentPage_textInput);
        recyclerViewComments = viewroot.findViewById(R.id.recyclerViewComments);
        CreateComment_imgUser = viewroot.findViewById(R.id.CreateComment_imgUser);
        background_img = viewroot.findViewById(R.id.commentView_background);
        likesIndicator_Tv = viewroot.findViewById(R.id.likesText);

        /**
         * * Utils vars definition
         */
        String idPost = getArguments().getString("idPost");
        int likesNumber = getArguments().getInt("likesNumber");
        getLifecycle().addObserver(new CommentAdapter());
        userApi = UtilApi.getUserApi();
        linearLayoutManager =new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        String url = "http://localhost:3000/public/images/users/"+Session.getInstance().getUser().getImage();

        /**
         * ? methods declarations & operation
         */

        recyclerViewComments.setLayoutManager(linearLayoutManager);

        comment_input_layout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComment(idPost,comment_input_text,comment_input_layout,recyclerViewComments);

            }
        });

        getComments(idPost,recyclerViewComments);

        // get user img to the view

        Glide.with(getContext())
                .load(url)
                .into(CreateComment_imgUser);

        likesIndicator_Tv.setText(likesNumber+" people has reacted");

        likesIndicator_Tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Bundle bundle= new Bundle();
                bundle.putString("idPost",idPost);
                bundle.putInt("likesNumber",likesNumber);
                final NavController navController = Navigation.findNavController(getActivity(),R.id.nav_host_fragment);
                navController.navigate(R.id.action_commentPage_to_likesDialogFragment,bundle);
            }
        });



        return viewroot;
    }


    public void addComment(String idPost,TextInputEditText Ti,TextInputLayout Tl, RecyclerView rv){
        int idUser = Session.getInstance().getUser().getIdUser();

        if (!TextUtils.isEmpty(Ti.getText())){

            userApi.addComment(idUser,idPost,Ti.getText().toString())
                    .toObservable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                            Log.d(TAG,"onSubscribe: created");
                            add_comment_disposable.add(d);
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
                            add_comment_disposable.clear();
                            Ti.getText().clear();
                            getComments(idPost,rv);
                        }
                    });

        }else {
            Tl.setError("comment content is empty!");
        }
    }

    public void getComments(String idPost,RecyclerView rv){

        userApi.getCommentsByPost(idPost)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<CommentUser>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        Log.d(TAG,"onSubscribe: created");
                        get_comment_disposable.add(d);
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<CommentUser> obj) {
                        Log.d(TAG,"onNext: "+obj);
                        for (int i=0;i<obj.size();i++){
                            Log.d("OBJECT Print Comment","COMMENTTTT :"+obj.get(i).getId_comment());
                        }
                        commentAdapter = new CommentAdapter(getContext(),getActivity(),obj);
                        rv.setAdapter(commentAdapter);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Log.d(TAG,"onError: getComments",e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG,"onComplete: created");
                        get_comment_disposable.clear();
                    }
                });
    }

}