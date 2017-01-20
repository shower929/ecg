package com.swm.core;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.TextView;

import com.swm.chart.RealtimeHistogram;
import com.swm.heart.R;
import com.swm.heart.SwmBaseActivity;
import com.swm.heartbeat.HeartBeatHandler;
import com.swm.heartbeat.HeartRateListener;
import com.swm.heartbeat.HeartRateSound;
import com.swm.hrv.RriDistributionListener;

public class RriDistributionActivity extends SwmBaseActivity implements RriDistributionListener
                                                    , HeartRateListener {
    RealtimeHistogram mRriDistributionHistogram;

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
                mSwmBinder.registerHeartRateListener(RriDistributionActivity.this);
                mSwmBinder.setRriDistributionListener(RriDistributionActivity.this);
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
        setContentView(R.layout.activity_rri_distribution);
        mRriDistributionHistogram = (RealtimeHistogram) findViewById(R.id.swm_rri_distribution);
        mSwitchController = new SwitchController(this, findViewById(R.id.swm_hrv_switch));
        mHeartBeatSound = new HeartRateSound(this);
        mHeartBeatHandler = new HeartBeatHandler(findViewById(R.id.swm_heart), (TextView) findViewById(R.id.swm_heart_rate));
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
            mSwmBinder.removeRriDistributionListener();
        }
        mHeartBeatSound.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mSwmBinder != null) {
            try {
                mSwmBinder.registerHeartRateListener(this);
                mSwmBinder.setRriDistributionListener(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
}
