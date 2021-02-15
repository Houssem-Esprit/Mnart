package com.Syrine.mnart.Controllers.Fragments;

import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.Syrine.mnart.Controllers.Adapters.MyworkAdapter;
import com.Syrine.mnart.Controllers.Adapters.PostCatAdapter;
import com.Syrine.mnart.Models.Post;
import com.Syrine.mnart.Models.PostByCategory;
import com.Syrine.mnart.R;
import com.Syrine.mnart.Utils.DataManager.UserApi;
import com.Syrine.mnart.Utils.DataManager.UtilApi;
import com.Syrine.mnart.Utils.Session;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class MyWorksPage extends Fragment {

    private RecyclerView myWorksRecyclerView;
    MyworkAdapter myworkAdapter;
    private static final String TAG = "MY-WORKS_FRAGMENT";
    CompositeDisposable disposable = new CompositeDisposable();
    LinearLayoutManager linearLayoutManager;
    private UserApi userApi;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewroot= inflater.inflate(R.layout.fragment_my_works_page, container, false);
        // UI
        myWorksRecyclerView = viewroot.findViewById(R.id.myWorks_RecyclerView);
        linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        myWorksRecyclerView.setLayoutManager(linearLayoutManager);
        userApi = UtilApi.getUserApi();



        // Network Call
        userApi.getPostsByUserId(Session.getInstance().getUser().getIdUser())
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Post>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.d(TAG,"onSubscribe: created");
                        disposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull List<Post> cat) {
                        Log.d(TAG,"onNext: ");

                        myworkAdapter = new MyworkAdapter(getContext(),getActivity(),cat);
                        myWorksRecyclerView.setAdapter(myworkAdapter);
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


        ItemTouchHelper.SimpleCallback touchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            private final ColorDrawable background = new ColorDrawable(getResources().getColor(R.color.M_colorPrimary));

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                myworkAdapter.showMenu(viewHolder.getAdapterPosition());
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                View itemView = viewHolder.itemView;

                if (dX > 0) {
                    background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + ((int) dX), itemView.getBottom());
                } else if (dX < 0) {
                    background.setBounds(itemView.getRight() + ((int) dX), itemView.getTop(), itemView.getRight(), itemView.getBottom());
                } else {
                    background.setBounds(0, 0, 0, 0);
                }

                background.draw(c);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchHelperCallback);
        itemTouchHelper.attachToRecyclerView(myWorksRecyclerView);


        myWorksRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                myworkAdapter.closeMenu();
            }
        });


        return viewroot;
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