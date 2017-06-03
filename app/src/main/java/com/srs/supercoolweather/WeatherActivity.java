package com.srs.supercoolweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.srs.supercoolweather.gson.Forecast;
import com.srs.supercoolweather.gson.Weather;
import com.srs.supercoolweather.util.Httputil;
import com.srs.supercoolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity
        extends AppCompatActivity
{

    private ScrollView mWeatherLayout;
    private TextView mTitleCity;
    private TextView mDegreeText;
    private TextView mTitleUpdateTime;
    private TextView mWeatherInfoText;
    private LinearLayout mForecastLayout;
    private TextView mAqiText;
    private TextView mPm25Text;
    private TextView mComfortText;
    private TextView mCarWashText;
    private TextView mSportText;
    private ImageView mBg;
    private SwipeRefreshLayout mRefreshLayout;
    private String mWeatherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSystemWindows();
        setContentView(R.layout.activity_weather);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });
    }

    private void initSystemWindows() {
        if (Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void initData() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(
                this);
        String weatherString = preferences.getString("weather", null);
        if (weatherString != null){
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        }else{
            //无缓存时去服务器查询天气
            mWeatherId = getIntent().getStringExtra("weather_id");
            mWeatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        }

        String bingPic = preferences.getString("bing_pic",null);
        if (bingPic != null){
            Glide.with(this).load(bingPic).into(mBg);
        }else{
            loadBingPic();
        }

    }

    //下载背景图
    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        Httputil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response)
                    throws IOException
            {
                final String bingPic = response.body()
                                               .string();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(
                        WeatherActivity.this);
                SharedPreferences.Editor edit = preferences.edit();
                edit.putString("bing_pic",bingPic);
                edit.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(mBg);
                    }
                });
            }
        });
    }

    //初始化控件
    private void initView() {
        mWeatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        mTitleCity = (TextView) findViewById(R.id.title_city);
        mTitleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        mDegreeText = (TextView) findViewById(R.id.degree_text);
        mWeatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        mForecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        mAqiText = (TextView) findViewById(R.id.aqi_text);
        mPm25Text = (TextView) findViewById(R.id.pm25_text);
        mComfortText = (TextView) findViewById(R.id.comfor_text);
        mCarWashText = (TextView) findViewById(R.id.car_wash_text);
        mSportText = (TextView) findViewById(R.id.sport_text);
        mBg = (ImageView) findViewById(R.id.bing_pic_img);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
    }

    /*根据天气id请求城市天气信息*/
    private void requestWeather(String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=06b7cc9fdcf94a4ba626f4788901f824";
        Httputil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT)
                             .show();
                        mRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response)
                    throws IOException
            {
                final String responseText = response.body()
                                                    .string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
                                    WeatherActivity.this)
                                                                             .edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                            loadBingPic();
                        }else{
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT)
                                 .show();
                        }

                        mRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    /*处理并展示Weather实体类中的数据*/
    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime        = weather.basic.update.updateTime.split(" ")[1];
        String degree =  weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        mTitleCity.setText(cityName);
        mTitleUpdateTime.setText(updateTime);
        mDegreeText.setText(degree);
        mWeatherInfoText.setText(weatherInfo);
        mForecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList){
            View view = LayoutInflater.from(WeatherActivity.this)
                                         .inflate(R.layout.forecast_item, mForecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            dateText.setText(forecast.date);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            infoText.setText(forecast.more.info);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            maxText.setText(forecast.temperature.max);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            minText.setText(forecast.temperature.min);
            mForecastLayout.addView(view);
        }

        if (weather.aqi != null){
            mAqiText.setText(weather.aqi.city.aqi);
            mPm25Text.setText(weather.aqi.city.pm25);
        }

        String comfort = "舒适度：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        String sport = "运动建议：" + weather.suggestion.sport.info;
        mComfortText.setText(comfort);
        mCarWashText.setText(carWash);
        mSportText.setText(sport);
        mWeatherLayout.setVisibility(View.VISIBLE);
    }
}
