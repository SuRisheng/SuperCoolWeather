package com.srs.supercoolweather.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.srs.supercoolweather.MainActivity;
import com.srs.supercoolweather.R;
import com.srs.supercoolweather.WeatherActivity;
import com.srs.supercoolweather.db.City;
import com.srs.supercoolweather.db.County;
import com.srs.supercoolweather.db.Province;
import com.srs.supercoolweather.util.Httputil;
import com.srs.supercoolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/*
 *  @项目名：  SuperCoolWeather 
 *  @包名：    com.srs.supercoolweather.fragment
 *  @文件名:   ChooseAreaFragment
 *  @创建者:   srs0116
 *  @创建时间:  2017/6/2 15:07
 *  @描述：    选择城市的碎片
 */
public class ChooseAreaFragment extends Fragment{
    private static final String TAG = "ChooseAreaFragment";
    private Button mBack;
    private TextView mTitle;
    private ListView mList;
    private List<String> mDatalist = new ArrayList<>();
    private ArrayAdapter<String> mAdapter;

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
   /*省列表*/
    private List<Province> mProvinceList;
    /*市列表*/
    private List<City> mCityList;
    /*县列表*/
    private List<County> mCountyList;
    /*选中的 省份*/
    private Province mSelectedProvince;
    /*选中的城市*/
    private City mSelectedCity;

    /*当前选中的级别*/
    private int mCurrentLevel;
    private ProgressDialog mProgressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = LayoutInflater.from(getActivity())
                                     .inflate(R.layout.choose_area, container, false);
        mBack = (Button) view.findViewById(R.id.back_button);
        mTitle = (TextView) view.findViewById(R.id.title_text);
        mList = (ListView) view.findViewById(R.id.list_view);
        mAdapter = new ArrayAdapter<>(getActivity(),
                                      android.R.layout.simple_list_item_1,
                                      mDatalist);
        mList.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCurrentLevel == LEVEL_PROVINCE){
                    mSelectedProvince = mProvinceList.get(position);
                    queryCities();
                }else if (mCurrentLevel == LEVEL_CITY){
                    mSelectedCity = mCityList.get(position);
                    queryCounties();
                }else if (mCurrentLevel == LEVEL_COUNTY){
                    String weatherId = mCountyList.get(position)
                                                  .getWeatherId();
                    if (getActivity() instanceof MainActivity){

                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id",weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    }else if (getActivity() instanceof WeatherActivity){
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.mDrawerLayout.closeDrawers();
                        activity.mRefreshLayout.setRefreshing(true);
                        activity.requestWeather(weatherId);
                    }

                }
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentLevel == LEVEL_COUNTY){
                    queryCities();
                }else if (mCurrentLevel == LEVEL_CITY){
                    queryProvinces();
                }
            }
        });

        queryProvinces();
    }


    /*查询所有省份*/
    public void queryProvinces(){
        mTitle.setText("中国");
        mBack.setVisibility(View.GONE);
        mProvinceList = DataSupport.findAll(Province.class);
        if (mProvinceList.size() > 0){
            mDatalist.clear();
            for (int i = 0; i < mProvinceList.size(); i ++){
                mDatalist.add(mProvinceList.get(i).getProvinceName());
            }
            mAdapter.notifyDataSetChanged();
            mList.setSelection(0);
            mCurrentLevel = LEVEL_PROVINCE;
            setCurrentLevel();
        }else{
            String address = "http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }

    /*根据传入的地址和类型从服务器上查询省市县数据*/
    private void queryFromServer(String address, final String type){
        showProgressDiaog();
        Httputil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getActivity(), "加载失败", Toast.LENGTH_SHORT)
                             .show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response)
                    throws IOException
            {
                String responseText = response.body()
                                        .string();
                boolean result = false;
                if ("province".equals(type)){
                    result = Utility.handleProvinceResponse(responseText);
                }else if ("city".equals(type)){
                    result = Utility.handleCityResponse(responseText,mSelectedProvince.getId());
                }else if ("county".equals(type)){
                    result = Utility.handleCountyResponse(responseText,mSelectedCity.getId());
                }
                 if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProvinces();
                            }else if ("city".equals(type)){
                                queryCities();
                            }else if ("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                 }
            }
        });
    }

    /*查询区*/
    public void queryCounties() {
        mTitle.setText(mSelectedCity.getCityName());
        mBack.setVisibility(View.VISIBLE);
        mCountyList = DataSupport.where("cityId = ?",String.valueOf(mSelectedCity.getId())).find(County.class);
        if (mCountyList.size() > 0){
            mDatalist.clear();
            for (int i = 0; i < mCountyList.size(); i ++){
                mDatalist.add(mCountyList.get(i).getCountyName());
            }
            mAdapter.notifyDataSetChanged();
            mList.setSelection(0);
            mCurrentLevel = LEVEL_COUNTY;
            setCurrentLevel();
        }else{
            int provinceCode = mSelectedProvince.getProvinceCode();
            int cityCode = mSelectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" +cityCode;
            queryFromServer(address,"county");
        }
    }

    /*查询市*/
    public void queryCities() {
        mTitle.setText(mSelectedProvince.getProvinceName());
        mBack.setVisibility(View.VISIBLE);
        mCityList = DataSupport.where("provinceId = ? ",String.valueOf(mSelectedProvince.getId())).find(City.class);
        if (mCityList.size() > 0){
            mDatalist.clear();
            for (int i = 0; i < mCityList.size(); i ++){
                mDatalist.add(mCityList.get(i).getCityName());
            }
            mAdapter.notifyDataSetChanged();
            mList.setSelection(0);
            mCurrentLevel = LEVEL_CITY;
            setCurrentLevel();
        }else{
            int    provinceCode = mSelectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address,"city");
        }
    }

    /*关闭进度对话框*/
    private void closeProgressDialog() {
        if (mProgressDialog != null)mProgressDialog.dismiss();
    }

    /*显示进度对话框*/
    private void showProgressDiaog() {
        if (mProgressDialog == null){
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("正在加载...");
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }

    private void setCurrentLevel(){
        if (mOnDataLevelChange!= null)
            mOnDataLevelChange.dataLevelChange(mCurrentLevel);
    }

    private OnDataLevelChange mOnDataLevelChange;

    public interface OnDataLevelChange{
        void dataLevelChange(int level);
    }

    public void setOnDataLevelChange(OnDataLevelChange onDataLevelChange){
        mOnDataLevelChange = onDataLevelChange;
    }
}
