package com.Syrine.mnart.Controllers.Fragments;

import android.Manifest;
import android.app.Activity;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.Syrine.mnart.BuildConfig;
import com.Syrine.mnart.Controllers.Adapters.CategoryAdapter;
import com.Syrine.mnart.Controllers.Interfaces.SocketCallbackInterface;
import com.Syrine.mnart.Models.Category;
import com.Syrine.mnart.R;
import com.Syrine.mnart.Utils.DataManager.UserApi;
import com.Syrine.mnart.Utils.DataManager.UtilApi;
import com.Syrine.mnart.Utils.Session;
import com.bumptech.glide.Glide;
import com.ramotion.circlemenu.CircleMenuView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.onimur.handlepathoz.HandlePathOz;
import br.com.onimur.handlepathoz.HandlePathOzListener;
import br.com.onimur.handlepathoz.model.PathOz;
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

import static android.provider.MediaStore.MediaColumns.DATA;

public class AddPage extends Fragment implements AdapterView.OnItemSelectedListener, HandlePathOzListener.SingleUri {


    private static final int GALLERY_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final String TAG = "ADDPOST_FRAGMENT";
    private String cameraFilePath;
    private Spinner categorySpinner;
    private UserApi userApi;
    List<Category> listcat;
    ImageView imgPost;
    EditText titlePost;
    EditText pricePost;
    EditText descPost;
    Button addPost;
    LinearLayout containerpost;
    Bitmap mBitmap;
    TextView errorNotice;
    private HandlePathOz handlePathOz;
    private String selectedImagePath ;
    Socket mSocket;

    int catID;
    CompositeDisposable disposable = new CompositeDisposable();
    CompositeDisposable Categorydisposable = new CompositeDisposable();




    public AddPage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewroot = inflater.inflate(R.layout.fragment_add_page, container, false);
        mSocket = Session.getInstance().getSocket();
        mSocket.emit("join", Session.getInstance().getUser().getIdUser());
        ((SocketCallbackInterface)getActivity()).onNotifSocketEmitedToServer();
        // UI
        categorySpinner = viewroot.findViewById(R.id.categorySpinner);
        categorySpinner.setOnItemSelectedListener(this);
        imgPost=viewroot.findViewById(R.id.imgPost);
        titlePost= viewroot.findViewById(R.id.titlePost);
        pricePost = viewroot.findViewById(R.id.pricePost);
        descPost= viewroot.findViewById(R.id.descPost);
        addPost = viewroot.findViewById(R.id.createPost);
        containerpost =viewroot.findViewById(R.id.postContainer);
        errorNotice = viewroot.findViewById(R.id.postErrorNotice);

        //vars
        userApi = UtilApi.getUserApi();
        handlePathOz = new HandlePathOz(getContext(),this);

        // get categories from database
        getCategories();

        final CircleMenuView circleMenuView = (CircleMenuView) viewroot.findViewById(R.id.circle_menu);
        circleMenuView.setEventListener(new CircleMenuView.EventListener(){
            @Override
            public void onMenuOpenAnimationStart(@NonNull CircleMenuView view) {
                Log.d("D", "onMenuOpenAnimationStart");
            }

            @Override
            public void onMenuOpenAnimationEnd(@NonNull CircleMenuView view) {
                Log.d("D", "onMenuOpenAnimationEnd");
            }

            @Override
            public void onMenuCloseAnimationStart(@NonNull CircleMenuView view) {
                Log.d("D", "onMenuCloseAnimationStart");
            }

            @Override
            public void onMenuCloseAnimationEnd(@NonNull CircleMenuView view) {
                Log.d("D", "onMenuCloseAnimationEnd");
            }

            @Override
            public void onButtonClickAnimationStart(@NonNull CircleMenuView view, int index) {
                Log.d("D", "onButtonClickAnimationStart| index: " + index);
            }

            @Override
            public void onButtonClickAnimationEnd(@NonNull CircleMenuView view, int index) {
                Log.d("D", "onButtonClickAnimationEnd| index: " + index);
                if (index==0){
                    checkPermission(0, Manifest.permission.WRITE_EXTERNAL_STORAGE,STORAGE_PERMISSION_CODE);
                    containerpost.setVisibility(View.VISIBLE);
                    circleMenuView.setVisibility(View.GONE);
                    getCategories();
                }else if(index==1){
                    checkPermission(1, Manifest.permission.CAMERA,CAMERA_PERMISSION_CODE);
                    containerpost.setVisibility(View.VISIBLE);
                    circleMenuView.setVisibility(View.GONE);
                    getCategories();
                }

            }
        });


        // submit post


        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((titlePost.getText().length()!=0) & (pricePost.getText().length()!=0) & (descPost.getText().length() != 0)){
                    SubmitPost();

                }else {
                    errorNotice.setText("All fields should not be empty !");
                }
            }
        });



        return  viewroot;
    }






    private void pickFromGallery() {
        //Create an Intent with action as ACTION_PICK
        Intent intent = new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        // Launching the Intent
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result code is RESULT_OK only if the user selects an Image

        Uri selectedImage;
        String[] filePathColumn = {DATA};

        Cursor cursor;
        int columnIndex;
        String imgDecodableString;
        File imgFile;

        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    //data.getData return the content URI for the selected Image
                    selectedImage = data.getData();
                    if(selectedImage != null){
                        handlePathOz.getRealPath(selectedImage);
                        Glide.with(getContext())
                                .load(selectedImage)
                                .into(imgPost);
                    }
                    break;

                case CAMERA_REQUEST_CODE:
                    imgPost.setImageURI(Uri.parse(cameraFilePath));
                    break;
            }


    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //This is the directory in which the file will be created. This is the default location of Camera photos
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for using again
        cameraFilePath = "file://" + image.getAbsolutePath();
        return image;
    }


    private void captureFromCamera() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", createImageFile()));
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }




    public void checkPermission(int TAG ,String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(getContext(), permission)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[] { permission },
                    requestCode);
        }
        else {
            Toast.makeText(getContext(),
                    "Permission already granted",
                    Toast.LENGTH_SHORT)
                    .show();
            if (TAG == 0){
                pickFromGallery();
            }else if(TAG==1){
                captureFromCamera();
            }


        }
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

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(),
                        "Camera Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                Toast.makeText(getContext(),
                        "Camera Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
        else if (requestCode == STORAGE_PERMISSION_CODE) {
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


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        catID = listcat.get(position).getIdCat();
        Log.d(TAG,"selected category: "+listcat.get(position).getIdCat());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
                        Categorydisposable.add(d);
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<Category> cat) {
                        Log.d(TAG,"onNext: ");
                        Log.d(TAG,"categories from DB : "+cat.toString());
                        listcat = cat;
                        List<String> categories = new ArrayList<>();
                        for(int i=0;i<cat.size();i++){
                            Log.d(TAG,"categories from DB : "+cat.get(i).getCatName().toString());
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
                        Categorydisposable.clear();


                    }
                });
    }


    public void SubmitPost(){

        if (mBitmap != null){

            File filesDir = getContext().getFilesDir();

            File file = new File(filesDir, "image" + ".png");

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
            byte[] bitmapdata = bos.toByteArray();

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e){ e.printStackTrace(); }

            String title = titlePost.getText().toString();
            String desc = descPost.getText().toString();
            int price = Integer.parseInt(pricePost.getText().toString());

            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), reqFile);
            RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "image");
            RequestBody Title = RequestBody.create(MediaType.parse("multipart/form-data"),title);
            RequestBody Desc = RequestBody.create(MediaType.parse("multipart/form-data"),desc);
            RequestBody Price = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(price));
            RequestBody CategoryID = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(catID));
            RequestBody UserId = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(Session.getInstance().getUser().getIdUser()));

            Log.d(TAG,"User Session ID : "+Session.getInstance().getUser().getIdUser());


            userApi.addPost(UserId,CategoryID,Title,Desc,Price,body,name)
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


                        }
                    });
        }else {
            Toast.makeText(getContext(), "Selected image is empty, Request failed!", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.clear();
        Categorydisposable.clear();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        disposable.clear();
        Categorydisposable.clear();
    }

    @Override
    public void onRequestHandlePathOz(@NotNull PathOz pathOz, @Nullable Throwable throwable) {
        selectedImagePath = pathOz.getPath();
        Log.d(TAG,"onRequestHandlePathOz SELECTEDIMAGE_PATH : "+selectedImagePath);
        if (selectedImagePath.length() != 0) {
            try {
                File file = new File(selectedImagePath);
                mBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            }catch (Exception e){e.printStackTrace();}

        }
    }
}