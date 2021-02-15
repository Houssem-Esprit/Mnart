package com.Syrine.mnart.Utils.DataManager;

public class UtilApi {
    private  static  final String BASE_URL="http://localhost:3000/";

    public static UserApi getUserApi(){
        return RetrofitClient.getRetrofitClient(BASE_URL).create(UserApi.class);
    }
}
