package com.Syrine.mnart.Controllers.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.Syrine.mnart.Controllers.Activities.MainActivity;
import com.Syrine.mnart.Controllers.Activities.SignIn;
import com.Syrine.mnart.Controllers.Adapters.CategoryAdapter;
import com.Syrine.mnart.Controllers.Adapters.GridItemsHomePageAdapter;
import com.Syrine.mnart.Models.Category;
import com.Syrine.mnart.Models.Post;
import com.Syrine.mnart.Models.User;
import com.Syrine.mnart.R;
import com.Syrine.mnart.Utils.DataManager.UserApi;
import com.Syrine.mnart.Utils.DataManager.UtilApi;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class HomePage extends Fragment {

    private static final String TAG = "HOMEPAGE_FRAGMENT";
    private UserApi userApi;
    RecyclerView catrecyclerview;
    CategoryAdapter categoryAdapter;
    LinearLayoutManager linearLayoutManager;
    CompositeDisposable disposable = new CompositeDisposable();
    CompositeDisposable Catdisposable = new CompositeDisposable();

    RecyclerView PostRecyclerView;
    GridItemsHomePageAdapter homePageAdapter;



    public HomePage() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View viewroot= inflater.inflate(R.layout.fragment_home_page, container, false);

        //UI
        PostRecyclerView = viewroot.findViewById(R.id.RecyclerViewPost);

        userApi = UtilApi.getUserApi();
        catrecyclerview=viewroot.findViewById(R.id.catrecyclerView);
        linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        catrecyclerview.setLayoutManager(linearLayoutManager);

        userApi.get4Posts()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Post>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.d(TAG,"onSubscribe: created AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                        disposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull List<Post> cat) {
                        Log.d(TAG,"onNext: NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNnn");
                        Log.d(TAG,"BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB"+cat.get(1).getImgPost().toString());
                        homePageAdapter = new GridItemsHomePageAdapter(getContext(),cat);
                        RecyclerView.LayoutManager manager = new GridLayoutManager(getContext(),2);
                        PostRecyclerView.setLayoutManager(manager);
                        PostRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),LinearLayoutManager.VERTICAL));
                        PostRecyclerView.setAdapter(homePageAdapter);
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
                    }
                });



     userApi.getcategories()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Category>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.d(TAG,"onSubscribe: created");
                        Catdisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull List<Category> cat) {
                        Log.d(TAG,"onNext: ");
                        categoryAdapter = new CategoryAdapter(getContext(),getActivity(),cat);
                        catrecyclerview.setAdapter(categoryAdapter);
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
                        Catdisposable.clear();
                    }
                });


        return viewroot;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.clear();
        Catdisposable.clear();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        disposable.clear();
        Catdisposable.clear();
    }
}