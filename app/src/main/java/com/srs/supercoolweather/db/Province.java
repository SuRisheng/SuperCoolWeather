package com.srs.supercoolweather.db;

import org.litepal.crud.DataSupport;

/*
 *  @项目名：  SuperCoolWeather 
 *  @包名：    com.srs.supercoolweather.db
 *  @文件名:   Province
 *  @创建者:   srs0116
 *  @创建时间:  2017/6/2 14:10
 *  @描述：    省份
 */
public class Province extends DataSupport{
    private static final String TAG = "Province";
    private int id;     // id
    private String provinceName;    //省份名称
    private int provinceCode;   //省份编码

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public int getId() {
        return id;
    }

    public String getProvinceName() {
        return provinceName;
    }
}
