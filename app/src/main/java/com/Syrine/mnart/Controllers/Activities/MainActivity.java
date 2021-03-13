package com.Syrine.mnart.Controllers.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.Syrine.mnart.Controllers.Adapters.PostCatAdapter;
import com.Syrine.mnart.Controllers.Interfaces.SocketCallbackInterface;
import com.Syrine.mnart.R;
import com.Syrine.mnart.Utils.Session;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity implements SocketCallbackInterface
{
    BottomNavigationView bottomNavigationView;
    BottomAppBar bottomAppBar;
    FloatingActionButton addPost;
    NavController navController;
    AppBarLayout appBarLayout;
    Socket mSocket;
    TextView notifBadge;
    View actionView;
    Menu menu;
    MaterialToolbar materialToolbar;
    int data;
    PostCatAdapter postCatAdapter;
    MediaPlayer mediaPlayer;
    private static final String TAG = "MAIN_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Socket socket;
        super.onCreate(savedInstanceState);
        try {
            socket = IO.socket("http://localhost:3001/");
            Session.getInstance().setSocket(socket);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


        setContentView(R.layout.activity_main);
        postCatAdapter = new PostCatAdapter();
        // UI
        addPost=findViewById(R.id.addPost);
        appBarLayout = findViewById(R.id.appBar);
        bottomAppBar = findViewById(R.id.bottom_appBar);
        materialToolbar = findViewById(R.id.toolbar_id);
        setSupportActionBar(materialToolbar);
        //materialToolbar.showOverflowMenu();
        // Bottom navigation bar & Navigation component setup
        setupNavigation();

        // Media implementation
        mediaPlayer = MediaPlayer.create(MainActivity.this,R.raw.notif_sound);
        // Socket Initialization
        mSocket = Session.getInstance().getSocket();
        mSocket.connect();
        mSocket.emit("join", Session.getInstance().getUser().getIdUser());



        // Button add post impl
        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NavController navController = Navigation.findNavController(MainActivity.this,R.id.nav_host_fragment);
                navController.navigate(R.id.addPage);
            }
        });


        // test

   /*     postCatAdapter.setSocketCallbackInterface(new SocketCallbackInterface() {
            @Override
            public void onNotifSocketEmitedToServer() {

            }
        });  */
    }




    public void setupNavigation(){
        bottomNavigationView = findViewById(R.id.bottom_nav_id);
        bottomNavigationView.setBackground(null);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView,navHostFragment.getNavController());

        // on destination changed controls
        navController = navHostFragment.getNavController();

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if (destination.getId()==R.id.videofragment || destination.getId()==R.id.addPage || destination.getId()==R.id.video || destination.getId()==R.id.addCours ||destination.getId()==R.id.allPosts || destination.getId()==R.id.commentPage||destination.getId()==R.id.likesDialogFragment ||destination.getId()==R.id.profileGuestPage){

                    bottomAppBar.setVisibility(View.GONE);
                    bottomNavigationView.setVisibility(View.GONE);
                    addPost.setVisibility(View.GONE);
                    if (destination.getId()==R.id.videofragment){
                        appBarLayout.setVisibility(View.GONE);
                    }else if(destination.getId()==R.id.commentPage){
                        //appBarLayout.setAlpha(0.4F);
                    }
                }else {
                    bottomAppBar.setVisibility(View.VISIBLE);
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    addPost.setVisibility(View.VISIBLE);
                    appBarLayout.setVisibility(View.VISIBLE);
                }
            }
        });

    }

/*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    } */
    private void test(){

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.homepage_menu, menu);
        this.menu = menu;
        final MenuItem menuItem = menu.findItem(R.id.notificationS);
        actionView = menuItem.getActionView();
        notifBadge = actionView.findViewById(R.id.cart_badge);
        setUpBadge(notifBadge);
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menu){
        NavController navController = Navigation.findNavController(this,R.id.nav_host_fragment);
        switch (menu.getItemId()){
            case R.id.logout :
                startActivity(new Intent(MainActivity.this,SignIn.class));
                return true;
            case R.id.notificationS:
                navController.navigate(R.id.notificationS);

            default:
                return NavigationUI.onNavDestinationSelected(menu,navController) || super.onOptionsItemSelected(menu);


        }
    }




    private void setUpBadge(TextView notifBadge){
        if(notifBadge != null){
            if(data == 0){
                notifBadge.setVisibility(View.GONE);
            }else {
                notifBadge.setText(String.valueOf(Math.min(data,99)));
                mediaPlayer.start();
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"IM IN ONSTART LIFECYCLE STATE ");

        mSocket.on("getNotifications", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("MainActivity",": "+args[0].toString());
                        data = (int) args[0];

                    }
                });
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"IM IN ONSTOP LIFECYCLE STATE ");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"IM IN ONDESTROY LIFECYCLE STATE ");
        mSocket.disconnect();

    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"IM IN RESUME LIFECYCLE STATE ");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"IM IN ONPAUSE LIFECYCLE STATE ");

    }




    @Override
    public void onNotifSocketEmitedToServer() {
        Log.d(TAG," Notif callback Fired !!!! ");
        mSocket.on("getNotifications", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("MainActivity",": "+args[0].toString());
                        data = (int) args[0];
                        final MenuItem menuItem = menu.findItem(R.id.notificationS);
                        actionView = menuItem.getActionView();
                        notifBadge = actionView.findViewById(R.id.cart_badge);
                        if(notifBadge != null){
                            if(data == 0){
                                notifBadge.setVisibility(View.GONE);
                            }else {
                                notifBadge.setText(String.valueOf(Math.min(data,99)));
                                //mediaPlayer.start();
                            }
                        }
                    }
                });
            }
        });
    }
}