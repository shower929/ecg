package com.swm.app.superrun.training;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.swm.view.IntensityMeter;
import com.swm.view.IntensityView;
import com.swm.app.superrun.power.MeterListener;
import com.swm.app.superrun.power.SwmMeter;
import com.swm.core.HeartRateData;
import com.swm.device.SwmDeviceListener;
import com.swm.heart.R;
import com.swm.heartbeat.HeartRateListener;
import com.swm.training.TrainingListener;


/**
 * Created by yangzhenyu on 2016/10/14.
 */

public class TrainingIntensityPresenter extends Handler implements TrainingListener
                                                , HeartRateListener
                                                , SwmDeviceListener  {
    private IntensityMeter mMeter;

    private int currentValue;

    private int MAX_HEART_RATE = 220 - 50;
    private int STABLE_HEART_RATE = 72;
    private int mCurrentHeartRate;
    private int mCurrentMaxHeartRate;

    private float excellentLevel;
    private float goodLevel;
    private float poorLevel;

    private IntensityView intensityView;

    //private Handler handler  = new Handler();
    //private Runnable run = new Runnable() {
        //@Override
        //public void run() {
            //mMeter.setValue((int) (System.currentTimeMillis() % 7) + 1);
            //handler.postDelayed(this, 6000);
    //    }
    //};

    public TrainingIntensityPresenter(IntensityMeter meter, IntensityView intensityView) {
        mMeter = meter;
        this.intensityView = intensityView;
        //handler.post(run);
    }

    public void setExcellentLevel(int level) {
        excellentLevel = level;
        mMeter.setExcellentLevel((int)excellentLevel);
    }

    public void setGoodLevel(int level) {
        goodLevel = level;
        mMeter.setGoodLevel((int)goodLevel);
    }

    public void setPoorLevel(int level) {
        poorLevel = level;
        mMeter.setPoorLevel((int)poorLevel);
    }

    public void setHeartRate(int heartRate) {
        if (mCurrentHeartRate == heartRate)
            return;

        mCurrentHeartRate = heartRate;

        mMeter.setSecondValue(heartRate);
        float p = (float)(heartRate - STABLE_HEART_RATE) / (MAX_HEART_RATE - STABLE_HEART_RATE);
        if (p >= 0.55 && p <= 0.79)
            mMeter.updateSecondCircleColor(mMeter.getResources().getColor(R.color.swm_mhr_E));
        if (p >= 0.8 && p <= 0.9)
            mMeter.updateSecondCircleColor(mMeter.getResources().getColor(R.color.swm_mhr_M));
        if (p >= 0.88 && p <= 0.92)
            mMeter.updateSecondCircleColor(mMeter.getResources().getColor(R.color.swm_mhr_T));
        if (p >= 0.975 && p == 1)
            mMeter.updateSecondCircleColor(mMeter.getResources().getColor(R.color.swm_mhr_I));
        if (p > 1)
            mMeter.updateSecondCircleColor(mMeter.getResources().getColor(R.color.swm_mhr_R));

        mMeter.invalidate();
    }

    @Override
    public void onTrainingIntensityChanged(final int newIntensity) {
        intensityView.post(new Runnable() {
            @Override
            public void run() {
                intensityView.setIntensity(newIntensity);
            }
        });

        mMeter.post(new Runnable() {
            @Override
            public void run() {
                mMeter.setValue(newIntensity);
            }
        });

    }

    @Override
    public void onHeartRateDataAvailable(final HeartRateData heartRateData) {
        mMeter.post(new Runnable() {
            @Override
            public void run() {
                setHeartRate(heartRateData.heartRate);
            }
        });

    }

    @Override
    public void onConnectStateChanged(int state) {

        if (state == SwmDeviceListener.CONNECTED)
            mMeter.on();
        else if (state == SwmDeviceListener.DISCONNECTED)
            mMeter.off();
    }
}
