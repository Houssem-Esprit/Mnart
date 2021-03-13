package com.Syrine.mnart.Controllers.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.Syrine.mnart.Controllers.Adapters.CoursAdapter;
import com.Syrine.mnart.Controllers.Interfaces.SocketCallbackInterface;
import com.Syrine.mnart.Models.CoursPost;
import com.Syrine.mnart.R;
import com.Syrine.mnart.Utils.DataManager.UserApi;
import com.Syrine.mnart.Utils.DataManager.UtilApi;
import com.Syrine.mnart.Utils.Session;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.socket.client.Socket;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING;


public class Chat extends Fragment {
    private static final String TAG = "COURS_FRAGMENT";
    CompositeDisposable disposable = new CompositeDisposable();
    LinearLayoutManager linearLayoutManager;
    LinearLayout addCours;
    private UserApi userApi;
    RecyclerView CoursPostRecyclerView;
    CoursAdapter coursAdapter;
    PlayerView playerView;
        Socket mSocket;



    public Chat() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewroot = inflater.inflate(R.layout.fragment_chat, container, false);
        // provide CoursAdapter with this fragment Lifecycle
        mSocket = Session.getInstance().getSocket();
        mSocket.emit("join", Session.getInstance().getUser().getIdUser());
        ((SocketCallbackInterface)getActivity()).onNotifSocketEmitedToServer();
        //UI
        addCours= viewroot.findViewById(R.id.Go_To_AddCours);
        playerView = viewroot.findViewById(R.id.video_view);
        CoursPostRecyclerView = viewroot.findViewById(R.id.videoRecyclerView);
        linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        CoursPostRecyclerView.setLayoutManager(linearLayoutManager);
        userApi = UtilApi.getUserApi();

        userApi.getCoursPosts()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<CoursPost>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.d(TAG,"onSubscribe: created");
                        disposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull List<CoursPost> cat) {
                        Log.d(TAG,"onNext: ");
                        coursAdapter = new CoursAdapter(getContext(),getActivity(),cat);
                        getLifecycle().addObserver(coursAdapter);
                        CoursPostRecyclerView.setAdapter(coursAdapter);
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

      /*  CoursPostRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@androidx.annotation.NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int currentPosition = linearLayoutManager.findFirstVisibleItemPosition();
                if (newState == SCROLL_STATE_DRAGGING){
                    RecyclerView.ViewHolder holder = CoursPostRecyclerView.findViewHolderForLayoutPosition(currentPosition);
                    VideoView v =holder.itemView.findViewById(R.id.video_view);
                    if(v.isPlaying()){
                        v.stopPlayback();
                    }
                }
            }
        }); */

        addCours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NavController navController = Navigation.findNavController(getActivity(),R.id.nav_host_fragment);
                navController.navigate(R.id.action_video_to_addCours);
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