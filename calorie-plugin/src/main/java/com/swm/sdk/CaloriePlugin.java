package com.swm.sdk;

import android.os.Handler;

/**
 * Created by yangzhenyu on 2017/4/20.
 */

public class CaloriePlugin implements HeartEngineOutput {
    public enum Gender{MALE, FEMALE};
    private final Gender gender;
    private final int age;
    private final int weight;
    private CaloriePluginListener listener;
    private Handler handler;
    private volatile int heartRate;

    private Runnable timer = new Runnable() {
        private int duration = 0;

        @Override
        public void run() {
            if (listener == null)
                return;

            float calorie = APPS_RUNNING_GetCalorie(gender, age, heartRate, weight, ++duration, 0);
            listener.onCalorieAvailable(calorie);
            handler.postDelayed(this, 1000);
        }
    };

    public CaloriePlugin(Gender gender, int age, int weight) {
        this.gender = gender;
        this.age = age;
        this.weight = weight;
    }

    @Override
    public void onHeartRateAvailable(int heartRate) {
        if(gender == null || age == 0 || weight == 0)
            throw new RuntimeException("Need input gender, height and weight");
        
        this.heartRate = heartRate;

    }

    private float APPS_RUNNING_GetCalorie(Gender enSex, int i16Age, int i16HR, float fWeight, float fSecondTime, float fVO2Max)
    {
        float fCalorie = 0;

        if (fVO2Max <= 0)
        {
            if (enSex == Gender.FEMALE)
            {
                fCalorie = (float)(fSecondTime * ((-20.4022 +
                        (0.4472 * i16HR) +
                        (0.1236 * fWeight) +
                        (0.074 * i16Age)) / 4.184) / 60);
            }
            else
            {
                fCalorie = (float)(fSecondTime * ((-55.0969 +
                        (0.6309 * i16HR) +
                        (0.1988 * fWeight) +
                        (0.2017 * i16Age)) / 4.184) / 60);
            }
        }
        else
        {
            if (enSex == Gender.FEMALE)
            {
                fCalorie = (float)(fSecondTime * ((-59.3954 +
                        (0.45 * i16HR) +
                        (0.38 * fVO2Max) +
                        (0.103 * fWeight) +
                        (0.274 * i16Age)) / 4.184) / 60);
            }
            else
            {
                fCalorie = (float)(fSecondTime * ((-95.7735 +
                        (0.634 * i16HR) +
                        (0.404 * fVO2Max) +
                        (0.394 * fWeight) +
                        (0.271 * i16Age)) / 4.184) / 60);
            }
        }

        if (fCalorie < 0)
            fCalorie = 0;

        return fCalorie;
    }

    public void setListener(CaloriePluginListener listener) {
        this.listener = listener;
    }

    public synchronized void on() {
        if (handler == null)
            handler = new Handler();
        handler.post(timer);
    }

    public synchronized void off() {
        handler.removeCallbacks(timer);
    }
}