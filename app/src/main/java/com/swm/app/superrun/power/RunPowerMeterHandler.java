package com.swm.app.superrun.power;


import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.swm.heart.R;


/**
 * Created by yangzhenyu on 2016/10/14.
 */

public class RunPowerMeterHandler extends Handler implements MeterListener {
    private SwmMeter mMeter;
    private TextView mMeterValue;
    public static final int MSG_TURNON = 1;
    public static final int MSG_SET_MAIN_VALUE = 2;
    public static final int MSG_TURNOFF = 3;
    public static final int MSG_SET_MAX_HEART_RATE = 4;

    private static final String KEY_POWER = "key_power";
    private static final String KEY_MAX_HEART_RATE = "key_max_heart_rate";
    private static final int FPS = 60;
    private Animator mMeterValueColorAnim;
    private int mMainValue;
    private int mMeterValueColor;
    private int MAX_HEART_RATE = 220 - 50;
    private int STABLE_HEART_RATE = 72;
    private int mCurrentHeartRate;
    private int mCurrentMaxHeartRate;
    private int mStep;

    public RunPowerMeterHandler(SwmMeter meter, TextView meterValue) {
        mMeter = meter;
        mMeterValue = meterValue;
        mMeter.setCallback(this);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch(msg.what) {
            case MSG_TURNON:
                mMeter.turnon();

                break;
            case MSG_SET_MAIN_VALUE:
                Bundle powerData = msg.getData();
                int power = powerData.getInt(KEY_POWER);

                mMeter.setRunPower(power);
                mMeter.invalidate();

                break;
            case MSG_TURNOFF:
                mMeter.turnoff();
                break;

        }
    }

    public void setStep(int step) {
        mStep = step;
    }

    public void setMainValue(int mainValue) {
        if(mMainValue == mainValue)
            return;
        float fraction = (float)mainValue / mStep * SwmMeter.MAX_DEGREE;
        fraction = fraction > SwmMeter.MAX_DEGREE ? SwmMeter.MAX_DEGREE : fraction;

        float currentFraction = (float)mMainValue / mStep * SwmMeter.MAX_DEGREE;
        currentFraction = currentFraction > SwmMeter.MAX_DEGREE ? SwmMeter.MAX_DEGREE : currentFraction;

        float frame = Math.round((float)300 / FPS);
        int len = (int) Math.abs(currentFraction - fraction);
        final int step = (fraction - currentFraction) > 0 ? 1 : -1;

        mMainValue = mainValue;

        for (int i = 0; i < len; i++) {
            currentFraction = currentFraction + step;
            Bundle data = new Bundle();
            data.putInt(KEY_POWER, (int) currentFraction);
            Message msg = obtainMessage(MSG_SET_MAIN_VALUE);
            msg.setData(data);
            sendMessageDelayed(msg, i * Math.round(frame));
        }

    }

    @Override
    public void onChange(int value) {
        if (mMeterValue == null)
            return;

        if (value < 40)
            setMeterValueColor(mMeterValue.getResources().getColor(R.color.swm_run_power_poor));
        else
            setMeterValueColor(mMeterValue.getResources().getColor(R.color.swm_run_power_good));

        mMeterValue.setText(String.valueOf(value));
    }

    private void setMeterValueColor(int color) {
        if (color == mMeterValueColor)
            return;

        mMeterValueColor = color;

        if (mMeterValueColorAnim != null && mMeterValueColorAnim.isStarted())
            mMeterValueColorAnim.cancel();

        ArgbEvaluator evaluator = new ArgbEvaluator();
        mMeterValueColorAnim = ObjectAnimator.ofObject(mMeterValue, "textColor", evaluator, mMeterValue.getCurrentTextColor(), color);
        if (!mMeterValueColorAnim.isStarted()) {
            mMeterValueColorAnim.setDuration(500);
            mMeterValueColorAnim.start();
        }
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
}
