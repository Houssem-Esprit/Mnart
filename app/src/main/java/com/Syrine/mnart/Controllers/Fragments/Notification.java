package com.Syrine.mnart.Controllers.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.Syrine.mnart.Controllers.Adapters.NotificationAdapter;
import com.Syrine.mnart.Controllers.Interfaces.SocketCallbackInterface;
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
import io.socket.client.Socket;


public class Notification extends Fragment {
    private RecyclerView notificationsRecyclerView;
    NotificationAdapter notificationAdapter;
    private static final String TAG = "NOTIFICATIONS_FRAGMENT";
    CompositeDisposable disposable = new CompositeDisposable();
    LinearLayoutManager linearLayoutManager;
    private UserApi userApi;
    Socket mSocket;
    SocketCallbackInterface callback ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewroot =  inflater.inflate(R.layout.fragment_notification, container, false);
        // notification socket Check
        mSocket = Session.getInstance().getSocket();
        mSocket.emit("join", Session.getInstance().getUser().getIdUser());
        ((SocketCallbackInterface)getActivity()).onNotifSocketEmitedToServer();
        // UI
        notificationsRecyclerView = viewroot.findViewById(R.id.notif_recyclerView);
        linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        notificationsRecyclerView.setLayoutManager(linearLayoutManager);
        userApi = UtilApi.getUserApi();



        // Network Call
        userApi.getNotifications()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<com.Syrine.mnart.Models.Notification>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.d(TAG,"onSubscribe: created");
                        disposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull List<com.Syrine.mnart.Models.Notification> cat) {
                        Log.d(TAG,"onNext: ");

                        notificationAdapter = new NotificationAdapter(getContext(),getActivity(),cat);
                        notificationsRecyclerView.setAdapter(notificationAdapter);

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