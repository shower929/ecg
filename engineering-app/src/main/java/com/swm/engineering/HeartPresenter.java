package com.swm.engineering;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.RadioGroup;

import com.swm.chart.Oscilloscope;
import com.swm.filter.FirFilter;
import com.swm.filter.NotchFilter;
import com.swm.filter.RemoveBaselineWander;
import com.swm.sdk.EcgDataSource;
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

public class HeartPresenter extends Presenter implements RadioGroup.OnCheckedChangeListener{
    private Activity myActivity;
    private final View parent;
    private final Oscilloscope oscilloscope;
    private final HeartRateView heartRateView;
    private final HrvView hrvView;
    private final StressView stressView;
    private final PhyAgeView phyAgeView;
    private final RadioGroup xScaleView;
    private final RadioGroup yScaleView;

    private final HeartBeatSound heartBeatSound;

    private IntentFilter intentFilter;
    private BroadcastReceiver broadcastReceiver;

    /**
     * Double is 8 bytes.  Sample rate is 250 Hz.  Send 5 sample data every connection interval.
     */
    private int period = 4;
    private int averageSample = 200;
    private int payload = 5;
    private int SAMPLE_RATE = 250;
    private int lineBuffer = period * averageSample * payload * Double.SIZE / Byte.SIZE;
    private float xScale = 1.1f;
    private float yScale = 1.0f;

    private FirFilter firFilter;
    private NotchFilter notchFilter;
    private RemoveBaselineWander removeBaselineWander;

    HeartPresenter(Activity activity, View view) {
        this.myActivity = activity;
        this.parent = view;
        this.oscilloscope = (Oscilloscope) parent.findViewById(R.id.swm_ecg_breath_view);
        oscilloscope.setBufferSize(lineBuffer);
        oscilloscope.setClock(Math.round(1f/ SAMPLE_RATE * 1000));
        oscilloscope.setXScale(xScale);
        oscilloscope.setYScale(yScale);
        oscilloscope.setFrequency(1000.0 / SAMPLE_RATE);

        notchFilter = new NotchFilter(SAMPLE_RATE);
        oscilloscope.addFilter(notchFilter);

        removeBaselineWander = new RemoveBaselineWander(SAMPLE_RATE);
        oscilloscope.addFilter(removeBaselineWander);

        //firFilter = new FirFilter(SAMPLE_RATE);
        //oscilloscope.addFilter(firFilter);

        this.heartRateView = (HeartRateView) parent.findViewById(R.id.swm_heart_rate_view);
        this.heartRateView.setHeartRate(0);
        this.hrvView = (HrvView) parent.findViewById(R.id.swm_hrv_view);
        hrvView.setSdnn(0);
        hrvView.setRmssd(0);
        this.stressView = (StressView) parent.findViewById(R.id.swm_stress_view);
        this.stressView.setStress("");
        this.phyAgeView = (PhyAgeView) parent.findViewById(R.id.swm_phy_age_view);
        this.phyAgeView.setPhyAge(0);
        this.heartBeatSound = new HeartBeatSound(myActivity);

        xScaleView = (RadioGroup) view.findViewById(R.id.swm_x_scale);
        xScaleView.setOnCheckedChangeListener(this);

        yScaleView = (RadioGroup) view.findViewById(R.id.swm_y_scale);
        yScaleView.setOnCheckedChangeListener(this);
    }

    private void initDataSource() {
        oscilloscope.addDataSource(new EcgDataSource(), myActivity.getResources().getColor(R.color.swm_ecg));
        // @TODO add breath line
        //lineChart.addDataSource(new BreathDataSource(), myActivity.getResources().getColor(R.color.swm_breath));
    }

    private void registerBroadcastReceiver() {
        intentFilter = new IntentFilter(HeartEngine.ACTION_HEART_RATE);

        intentFilter.addAction(HrvPlugin.ACTION_STRESS_CHANGED);
        intentFilter.addAction(HrvPlugin.ACTION_PHYSICAL_AGE_CHANGED);
        intentFilter.addAction(RunningPlugin.ACTION_STEP);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(HeartEngine.ACTION_HEART_RATE))
                    handleHeartRateBroadcast(intent);
                else if (intent.getAction().equals((HrvPlugin.ACTION_STRESS_CHANGED)))
                    handleStressChange(intent);
                else if (intent.getAction().equals(HrvPlugin.ACTION_PHYSICAL_AGE_CHANGED))
                    handlePhyAgeChange(intent);

            }
        };
        myActivity.registerReceiver(broadcastReceiver, intentFilter);
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

    private void unregisterroadcastReceiver() {
        myActivity.unregisterReceiver(broadcastReceiver);
    }

    private void handleHeartRateBroadcast(Intent intent) {
        int heartRate = intent.getIntExtra(HeartEngine.EXTRA_HEART_RATE, 0);
        heartRateView.setHeartRate(heartRate);
        heartBeatSound.setHeartRate(heartRate);
    }

    @Override
    void onStart() {
        registerBroadcastReceiver();
        initDataSource();
    }

    @Override
    void onStop() {
        unregisterroadcastReceiver();
    }

    @Override
    void show() {
        onStart();
    }

    @Override
    void hide() {
        onStop();
    }


    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch(checkedId) {
            case R.id.swm_x_smaller:
                oscilloscope.setXScale(0.75f);
                break;
            case R.id.swm_x_the_same:
                oscilloscope.setXScale(1.0f);
                break;
            case R.id.swm_x_bigger:
                oscilloscope.setXScale(1.25f);
                break;
            case R.id.swm_y_smaller:
                oscilloscope.setYScale(0.75f);
                break;
            case R.id.swm_y_the_same:
                oscilloscope.setYScale(1.0f);
                break;
            case R.id.swm_y_bigger:
                oscilloscope.setYScale(1.25f);
                break;
        }
    }
}
