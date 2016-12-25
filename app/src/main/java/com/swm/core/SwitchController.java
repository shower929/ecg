package com.swm.core;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.swm.heart.R;

/**
 * Created by swm on 2016/12/23.
 */

class SwitchController implements View.OnClickListener {
    private View mView;
    private Context mContext;

    SwitchController(Activity activity, View view) {
        mView = view;

        View rri = activity.findViewById(R.id.swm_hrv_rri);
        View sdnnAndRmssd = activity.findViewById(R.id.swm_hrv_sdnn_and_rmssd);
        View frequency = activity.findViewById(R.id.swm_hrv_frequency);
        View poincare = activity.findViewById(R.id.swm_hrv_poincare);

        rri.setOnClickListener(this);
        sdnnAndRmssd.setOnClickListener(this);

        frequency.setOnClickListener(this);
        poincare.setOnClickListener(this);

        if(activity instanceof SdnnAndRmssdActivity) {
            sdnnAndRmssd.setOnClickListener(null);
            sdnnAndRmssd.setBackground(activity.getResources().getDrawable(R.drawable.swm_cta_button));
        }
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.swm_hrv_rri:
                //switchToRriActivity();
                break;
            case R.id.swm_hrv_sdnn_and_rmssd:
                switchToSdnnAndRmssdActivity();
                break;
            case R.id.swm_hrv_frequency:
                //switchToFrequency();
                break;
            case R.id.swm_hrv_poincare:
                //switchToPoincare();
                break;
        }
    }

    private void switchToSdnnAndRmssdActivity() {

    }
}
