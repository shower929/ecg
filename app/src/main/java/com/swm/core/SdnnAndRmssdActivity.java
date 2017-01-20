package com.swm.core;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Bundle;
import android.widget.TextView;

import com.swm.chart.RealtimeSingleLineChart;
import com.swm.heart.R;
import com.swm.heart.SwmBaseActivity;
import com.swm.heartbeat.HeartBeatHandler;
import com.swm.heartbeat.HeartRateListener;
import com.swm.heartbeat.HeartRateSound;
import com.swm.hrv.RmssdListener;
import com.swm.hrv.SdnnListener;

public class SdnnAndRmssdActivity extends SwmBaseActivity implements SdnnListener
                                                    , RmssdListener
                                                    , HeartRateListener {

    RealtimeSingleLineChart mSdnnLineChart;
    TextView mSdnnValue;

    RealtimeSingleLineChart mRmssdLineChart;
    TextView mRmssdValue;

    SwmBinder mSwmBinder;
    SwitchController mSwitchController;
    private HeartRateSound mHeartBeatSound;
    private HeartBeatHandler mHeartBeatHandler;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mSwmBinder = (SwmBinder) service;
            mSwmBinder.startMonitorHrv();
            try {
                mSwmBinder.registerHeartRateListener(SdnnAndRmssdActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }

            mSwmBinder.setSdnnListener(SdnnAndRmssdActivity.this);
            mSwmBinder.setRmssdListener(SdnnAndRmssdActivity.this);
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
        setContentView(R.layout.activity_sdnn_and_rmssd);
        mSdnnLineChart = (RealtimeSingleLineChart) findViewById(R.id.swm_sdnn);
        mSdnnValue = (TextView) findViewById(R.id.swm_sdnn_value);
        mRmssdLineChart = (RealtimeSingleLineChart) findViewById(R.id.swm_rmssd);
        mRmssdValue = (TextView) findViewById(R.id.swm_rmssd_value);
        mSwitchController = new SwitchController(this, findViewById(R.id.swm_hrv_switch));
        mHeartBeatSound = new HeartRateSound(this);
        mHeartBeatHandler = new HeartBeatHandler(findViewById(R.id.swm_heart), (TextView) findViewById(R.id.swm_heart_rate));
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
        mSwmBinder.removeSdnnListener();
        mSwmBinder.removeRmssdListener();
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
            mSwmBinder.setSdnnListener(this);
            mSwmBinder.setRmssdListener(this);
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
}
