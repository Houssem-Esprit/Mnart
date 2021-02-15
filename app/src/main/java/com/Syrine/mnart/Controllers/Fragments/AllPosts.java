package com.Syrine.mnart.Controllers.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.Syrine.mnart.Controllers.Adapters.CoursAdapter;
import com.Syrine.mnart.Controllers.Adapters.PostCatAdapter;
import com.Syrine.mnart.Controllers.Interfaces.SocketCallbackInterface;
import com.Syrine.mnart.Models.CoursPost;
import com.Syrine.mnart.Models.PostByCategory;
import com.Syrine.mnart.R;
import com.Syrine.mnart.Utils.DataManager.UserApi;
import com.Syrine.mnart.Utils.DataManager.UtilApi;
import com.Syrine.mnart.Utils.Session;
import com.ramotion.foldingcell.FoldingCell;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.socket.client.Socket;

public class AllPosts extends Fragment {
    FoldingCell foldingCell;
    RecyclerView allPosts_ReceyclerView;
    private static final String TAG = "ALL-POSTS_FRAGMENT";
    CompositeDisposable disposable = new CompositeDisposable();
    LinearLayoutManager linearLayoutManager;
    PostCatAdapter postCatAdapter;
    List<PostByCategory> myPostsList;
    private UserApi userApi;
    Socket mSocket;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewroot = inflater.inflate(R.layout.fragment_all_posts, container, false);
        // notification socket Check
        mSocket = Session.getInstance().getSocket();
        mSocket.emit("join", Session.getInstance().getUser().getIdUser());
        ((SocketCallbackInterface)getActivity()).onNotifSocketEmitedToServer();
        // UI
        allPosts_ReceyclerView = viewroot.findViewById(R.id.allPosts_RecyclerView);
        linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        allPosts_ReceyclerView.setLayoutManager(linearLayoutManager);
        userApi = UtilApi.getUserApi();



        int IdCat = getArguments().getInt("CatID");
        userApi.getPostsByCategory(IdCat)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<PostByCategory>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.d(TAG,"onSubscribe: created");
                        disposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull List<PostByCategory> cat) {
                        Log.d(TAG,"onNext: ");

                        postCatAdapter = new PostCatAdapter(getContext(),getActivity(),cat);
                        myPostsList = cat;
                        allPosts_ReceyclerView.setAdapter(postCatAdapter);
                        //editor.putInt("idUser",user.getIdUser());
                        //editor.apply();

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG,"onError: ",e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG,"onComplete: created");
                        disposable.clear();
                        try {
                        /*    if (getArguments().getString("idPost") != null){
                                String postId = getArguments().getString("idPost");
                                for(int i=0;i<myPostsList.size();i++){
                                    if (postId == myPostsList.get(i).getId()){
                                        allPosts_ReceyclerView.getLayoutManager().scrollToPosition(myPostsList.indexOf(myPostsList.get(i)));
                                        LinearLayout view = allPosts_ReceyclerView.findViewHolderForAdapterPosition(myPostsList.indexOf(myPostsList.get(i))).itemView.findViewById(R.id.cell_content_id);
                                        view.setVisibility(View.VISIBLE);
                                    }
                                }
                            } */
                        }catch (Exception e ){e.printStackTrace();}

                    }
                });

        return  viewroot;
    }






    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        disposable.clear();
    }




}