package com.srs.supercoolweather;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.srs.supercoolweather.fragment.ChooseAreaFragment;

public class MainActivity
        extends AppCompatActivity
{

    private int mCurrentLevel;
    private ChooseAreaFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFragment = (ChooseAreaFragment) getFragmentManager().findFragmentById(R.id.choose_area_fragment);
        mFragment.setOnDataLevelChange(new ChooseAreaFragment.OnDataLevelChange() {
            @Override
            public void dataLevelChange(int level) {
                mCurrentLevel = level;
            }
        });
    }

    @Override
    public void onBackPressed() {
        switch (mCurrentLevel) {
            case 0:
                    finish();
                break;
            case 1:
                mFragment.queryProvinces();
                break;
            case 2:
                mFragment.queryCities();
                break;
            default:
                 break;
        }
    }
}
