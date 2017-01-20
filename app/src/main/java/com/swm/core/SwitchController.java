package com.swm.core;

import android.app.Activity;
import android.view.View;

import com.swm.heart.R;

/**
 * Created by swm on 2016/12/23.
 */

class SwitchController implements View.OnClickListener {
    private static final int VIEW_RRI_DISTRIBUTION = 1;
    private static final int VIEW_SDNN_AND_RMSSD = 2;
    private static final int VIEW_RRI_FREQ = 3;

    private Activity mActivity;
    private View mRriDistributionView;
    private View mRriDistributionBtn;

    private View mSdnnAndRmssdView;
    private View mSdnnAndRmssdBtn;

    private View mRriFreqView;
    private View mRriFreqBtn;

    SwitchController(Activity activity, View rriDistributionView, View sdnnandRmssdView, View rriFreqView) {
        mActivity = activity;

        mRriDistributionView = rriDistributionView;
        mRriDistributionBtn = activity.findViewById(R.id.swm_hrv_rri);
        mRriDistributionBtn.setOnClickListener(null);
        mRriDistributionBtn.setBackground(activity.getResources().getDrawable(R.drawable.swm_cta_button));

        mSdnnAndRmssdView = sdnnandRmssdView;
        mSdnnAndRmssdBtn = activity.findViewById(R.id.swm_hrv_sdnn_and_rmssd);
        mSdnnAndRmssdBtn.setOnClickListener(this);

        mRriFreqView = rriFreqView;
        mRriFreqBtn = activity.findViewById(R.id.swm_hrv_frequency);
        mRriFreqBtn.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.swm_hrv_rri:
                switchTo(VIEW_RRI_DISTRIBUTION);
                break;
            case R.id.swm_hrv_sdnn_and_rmssd:
                switchTo(VIEW_SDNN_AND_RMSSD);
                break;
            case R.id.swm_hrv_frequency:
                switchTo(VIEW_RRI_FREQ);
                break;
        }
    }

    private void switchTo(int view) {
        mRriDistributionBtn.setOnClickListener(view == VIEW_RRI_DISTRIBUTION ? null : this);
        mSdnnAndRmssdBtn.setOnClickListener(view == VIEW_SDNN_AND_RMSSD ? null : this);
        mRriFreqBtn.setOnClickListener(view == VIEW_RRI_FREQ ? null : this);

        mRriDistributionBtn.setBackground(mActivity.getResources().getDrawable(view == VIEW_RRI_DISTRIBUTION ? R.drawable.swm_cta_button : R.drawable.swm_inactive_cta_button));
        mSdnnAndRmssdBtn.setBackground(mActivity.getResources().getDrawable(view == VIEW_SDNN_AND_RMSSD ? R.drawable.swm_cta_button : R.drawable.swm_inactive_cta_button));
        mRriFreqBtn.setBackground(mActivity.getResources().getDrawable(view == VIEW_RRI_FREQ ? R.drawable.swm_cta_button : R.drawable.swm_inactive_cta_button));

        mRriDistributionView.setVisibility(view == VIEW_RRI_DISTRIBUTION ? View.VISIBLE : View.INVISIBLE);
        mSdnnAndRmssdView.setVisibility(view == VIEW_SDNN_AND_RMSSD ? View.VISIBLE : View.INVISIBLE);
        mRriFreqView.setVisibility(view == VIEW_RRI_FREQ ? View.VISIBLE : View.INVISIBLE);
    }
}
