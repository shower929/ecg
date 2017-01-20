package com.swm.core;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.swm.chart.RealtimeHistogram;
import com.swm.chart.RealtimeLineChart;
import com.swm.chart.RealtimeSingleLineChart;
import com.swm.chart.SignalGenerator;
import com.swm.heart.R;
import com.swm.heart.SwmBaseActivity;
import com.swm.heartbeat.HeartBeatHandler;
import com.swm.heartbeat.HeartRateListener;
import com.swm.heartbeat.HeartRateSound;
import com.swm.hrv.RmssdListener;
import com.swm.hrv.RriDistributionListener;
import com.swm.hrv.RriFrequencyListener;
import com.swm.hrv.SdnnListener;

public class HrvActivity extends SwmBaseActivity implements SdnnListener
                                                    , RmssdListener
                                                    , HeartRateListener
                                                    , RriDistributionListener
                                                    , RriFrequencyListener
                                                    , View.OnClickListener{
    SwmBinder mSwmBinder;
    SwitchController mSwitchController;
    private HeartRateSound mHeartBeatSound;
    private HeartBeatHandler mHeartBeatHandler;

    private View mRriDistributionLayout;
    private RealtimeHistogram mRriDistributionHistogram;

    private View mSdnnAndRmssdLayout;
    private RealtimeSingleLineChart mSdnnLineChart;
    private TextView mSdnnValue;
    private RealtimeSingleLineChart mRmssdLineChart;
    private TextView mRmssdValue;

    private View mRriFreqLayout;
    private RealtimeLineChart mRriFrequencyChart;
    private TextView mVeryLowFrequency;
    private TextView mLowFrequency;
    private TextView mHighFrequency;
    private TextView mTotalPower;
    private TextView mLfHfRatio;
    private SignalGenerator mSignalGenerator;

    private MenuController mMenuController;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mSwmBinder = (SwmBinder) service;
            mSwmBinder.startMonitorHrv();
            try {
                mSwmBinder.registerHeartRateListener(HrvActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }

            mSwmBinder.setSdnnListener(HrvActivity.this);
            mSwmBinder.setRmssdListener(HrvActivity.this);
            mSwmBinder.setRriDistributionListener(HrvActivity.this);
            mSwmBinder.setRriFreqListener(HrvActivity.this);
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
        setContentView(R.layout.activity_hrv);

        mRriDistributionLayout = findViewById(R.id.swm_rri_distrubution_layout);
        mRriDistributionHistogram = (RealtimeHistogram) findViewById(R.id.swm_rri_distribution);

        mSdnnAndRmssdLayout = findViewById(R.id.swm_sdnn_and_rmssd_layout);
        mSdnnLineChart = (RealtimeSingleLineChart) findViewById(R.id.swm_sdnn);
        mSdnnValue = (TextView) findViewById(R.id.swm_sdnn_value);
        mRmssdLineChart = (RealtimeSingleLineChart) findViewById(R.id.swm_rmssd);
        mRmssdValue = (TextView) findViewById(R.id.swm_rmssd_value);

        // Frequency
        mRriFreqLayout = findViewById(R.id.swm_rri_freq_layout);
        mRriFrequencyChart = (RealtimeLineChart) findViewById(R.id.swm_rri_frequency_chart);
        mVeryLowFrequency = (TextView) findViewById(R.id.swm_vlf_value);
        mLowFrequency = (TextView) findViewById(R.id.swm_lf_value);
        mHighFrequency = (TextView) findViewById(R.id.swm_hf_value);
        mTotalPower = (TextView) findViewById(R.id.swm_total_power_value);
        mLfHfRatio = (TextView) findViewById(R.id.swm_lf_hf_ratio_value);
        mSignalGenerator = new SignalGenerator();
        mSignalGenerator.setListener(mRriFrequencyChart);

        mSwitchController = new SwitchController(this, mRriDistributionLayout, mSdnnAndRmssdLayout, mRriFreqLayout);
        mHeartBeatSound = new HeartRateSound(this);
        mHeartBeatHandler = new HeartBeatHandler(findViewById(R.id.swm_heart), (TextView) findViewById(R.id.swm_heart_rate));

        mMenuController = new MenuController(this, findViewById(R.id.swm_menu));
        findViewById(R.id.swm_menu_icon).setOnClickListener(this);
        findViewById(R.id.swm_menu_ecg).setOnClickListener(this);
        findViewById(R.id.swm_menu_hrv).setOnClickListener(this);
        findViewById(R.id.swm_menu_motion).setOnClickListener(this);
        findViewById(R.id.swm_menu_ble).setOnClickListener(this);
    }

    @Override
    public void onSdnnAvailable(final Float sdnn) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSdnnLineChart.offerData(sdnn);
                mSdnnValue.setText(String.valueOf(sdnn) + " ms");
            }
        });

    }

    @Override
    public void onRmssdAvailable(final Float rmssd) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRmssdLineChart.offerData(rmssd);
                mRmssdValue.setText(String.valueOf(rmssd) + " ms");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mSwmBinder != null)
            mSwmBinder.stopMonitorHrv();
        unbindService(mConnection);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mSwmBinder != null) {
            mSwmBinder.removeHeartRateListener(this);
        }
        mSwmBinder.removeRriDistributionListener();
        mSwmBinder.removeSdnnListener();
        mSwmBinder.removeRmssdListener();
        mSwmBinder.removeRriFreqListener();
        mHeartBeatSound.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSwmBinder != null){
            try {
                mSwmBinder.registerHeartRateListener(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mSwmBinder.setRriDistributionListener(this);
            mSwmBinder.setSdnnListener(this);
            mSwmBinder.setRmssdListener(this);
            mSwmBinder.setRriFreqListener(this);
        }
    }

    @Override
    public void onHeartRateDataAvailable(final HeartRateData heartRateData) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mHeartBeatSound.onHeartRateDataAvailable(heartRateData);
                mHeartBeatHandler.onHeartBeat(heartRateData.heartRate);

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
    public void onRriDistributionChanged(final double[] binAry, final double[] binIdx) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRriDistributionHistogram.offerValue(binAry, binIdx);
            }
        });
    }

    @Override
    public void onFrequencyDataAvailable(final double[] frequencyData) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mVeryLowFrequency.setText(String.valueOf(Math.round(frequencyData[0] * 100.0) / 100.0));
                mLowFrequency.setText(String.valueOf(Math.round(frequencyData[1] * 100.0) / 100.0));
                mHighFrequency.setText(String.valueOf(Math.round(frequencyData[2] * 100.0) / 100.0));
                mTotalPower.setText(String.valueOf(Math.round(frequencyData[3] * 100.0) / 100.0));
                mLfHfRatio.setText(String.valueOf(Math.round(frequencyData[4] * 100.0) / 100.0));
            }
        });

        mSignalGenerator.offerData(frequencyData);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.swm_menu_icon:
                mMenuController.onToggle();
                break;
            case R.id.swm_menu_ecg:
            case R.id.swm_menu_hrv:
            case R.id.swm_menu_motion:
            case R.id.swm_menu_ble:
                mMenuController.onSwitchMode(v.getId());
                finish();
                break;
        }
    }
}
