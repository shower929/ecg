package com.swm.core;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.TextView;

import com.swm.chart.RealtimeHistogram;
import com.swm.chart.RealtimeLineChart;
import com.swm.heart.R;
import com.swm.heart.SwmBaseActivity;
import com.swm.heartbeat.HeartBeatHandler;
import com.swm.heartbeat.HeartBeatListener;
import com.swm.heartbeat.HeartBeatSound;
import com.swm.hrv.HrvListener;

public class RriActivity extends SwmBaseActivity implements HrvListener
                                                    , HeartBeatListener{
    RealtimeHistogram mRriHistogram;
    SwmBinder mSwmBinder;
    SwitchController mSwitchController;
    private HeartBeatSound mHeartBeatSound;
    private HeartBeatHandler mHeartBeatHandler;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mSwmBinder = (SwmBinder) service;

            try {
                mSwmBinder.registerHeartRateListener(RriActivity.this);
                mSwmBinder.registerHrvListener(RriActivity.this);
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
        setContentView(R.layout.activity_rri);
        mRriHistogram = (RealtimeHistogram) findViewById(R.id.swm_rri);

        mSwitchController = new SwitchController(this, findViewById(R.id.swm_hrv_switch));
        mHeartBeatSound = new HeartBeatSound(this);
        mHeartBeatHandler = new HeartBeatHandler(findViewById(R.id.swm_heart), (TextView) findViewById(R.id.swm_heart_rate));
    }

    @Override
    public void onHrvDataAvailable(final HrvData hrvData) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRriHistogram.offerData(hrvData.rriData);
            }
        });

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
            mSwmBinder.removeHrvListener(this);
        }
        mHeartBeatSound.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mSwmBinder != null)
            try {
                mSwmBinder.registerHeartRateListener(this);
                mSwmBinder.registerHrvListener(this);
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
}
