package com.swm.core;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.TextView;

import com.swm.chart.LineDataProvider;
import com.swm.chart.RealtimeLineChart;
import com.swm.heart.R;
import com.swm.heart.SwmBaseActivity;
import com.swm.heartbeat.HeartBeatHandler;
import com.swm.heartbeat.HeartBeatListener;
import com.swm.heartbeat.HeartBeatSound;
import com.swm.hrv.FrequencyListener;

public class FrequencyActivity extends SwmBaseActivity implements HeartBeatListener
                                                    , FrequencyListener{
    RealtimeLineChart mHrvFrequency;
    TextView mVeryLowFrequency;
    TextView mLowFrequency;
    TextView mHighFrequency;
    TextView mTotalPower;
    TextView mLfHfRatio;
    SwmBinder mSwmBinder;
    SwitchController mSwitchController;
    private HeartBeatSound mHeartBeatSound;
    private HeartBeatHandler mHeartBeatHandler;
    private LineDataProvider<Double> mVeryLowFreqDataProvider;
    private LineDataProvider<Double> mLowFreqDataProvider;
    private LineDataProvider<Double> mHighFreqDataProvider;
    private LineDataProvider<Double> mTotalPowerDataProvider;
    private LineDataProvider<Double> mLfHfRatioDataProvider;


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mSwmBinder = (SwmBinder) service;

            try {
                mSwmBinder.registerHeartRateListener(FrequencyActivity.this);
                mSwmBinder.setFrequencyListener(FrequencyActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, SwmService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
        setContentView(R.layout.activity_frequency);
        mHrvFrequency = (RealtimeLineChart) findViewById(R.id.swm_hrv_frequency);
        mVeryLowFrequency = (TextView) findViewById(R.id.swm_vlf_value);
        mLowFrequency = (TextView) findViewById(R.id.swm_lf_value);
        mHighFrequency = (TextView) findViewById(R.id.swm_hf_value);
        mTotalPower = (TextView) findViewById(R.id.swm_total_power_value);
        mLfHfRatio = (TextView) findViewById(R.id.swm_lf_hf_ratio_value);
        mSwitchController = new SwitchController(this, findViewById(R.id.swm_hrv_switch));
        mHeartBeatSound = new HeartBeatSound(this);
        mHeartBeatHandler = new HeartBeatHandler(findViewById(R.id.swm_heart), (TextView) findViewById(R.id.swm_heart_rate));

        mVeryLowFreqDataProvider = new LineDataProvider<>();
        mLowFreqDataProvider = new LineDataProvider<>();
        mHighFreqDataProvider = new LineDataProvider<>();
        mTotalPowerDataProvider = new LineDataProvider<>();
        mLfHfRatioDataProvider = new LineDataProvider<>();

        mHrvFrequency.addLineDataProvider(mVeryLowFreqDataProvider);
        mHrvFrequency.addLineDataProvider(mLowFreqDataProvider);
        mHrvFrequency.addLineDataProvider(mHighFreqDataProvider);
        mHrvFrequency.addLineDataProvider(mTotalPowerDataProvider);
        mHrvFrequency.addLineDataProvider(mLfHfRatioDataProvider);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mSwmBinder != null) {
            mSwmBinder.removeHeartRateListener(this);
            mSwmBinder.removeFrequencyListener();
        }
        mHeartBeatSound.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mSwmBinder != null)
            try {
                mSwmBinder.registerHeartRateListener(this);
                mSwmBinder.setFrequencyListener(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    @Override
    public void onHeartBeatDataAvailable(final HeartBeatData heartBeatData) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mHeartBeatSound.onHeartBeatDataAvailable(heartBeatData);
                mHeartBeatHandler.onHeartBeat(heartBeatData.heartRate);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mHeartBeatSound.prepare();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHeartBeatSound.release();
    }

    @Override
    public void onFrequencyDataAvailable(double[] frequencyData) {
        mVeryLowFreqDataProvider.offerData(frequencyData[0]);
        mLowFreqDataProvider.offerData(frequencyData[1]);
        mHighFreqDataProvider.offerData(frequencyData[2]);
        mTotalPowerDataProvider.offerData(frequencyData[3]);
        mLfHfRatioDataProvider.offerData(frequencyData[4]);
    }


}
