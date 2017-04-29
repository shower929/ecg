package com.swm.engineering.app;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * Created by yangzhenyu on 2016/9/22.
 */

public class BleIconHandler {

    private ImageView mIcon;
    private ProgressBar mProgress;

    public BleIconHandler() {

    }

    public void setIcon(View view) {
        mProgress = (ProgressBar) view.findViewById(R.id.swm_ble_icon_scan);
        //@TODO add service status icon
        //mIcon = (ImageView) group.findViewById(R.id.swm_ble_icon_status);
    }

    public void connecting() {
        if (mProgress != null) {
            mProgress.setVisibility(View.VISIBLE);
        }
        if (mIcon != null) {
            mIcon.setVisibility(View.GONE);
        }
    }

    public void scanning() {
        if (mIcon != null) {
            mIcon.setVisibility(View.GONE);
        }
        if (mProgress != null) {
            mProgress.setVisibility(View.VISIBLE);
        }
    }

    public void idle() {
        if (mProgress != null) {
            mProgress.setVisibility(View.GONE);
        }
        if (mIcon != null) {
            mIcon.setVisibility(View.VISIBLE);
            //@TODO Place idle icon
            //mIcon.setImageDrawable(ContextCompat.getDrawable(R.drawable.swm_ble_idle));
        }
    }

    public void connected() {
        if (mProgress != null) {
            mProgress.setVisibility(View.GONE);
        }

        if (mIcon != null) {
            mIcon.setVisibility(View.VISIBLE);
            //@TODO Place connected icon
            //mIcon.setImageDrawable(ContextCompat.getDrawable(R.drawable.swm_ble_connected));
        }

    }

    public void disconnected() {
        if (mProgress != null) {
            mProgress.setVisibility(View.GONE);
        }
        if (mIcon != null) {
            mIcon.setVisibility(View.VISIBLE);
            //@TODO Place disconnected icon
            //mIcon.setImageDrawable(ContextCompat.getDrawable(R.drawable.swm_ble_disconnected));
        }
    }
}
