package com.srs.supercoolweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/*
 *  @项目名：  SuperCoolWeather 
 *  @包名：    com.srs.supercoolweather.util
 *  @文件名:   Httputil
 *  @创建者:   srs0116
 *  @创建时间:  2017/6/2 14:29
 *  @描述：    网络请求工具
 */
public class Httputil {
    private static final String TAG = "Httputil";

    public static void sendOkHttpRequest(String address, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request      request  = new Request.Builder().url(address)
                                                   .build();
        client.newCall(request).enqueue(callback);
    }
}
