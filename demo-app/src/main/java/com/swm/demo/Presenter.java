package com.swm.demo;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.swm.stuff.view.CalorieView;
import com.swm.stuff.view.HeartRateView;
import com.swm.stuff.view.PhyAgeView;
import com.swm.stuff.view.StepView;
import com.swm.stuff.view.StressView;
import com.swm.sdk.CaloriePlugin;
import com.swm.sdk.HeartEngine;
import com.swm.sdk.HrvPlugin;
import com.swm.sdk.RunningPlugin;
import com.swm.sdk.SwmDevice;

/**
 * Created by yangzhenyu on 2017/4/24.
 */

public class Presenter implements Application.ActivityLifecycleCallbacks{
    private final HeartRateView heartRateView;
    private final CalorieView calorieView;
    private final StressView stressView;
    private final PhyAgeView phyAgeView;
    private final StepView stepView;
    private final DeviceStatus deviceStatus;
    private IntentFilter intentFilter;
    private BroadcastReceiver broadcastReceiver;
    private final Activity myActivity;
    private int stepCount;

    public Presenter(Activity activity, HeartRateView heartRateView, CalorieView calorieView, StressView stressView, PhyAgeView phyAgeView, StepView stepView, DeviceStatus deviceStatus) {
        this.myActivity = activity;
        this.heartRateView = heartRateView;
        this.calorieView = calorieView;
        this.stressView = stressView;
        this.phyAgeView = phyAgeView;
        this.stepView = stepView;
        this.deviceStatus = deviceStatus;
    }

    private void registerBroadcastReceiver() {
        intentFilter = new IntentFilter(HeartEngine.ACTION_HEART_RATE);
        intentFilter.addAction(CaloriePlugin.ACTION_CALORIE_AVAILABLE);
        intentFilter.addAction(HrvPlugin.ACTION_STRESS_CHANGED);
        intentFilter.addAction(HrvPlugin.ACTION_PHYSICAL_AGE_CHANGED);
        intentFilter.addAction(SwmDevice.ACTION_DEVICE_EVENT);
        intentFilter.addAction(RunningPlugin.ACTION_STEP);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(HeartEngine.ACTION_HEART_RATE))
                    handleHeartRateBroadcast(intent);
                else if (intent.getAction().equals(CaloriePlugin.ACTION_CALORIE_AVAILABLE))
                    handleCalorie(intent);
                else if (intent.getAction().equals((HrvPlugin.ACTION_STRESS_CHANGED)))
                    handleStressChange(intent);
                else if (intent.getAction().equals(HrvPlugin.ACTION_PHYSICAL_AGE_CHANGED))
                    handlePhyAgeChange(intent);
                else if (intent.getAction().equals(SwmDevice.ACTION_DEVICE_EVENT))
                    handleDeviceEvent(intent);
                else if (intent.getAction().equals(RunningPlugin.ACTION_STEP))
                    handleStep(intent);

            }
        };
        myActivity.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void handleCalorie(Intent intent) {
        float calorie = intent.getFloatExtra(CaloriePlugin.EXTRA_CALORIE, 0f);
        calorieView.setCalorie(calorie);
    }

    private void handleStressChange(Intent intent) {
        com.swm.sdk.HrvPlugin.Stress stress = (HrvPlugin.Stress) intent.getSerializableExtra(HrvPlugin.EXTRA_STRESS);
        if (stress == HrvPlugin.Stress.BAD)
            stressView.setStress((myActivity.getString(R.string.swm_stress_bad)));
        else if (stress == HrvPlugin.Stress.GOOD)
            stressView.setStress(myActivity.getString(R.string.swm_stress_good));
        else if (stress == HrvPlugin.Stress.HAPPY)
            stressView.setStress(myActivity.getString(R.string.swm_stress_happy));
        else if (stress == HrvPlugin.Stress.NORMAL)
            stressView.setStress(myActivity.getString(R.string.swm_stress_normal));
    }

    private void handlePhyAgeChange(Intent intent) {
        int phyAge = intent.getIntExtra(HrvPlugin.EXTRA_PHYSICAL_AGE, 0);
        phyAgeView.setPhyAge(phyAge);
    }

    private void handleDeviceEvent(Intent intent) {
        int state = intent.getIntExtra(SwmDevice.EXTRA_CONNECTION_STATUS, 0);
        if(state == SwmDevice.CONNECTED)
            deviceStatus.active();
        else
            deviceStatus.inactive();
    }

    public void handleStep(Intent intent) {
        int step = intent.getIntExtra(RunningPlugin.EXTRA_STEP, 0);
        stepCount += step;
        stepView.setStep(stepCount);
    }

    private void unregisterroadcastReceiver() {
        myActivity.unregisterReceiver(broadcastReceiver);
    }

    private void handleHeartRateBroadcast(Intent intent) {
        heartRateView.setHeartRate(intent.getIntExtra(HeartEngine.EXTRA_HEART_RATE, 0));
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (activity == myActivity)
            registerBroadcastReceiver();
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (activity == myActivity)
            unregisterroadcastReceiver();
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
