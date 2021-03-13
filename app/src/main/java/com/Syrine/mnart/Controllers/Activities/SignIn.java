package com.Syrine.mnart.Controllers.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.Syrine.mnart.Models.User;
import com.Syrine.mnart.Models.UserResponse;
import com.Syrine.mnart.R;
import com.Syrine.mnart.Utils.DataManager.UserApi;
import com.Syrine.mnart.Utils.DataManager.UtilApi;
import com.Syrine.mnart.Utils.Session;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SignIn extends AppCompatActivity {
    // define vars
    private static final String TAG = "SIGN-IN_ACTIVITY";
    private static  final String SHARED_FILE_NAME="userPreferences";  // key of the sharedPreferences data store
    SharedPreferences sharedData;
    private  UserApi userApi;
    TextInputEditText email_input;
    TextInputEditText password_input;
    TextInputLayout email_layout;
    TextInputLayout pass_layout;
    Button loginButton;
    User currentUser = new User();
    ProgressBar progressBar;
    // the operator that cares about disposing the Observers when they done work| this compositeDisposable should omitted in onDistroy method of the Activity or Fragment
    CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Initialize vars
        userApi = UtilApi.getUserApi();
        //SharedPreferences.Editor editor = sharedData.edit();
        //sharedData = getSharedPreferences(SHARED_FILE_NAME,MODE_PRIVATE);

        // Initialize the UI
        email_layout = findViewById(R.id.id_email_layout);
        pass_layout = findViewById(R.id.id_pass_layout);
        email_input = findViewById(R.id.email);
        password_input = findViewById(R.id.password);
        loginButton = findViewById(R.id.LoginButton);
        loginButton.setBackgroundColor(Color.parseColor("#B0B0B0"));

           email_input.addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence s, int start, int count, int after) {

               }

               @Override
               public void onTextChanged(CharSequence s, int start, int before, int count) {
                   if (s.toString().length() == 0){
                       email_layout.setError(getString(R.string.empty_email));
                   }else if(s.toString().length() != 0 & password_input.getText().length()!= 0){
                       email_layout.setError(null);
                       loginButton.setEnabled(true);
                       loginButton.setClickable(true);
                       loginButton.setFocusable(true);
                       loginButton.setBackgroundColor(getResources().getColor(R.color.M_colorWhite));
                   }
                   else if(s.toString().length() != 0){
                       pass_layout.setError(null);
                   }
               }

               @Override
               public void afterTextChanged(Editable s) {

               }
           });

           password_input.addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence s, int start, int count, int after) {

               }

               @Override
               public void onTextChanged(CharSequence s, int start, int before, int count) {
                   if (s.toString().length() == 0){
                       pass_layout.setError(getString(R.string.empty_password));
                   }else if(s.toString().length() != 0 & email_input.getText().length()!= 0){
                       pass_layout.setError(null);
                       loginButton.setEnabled(true);
                       loginButton.setClickable(true);
                       loginButton.setFocusable(true);
                       loginButton.setBackgroundColor(getResources().getColor(R.color.M_colorWhite));
                   }
                   else if(s.toString().length() != 0){
                       pass_layout.setError(null);
                   }
               }

               @Override
               public void afterTextChanged(Editable s) {

               }
           });



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((email_layout.getError()==null) & (pass_layout.getError()==(null))){


                    // implements login network operation with Observable RxJava3
                    userApi.Authenticate(email_input.getText().toString(),password_input.getText().toString())
                            .toObservable()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<UserResponse>() {
                                @Override
                                public void onSubscribe(@NonNull Disposable d) {
                                    Log.d(TAG,"onSubscribe: created");
                                    disposable.add(d);
                                }

                                @Override
                                public void onNext(@NonNull UserResponse user) {
                                    Log.d(TAG,"onNext: ");
                                    initSession(user.getResult());
                                    Snackbar.make(v,"Authenticated Successfully!",Snackbar.LENGTH_SHORT).show();

                                    //editor.putInt("idUser",user.getIdUser());
                                    //editor.apply();
                                    startActivity(new Intent(SignIn.this,MainActivity.class));
                                    finish();
                                }

                                @Override
                                public void onError(@NonNull Throwable e) {
                                    Log.d(TAG,"onError: ",e);
                                }

                                @Override
                                public void onComplete() {
                                    Log.d(TAG,"onComplete: created");
                                }
                            });
                }

            }
        });


        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Throwable {
                    emitter.onNext("Test");
                    emitter.onComplete();
            }
        });

      Observable.just("Hello word")
              .subscribe(s -> System.out.println(s));


    }





    public void initSession(User userSerialized)
    {
        try {
            // fill the current user object with the one coming from network request (userSerialized)
            currentUser.setIdUser(userSerialized.getIdUser());
            Log.d("AAAAAAAAAAAAAAAAAAA","UUUUUUU USER id :"+currentUser.getIdUser());
            currentUser.setFirstName(userSerialized.getFirstName());
            currentUser.setLastName(userSerialized.getLastName());
            currentUser.setEmail(userSerialized.getEmail());
            currentUser.setPaswword(userSerialized.getPaswword());
            currentUser.setImage(userSerialized.getImage());

            Session.getInstance().setUser(currentUser); // store the current user in the session

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }



}