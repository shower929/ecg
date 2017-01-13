package com.swm.core;

import android.content.Context;
import android.content.Intent;

import com.swm.heart.R;

/**
 * Created by swm on 2016/12/22.
 */

class MenuController {
    private Context mContext;

    MenuController(Context context) {
        mContext = context;
    }

    void onSwitchMode(int menuId) {
        switch(menuId) {
            case R.id.swm_menu_ecg:
                startEcgActivity();
                break;
            case R.id.swm_menu_hrv:
                startHrvActivity();
                break;
            case R.id.swm_menu_motion:
                startMotionActivity();
                break;
            case R.id.swm_menu_ble:

                break;
        }
    }

    void startEcgActivity() {
        Intent intent = new Intent(mContext, CompositeActivity.class);
        mContext.startActivity(intent);
    }

    void startHrvActivity() {
        Intent intent = new Intent(mContext, RriActivity.class);
        mContext.startActivity(intent);
    }

    void startMotionActivity() {
        Intent intent = new Intent(mContext, MotionActivity.class);
        mContext.startActivity(intent);
    }
}
