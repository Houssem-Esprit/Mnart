package com.Syrine.mnart.Controllers.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.Syrine.mnart.Controllers.Interfaces.SocketCallbackInterface;
import com.Syrine.mnart.R;
import com.Syrine.mnart.Utils.Session;
import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.Socket;

public class ProfilePage extends Fragment {

    private CircleImageView userImg;
    private TextView userName;
    private ScrollView scrollView;
    private Button GoToMyworkButton;
    Socket mSocket;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewroot = inflater.inflate(R.layout.fragment_profile_page, container, false);
        mSocket = Session.getInstance().getSocket();
        mSocket.emit("join", Session.getInstance().getUser().getIdUser());
        ((SocketCallbackInterface)getActivity()).onNotifSocketEmitedToServer();
        //UI
        userImg = viewroot.findViewById(R.id.profile_userImg);
        userName = viewroot.findViewById(R.id.profile_userName);
        scrollView = viewroot.findViewById(R.id.profile_scrollView);
        GoToMyworkButton = viewroot.findViewById(R.id.profile_myWorks_details_button);

        // Implementation
        String url = "http://localhost:3000/public/images/users/"+ Session.getInstance().getUser().getImage();
        Glide.with(getContext())
                .load(url)
                .into(userImg);

        userName.setText(Session.getInstance().getUser().getFirstName()+" "+Session.getInstance().getUser().getLastName());
            // scroll view
        GoToMyworkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NavController navController = Navigation.findNavController(getActivity(),R.id.nav_host_fragment);
                navController.navigate(R.id.action_profilePage_to_myWorksPage);
            }
        });
        return viewroot;
    }
}