package com.swm.core;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.swm.heart.R;
import com.swm.heart.SwmBaseActivity;
import com.swm.hrv.HrvListener;

public class RmssdActivity extends SwmBaseActivity implements HrvListener{
    LineChartView mLineChart;
    TextView mRmssdValue;
    SwmBinder mSwmBinder;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mSwmBinder = (SwmBinder) service;

            try {
                mSwmBinder.registerHrvListener(RmssdActivity.this);
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
        setContentView(R.layout.activity_rmssd);
        mLineChart = (LineChartView) findViewById(R.id.swm_rmssd);
        mRmssdValue = (TextView) findViewById(R.id.swm_rmssd_value);
    }

    @Override
    public void onHrvDataAvailable(final HrvData hrvData) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRmssdValue.setText(String.valueOf(hrvData.rmssd) + " ms");
                mLineChart.offerValue(hrvData.rmssd);
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
        if(mSwmBinder != null)
            mSwmBinder.removeHrvListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mSwmBinder != null)
            try {
                mSwmBinder.registerHrvListener(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
