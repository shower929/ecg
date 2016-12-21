package com.swm.heart.accelerator;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.swm.accelerator.AcceleratorData;
import com.swm.accelerator.AcceleratorListener;
import com.swm.core.SwmBinder;
import com.swm.core.SwmService;
import com.swm.heart.R;

public class AcceleratorActivity extends AppCompatActivity implements AcceleratorListener {
    private SwmBinder mSwmBinder;
    private static final String LOG_TAG = "Accelerator";

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mSwmBinder = (SwmBinder) service;
            Log.i(LOG_TAG, "Connected to ECG service");
            mSwmBinder.setAcceleratorListener(AcceleratorActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerator);
        Intent intent = new Intent(this, SwmService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSwmBinder.setAcceleratorListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSwmBinder.removeAcceleratorListener();
    }

    @Override
    public void onAcceleratorDataAvailable(AcceleratorData acceleratorData) {
        Log.d(LOG_TAG, "Accelerator data: x: " + acceleratorData.x + ", y: " + acceleratorData.y + ", z: " + acceleratorData.z);
    }
}
