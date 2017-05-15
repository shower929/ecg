package com.swm.engineering;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;

import com.swm.sdk.HeartEngine;
import com.swm.sdk.HrvPlugin;
import com.swm.sdk.RunningPlugin;
import com.swm.stuff.view.PhyAgeView;
import com.swm.stuff.view.StressView;
import com.swm.stuff.view.HeartRateView;
import com.swm.stuff.view.HrvView;
import com.swm.stuff.HeartBeatSound;
import com.swm.engineering.app.R;

/**
 * Created by yangzhenyu on 2017/4/28.
 */

public class HeartPresenter extends Presenter {
    private Activity myActivity;
    private final View parent;

    private OscilloscopeController oscilloscopeController;
    private final HeartRateView heartRateView;
    private final HrvView hrvView;
    private final StressView stressView;
    private final PhyAgeView phyAgeView;


    private final HeartBeatSound heartBeatSound;

    private IntentFilter intentFilter;
    private BroadcastReceiver broadcastReceiver;


    public HeartPresenter(Activity activity, View view, OscilloscopeController oscilloscopeController) {
        this.myActivity = activity;
        this.parent = view;

        this.oscilloscopeController = oscilloscopeController;

        heartRateView = (HeartRateView) parent.findViewById(R.id.swm_heart_rate_view);

        hrvView = (HrvView) parent.findViewById(R.id.swm_hrv_view);

        stressView = (StressView) parent.findViewById(R.id.swm_stress_view);

        phyAgeView = (PhyAgeView) parent.findViewById(R.id.swm_phy_age_view);

        this.heartBeatSound = new HeartBeatSound(myActivity);
    }

    private void registerBroadcastReceiver() {
        intentFilter = new IntentFilter(HeartEngine.ACTION_HEART_RATE);
        intentFilter.addAction(HeartEngine.ACTION_HRV);
        intentFilter.addAction(HrvPlugin.ACTION_STRESS_CHANGED);
        intentFilter.addAction(HrvPlugin.ACTION_PHYSICAL_AGE_CHANGED);
        intentFilter.addAction(RunningPlugin.ACTION_STEP);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(HeartEngine.ACTION_HEART_RATE))
                    handleHeartRateBroadcast(intent);
                else if (intent.getAction().equals(HeartEngine.ACTION_HRV))
                    handleHrv(intent);
                else if (intent.getAction().equals((HrvPlugin.ACTION_STRESS_CHANGED)))
                    handleStressChange(intent);
                else if (intent.getAction().equals(HrvPlugin.ACTION_PHYSICAL_AGE_CHANGED))
                    handlePhyAgeChange(intent);

            }
        };
        myActivity.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void handleHrv(Intent intent) {
        float sdnn = intent.getFloatExtra(HeartEngine.EXTRA_SDNN, 0);
        float rmssd = intent.getFloatExtra(HeartEngine.EXTRA_RMSSD, 0);

        hrvView.setSdnn(sdnn);
        hrvView.setRmssd(rmssd);
    }

    private void handleStressChange(Intent intent) {
        int stress = intent.getIntExtra(HrvPlugin.EXTRA_STRESS, 0);
        if (stress == HrvPlugin.STRESS_BAD)
            stressView.setStress((myActivity.getString(R.string.swm_stress_bad)));
        else if (stress == HrvPlugin.STRESS_GOOD)
            stressView.setStress(myActivity.getString(R.string.swm_stress_good));
        else if (stress == HrvPlugin.STRESS_HAPPY)
            stressView.setStress(myActivity.getString(R.string.swm_stress_happy));
        else if (stress == HrvPlugin.STRESS_NORMAL)
            stressView.setStress(myActivity.getString(R.string.swm_stress_normal));
    }

    private void handlePhyAgeChange(Intent intent) {
        int phyAge = intent.getIntExtra(HrvPlugin.EXTRA_PHYSICAL_AGE, 0);
        phyAgeView.setPhyAge(phyAge);
    }

    private void unregisterroadcastReceiver() {
        myActivity.unregisterReceiver(broadcastReceiver);
    }

    private void handleHeartRateBroadcast(Intent intent) {
        int heartRate = intent.getIntExtra(HeartEngine.EXTRA_HEART_RATE, 0);
        heartRateView.setHeartRate(heartRate);
        heartBeatSound.setHeartRate(heartRate);
    }

    @Override
    public void onStart() {
        parent.setVisibility(View.VISIBLE);
        heartBeatSound.prepare();
        registerBroadcastReceiver();
        // @TODO add breath line
        //lineChart.addDataSource(new BreathDataSource(), myActivity.getResources().getColor(R.color.swm_breath));
        oscilloscopeController.start();
    }

    @Override
    public void onStop() {
        oscilloscopeController.stop();

        unregisterroadcastReceiver();
        parent.setVisibility(View.GONE);
        heartBeatSound.release();
    }

    @Override
    public void show() {
        onStart();
    }

    @Override
    public void hide() {
        onStop();
    }

    public OscilloscopeController getOscilloscopeController() {
        return oscilloscopeController;
    }

}
