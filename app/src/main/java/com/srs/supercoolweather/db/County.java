package com.srs.supercoolweather.db;

import org.litepal.crud.DataSupport;

/*
 *  @项目名：  SuperCoolWeather 
 *  @包名：    com.srs.supercoolweather.db
 *  @文件名:   County
 *  @创建者:   srs0116
 *  @创建时间:  2017/6/2 14:17
 *  @描述：    区县
 */
public class County extends DataSupport {
    private static final String TAG = "County";
    private int id;
    private String countyName;
    private String weatherId;
    private int cityId;

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getId() {
        return id;
    }

    public String getCountyName() {
        return countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public int getCityId() {
        return cityId;
    }
}
