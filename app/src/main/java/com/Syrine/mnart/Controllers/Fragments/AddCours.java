package com.Syrine.mnart.Controllers.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.Syrine.mnart.Controllers.Interfaces.SocketCallbackInterface;
import com.Syrine.mnart.Models.Category;
import com.Syrine.mnart.R;
import com.Syrine.mnart.Utils.DataManager.UserApi;
import com.Syrine.mnart.Utils.DataManager.UtilApi;
import com.Syrine.mnart.Utils.Session;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.onimur.handlepathoz.HandlePathOz;
import br.com.onimur.handlepathoz.HandlePathOzListener;
import br.com.onimur.handlepathoz.model.PathOz;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.socket.client.Socket;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import static android.app.Activity.RESULT_OK;

public class AddCours extends Fragment implements AdapterView.OnItemSelectedListener, HandlePathOzListener.SingleUri {
    private static final String TAG = "ADDCOURS_FRAGMENT";
    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final int GALLERY_REQUEST_CODE = 1;

    CircleImageView userImg;
    TextView userName;
    EditText title;
    EditText desc;
    private Spinner categorySpinner;
    private UserApi userApi;
    List<Category> listcat;
    TextView errorNotice;
    int catID;
    CompositeDisposable disposable = new CompositeDisposable();
    CompositeDisposable Catdisposable = new CompositeDisposable();

    LinearLayout VideoSucces;
    VideoView videoView;
    Button publish;
    Button getVideoFromGallery;
    File file;
    LinearLayout error_background;
    private HandlePathOz handlePathOz;
    String selectedImagePath ;
    Socket mSocket;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View virwroot= inflater.inflate(R.layout.fragment_add_cours, container, false);
        mSocket = Session.getInstance().getSocket();
        mSocket.emit("join", Session.getInstance().getUser().getIdUser());
        ((SocketCallbackInterface)getActivity()).onNotifSocketEmitedToServer();

        userApi = UtilApi.getUserApi();
        handlePathOz = new HandlePathOz(getContext(),this);
        //UI
        userImg= virwroot.findViewById(R.id.Addcours_userImg);
        userName=virwroot.findViewById(R.id.Addcours_username);
        title=virwroot.findViewById(R.id.Addcours_title);
        desc=virwroot.findViewById(R.id.Addcours_desc);
        errorNotice=virwroot.findViewById(R.id.coursErrorNotice);
        VideoSucces=virwroot.findViewById(R.id.AddCours_videoPopUp);
        videoView=virwroot.findViewById(R.id.CoursVideo);
        error_background= virwroot.findViewById(R.id.error_background);
        categorySpinner = virwroot.findViewById(R.id.categorySpinnerCours);
        categorySpinner.setOnItemSelectedListener(this);
        publish = virwroot.findViewById(R.id.publishCours);
        getVideoFromGallery = virwroot.findViewById(R.id.getVideoFromGallery);

        getCategories();
        String url = "http://localhost:3000/public/images/users/"+ Session.getInstance().getUser().getImage();
        Glide.with(getContext())
                .load(url)
                .into(userImg);
        userName.setText(Session.getInstance().getUser().getFirstName()+" "+Session.getInstance().getUser().getLastName());

        // get Video From Gallery Event
        getVideoFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,STORAGE_PERMISSION_CODE)){
                    checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,STORAGE_PERMISSION_CODE);
                    if(title.getText().length() !=0 & desc.getText().length()!=0 & catID!=0){
                        publish.setBackgroundColor(Color.parseColor("#F27B63"));
                    }else {
                        errorNotice.setText("All fields should not be empty");
                    }
                }else {
                    checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,STORAGE_PERMISSION_CODE);
                    if(title.getText().length() !=0 & desc.getText().length()!=0 & catID!=0){
                        publish.setBackgroundColor(Color.parseColor("#F27B63"));
                    }else {
                        errorNotice.setText("All fields should not be empty");
                    }
                }
            }
        });

        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(title.getText().length() !=0 & desc.getText().length()!=0 & catID!=0){
                    //publish.setBackgroundColor(Color.parseColor("#F27B63"));
                        Log.d(TAG,"FFFFFFFFFFFFFFFFFFFFFFF");
                        PublishCours();
                }else {
                    errorNotice.setText("All fields should not be empty");
                }
            }
        });



        return  virwroot;

    }








    private void pickFromGallery() {

        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Video"),GALLERY_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String filemanagerstring;
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST_CODE) {
                Uri selectedImageUri = data.getData();
                // OI FILE Manager
                filemanagerstring = selectedImageUri.getPath();

                // MEDIA GALLERY


                if(selectedImageUri !=null){
                     handlePathOz.getRealPath(selectedImageUri);
                    try {
                        videoView.setVideoURI(selectedImageUri);
                        Log.d(TAG,"HHHHHHHHHHHHHHHHHHHHHHHHHHH");
                        if (videoView !=null){
                            Log.d(TAG,"MMMMMMMMMMMMMMMMMMMMMMM");
                            VideoSucces.setVisibility(View.VISIBLE);
                        }
                        videoView.start();

                    }catch (Exception e){e.printStackTrace();}

                }

            }
        }
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

    public  void getCategories(){
        userApi.getcategories()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Category>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        Log.d(TAG,"onSubscribe: created");
                        Catdisposable.add(d);
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<Category> cat) {
                        Log.d(TAG,"onNext: ");
                        Log.d(TAG,"categories from DB : "+cat.toString());
                        listcat = cat;
                        List<String> categories = new ArrayList<>();
                        for(int i=0;i<cat.size();i++){
                            Log.d(TAG,"categories from DB : "+cat.get(i).getIdCat()+" "+cat.get(i).getCatName().toString());
                            //categories = new ArrayList<>() ;
                            categories.add(cat.get(i).getCatName() );
                        }

                        ArrayAdapter<List<String>> array = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,categories);
                        array.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                        categorySpinner.setAdapter(array);
                        //editor.putInt("idUser",user.getIdUser());
                        //editor.apply();
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Log.d(TAG,"onError: ",e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG,"onComplete: created");
                        Catdisposable.clear();

                    }
                });

    }


    public Boolean checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(getContext(), permission)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[] { permission },
                    requestCode);
            Log.d(TAG,"NNNNNNNNNNNNNNNNNNNN");
            return false;

        }
        else if (ContextCompat.checkSelfPermission(getContext(), permission)
                == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getContext(),
                    "Permission already granted",
                    Toast.LENGTH_SHORT)
                    .show();
            Log.d(TAG,"WWWWWWWWWWWWWWWWWWWWW");
            pickFromGallery();
            return true;



        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super
                .onRequestPermissionsResult(requestCode,
                        permissions,
                        grantResults);


         if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(),
                        "Storage Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                Toast.makeText(getContext(),
                        "Storage Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }




    public void PublishCours(){


         /*   FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e){ e.printStackTrace(); } */
            Log.d(TAG,"PublishCours/CatID :"+catID);
            String titles = title.getText().toString();
            String descs = desc.getText().toString();
            //int price = Integer.parseInt(pricePost.getText().toString());
            MediaType MEDIA_TYPE = MediaType.parse("video/mp4");
            RequestBody reqFile = RequestBody.create(MEDIA_TYPE, file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("video", file.getName(), reqFile);
            RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "video");
            RequestBody Title = RequestBody.create(MediaType.parse("multipart/form-data"),titles);
            RequestBody Desc = RequestBody.create(MediaType.parse("multipart/form-data"),descs);
            RequestBody views = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(0));
            RequestBody CategoryID = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(catID));
            RequestBody UserId = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(Session.getInstance().getUser().getIdUser()));

            Log.d(TAG,"User Session ID : "+Session.getInstance().getUser().getIdUser());


            userApi.publishCours(UserId,CategoryID,Title,Desc,views,body,name)
                    .toObservable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                            Log.d(TAG,"onSubscribe: created");
                            disposable.add(d);
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
                            disposable.clear();
                            Snackbar.make(getView(),"Lesson published Successfully!",Snackbar.LENGTH_SHORT).show();
                            try {
                                Thread.sleep(500);
                                final NavController navController = Navigation.findNavController(getActivity(),R.id.nav_host_fragment);
                                navController.navigate(R.id.action_addCours_to_video);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    });


    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        catID = listcat.get(position).getIdCat();
        Log.d(TAG,"selected category: "+listcat.get(position).getIdCat());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onRequestHandlePathOz(@NotNull PathOz pathOz, @Nullable Throwable throwable) {
        //Now you can work with real path:
        selectedImagePath = pathOz.getPath();
        Log.d(TAG,"onRequestHandlePathOz SELECTEDIMAGE_PATH : "+selectedImagePath);
        if (selectedImagePath.length() != 0) {
            Log.d(TAG,"tTTTTTTTTTTTTTTTTTTTTTTTTTTTTT");
            file = new File(selectedImagePath);
        }
        Toast.makeText(getContext(), "The real path is: " + pathOz.getPath() + "\n The type is: "+pathOz.getType() , Toast.LENGTH_SHORT).show();
        if (throwable != null) {
            error_background.setBackground(getResources().getDrawable(R.drawable.error_background));
            errorNotice.setText("Problem occured while getting the Video, please try later");

        }
    }
}

