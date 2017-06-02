package com.srs.supercoolweather.db;

import org.litepal.crud.DataSupport;

/*
 *  @项目名：  SuperCoolWeather 
 *  @包名：    com.srs.supercoolweather.db
 *  @文件名:   City
 *  @创建者:   srs0116
 *  @创建时间:  2017/6/2 14:13
 *  @描述：    市
 */
public class City extends DataSupport {
    private static final String TAG = "City";
    private int id;
    private String cityName;
    private int cityCode;
    private int provinceId;

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public int getCityCode() {
        return cityCode;
    }

    public int getId() {
        return id;
    }

    public String getCityName() {
        return cityName;
    }

    public int getProvinceId() {
        return provinceId;
    }

}
