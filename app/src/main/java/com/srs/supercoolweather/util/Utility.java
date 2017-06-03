package com.srs.supercoolweather.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.srs.supercoolweather.db.City;
import com.srs.supercoolweather.db.County;
import com.srs.supercoolweather.db.Province;
import com.srs.supercoolweather.gson.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 *  @项目名：  SuperCoolWeather 
 *  @包名：    com.srs.supercoolweather.util
 *  @文件名:   Utility
 *  @创建者:   srs0116
 *  @创建时间:  2017/6/2 14:33
 *  @描述：    解析数据
 */
public class Utility {
    private static final String TAG = "Utility";

    /*解析和处理服务器返回的省级数据*/
    public static boolean handleProvinceResponse(String response){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i ++){
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province   province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /*解析和处理服务器返回的市级数据*/
    public static boolean handleCityResponse(String response,int provinceId){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i ++){
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City       city      = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /*解析和处理服务器返回的县级数据*/
    public static boolean handleCountyResponse(String response,int countyId){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i ++){
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County     county       = new County();
                    county.setCityId(countyObject.getInt("id"));
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(countyId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /*将返回的JSON数据解析成Weather实体类*/
    public static Weather handleWeatherResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray  jsonArray  = jsonObject.getJSONArray("HeWeather");
            String     weatherContent          = jsonArray.getJSONObject(0)
                                             .toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
