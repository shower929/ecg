package com.swm.sdk;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.swm.core.HrvActivity;
import com.swm.core.MotionActivity;
import com.swm.heart.R;

/**
 * Created by swm on 2016/12/22.
 */

class MenuController {
    private Context mContext;
    private View mMenu;

    MenuController(Context context, View menu) {
        mContext = context;
        mMenu = menu;
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
        Intent intent = new Intent(mContext, HeartActivity.class);
        mContext.startActivity(intent);
    }

    void startHrvActivity() {
        Intent intent = new Intent(mContext, HrvActivity.class);
        mContext.startActivity(intent);
    }

    void startMotionActivity() {
        Intent intent = new Intent(mContext, MotionActivity.class);
        mContext.startActivity(intent);
    }

    void onToggle() {
        if (mMenu.getVisibility() != View.VISIBLE)
            mMenu.setVisibility(View.VISIBLE);
        else {
            mMenu.setVisibility(View.GONE);
        }
    }
}
