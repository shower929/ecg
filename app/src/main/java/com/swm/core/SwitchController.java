package com.swm.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.swm.heart.R;

/**
 * Created by swm on 2016/12/23.
 */

class SwitchController implements View.OnClickListener {
    private Activity mActivity;

    SwitchController(Activity activity, View view) {
        mActivity = activity;

        View rri = activity.findViewById(R.id.swm_hrv_rri);
        View sdnnAndRmssd = activity.findViewById(R.id.swm_hrv_sdnn_and_rmssd);
        View frequency = activity.findViewById(R.id.swm_hrv_frequency);

        rri.setOnClickListener(this);
        sdnnAndRmssd.setOnClickListener(this);

        frequency.setOnClickListener(this);

        if(activity instanceof SdnnAndRmssdActivity) {
            sdnnAndRmssd.setOnClickListener(null);
            sdnnAndRmssd.setBackground(activity.getResources().getDrawable(R.drawable.swm_cta_button));
        }

        if (activity instanceof RriActivity) {
            rri.setOnClickListener(null);
            rri.setBackground(activity.getResources().getDrawable(R.drawable.swm_cta_button));
        }

        if (activity instanceof FrequencyActivity) {
            frequency.setOnClickListener(null);
            frequency.setBackground(activity.getResources().getDrawable(R.drawable.swm_cta_button));
        }
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.swm_hrv_rri:
                switchToRriActivity();
                break;
            case R.id.swm_hrv_sdnn_and_rmssd:
                switchToSdnnAndRmssdActivity();
                break;
            case R.id.swm_hrv_frequency:
                switchToFrequency();
                break;
        }
    }

    private void switchToSdnnAndRmssdActivity() {
        Intent intent = new Intent(mActivity, SdnnAndRmssdActivity.class);
        mActivity.startActivity(intent);
        mActivity.finish();
    }

    private void switchToRriActivity() {
        Intent intent = new Intent(mActivity, RriActivity.class);
        mActivity.startActivity(intent);
        mActivity.finish();
    }

    private void switchToFrequency() {
        Intent intent = new Intent(mActivity, FrequencyActivity.class);
        mActivity.startActivity(intent);
        mActivity.finish();
    }
}
