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
    public static final int MSG_DRAW_METER = 2;
    public static final int MSG_TURNOFF = 3;
    public static final int MSG_SET_MAX_HEART_RATE = 4;

    private static final String KEY_POWER = "key_power";
    private static final String KEY_MAX_HEART_RATE = "key_max_heart_rate";
    private static final String KEY_VALUE = "key_value";

    private static final int FPS = 60;
    private Animator mMeterValueColorAnim;
    private int currentValue;
    private int mMeterValueColor;
    private int MAX_HEART_RATE = 220 - 50;
    private int STABLE_HEART_RATE = 72;
    private int mCurrentHeartRate;
    private int mCurrentMaxHeartRate;
    private int max;
    private float excellentLevel;
    private float goodLevel;
    private float poorLevel;

    private static final float CLOCK = Math.round((float)1000 / FPS);

    public RunPowerMeterHandler(SwmMeter meter, TextView meterValue) {
        mMeter = meter;
        mMeterValue = meterValue;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch(msg.what) {
            case MSG_TURNON:
                mMeter.turnon();

                break;
            case MSG_DRAW_METER:
                Bundle data = msg.getData();
                int power = data.getInt(KEY_POWER);
                int value = data.getInt(KEY_VALUE);
                mMeter.setRunPower(power);
                mMeter.invalidate();
                mMeterValue.setText(String.valueOf(value));
                if (value >= excellentLevel)
                    setMeterValueColor(mMeterValue.getResources().getColor(R.color.swm_run_power_excellent));
                else if (value < excellentLevel && value >= goodLevel)
                    setMeterValueColor(mMeterValue.getResources().getColor(R.color.swm_run_power_good));
                else
                    setMeterValueColor(mMeterValue.getResources().getColor(R.color.swm_run_power_poor));
                break;

            case MSG_TURNOFF:
                mMeter.turnoff();
                break;

        }
    }

    public void setMax(int max) {
        this.max = max;
    }

    private void drawMeter(int newValue) {

        float currentFraction = (float) currentValue / max * SwmMeter.MAX_DEGREE;
        currentFraction = currentFraction > SwmMeter.MAX_DEGREE ? SwmMeter.MAX_DEGREE : currentFraction;

        float newFraction = (float) newValue / max * SwmMeter.MAX_DEGREE;

        int len = Math.abs(currentValue - newValue);
        float fractionStep = newFraction / len;

        final int sign = (newValue - currentValue) > 0 ? 1 : -1;
        int newStep = currentValue;

        for (int i = 0; i < len; i++) {
            currentFraction = currentFraction + fractionStep * sign;
            newStep = newStep + sign;
            Bundle data = new Bundle();
            data.putInt(KEY_POWER, (int) currentFraction);
            data.putInt(KEY_VALUE, newStep);
            Message msg = obtainMessage(MSG_DRAW_METER);
            msg.setData(data);
            sendMessageDelayed(msg, i * Math.round(CLOCK));
        }
        currentValue = newValue;
    }


    public void setMainValue(int newValue) {

        if(currentValue == newValue)
            return;

        drawMeter(newValue);

        currentValue = newValue;

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

    @Override
    public void onChange(int value) {
        if (mMeterValue == null)
            return;


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
