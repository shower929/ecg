package com.swm.sdk;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.swm.chart.SwmLineChart;
import com.swm.core.view.LocationView;
import com.swm.motion.MotionListener;
import com.swm.heart.R;
import com.swm.sdk.HeartActivity;


public class MotionActivity extends AppCompatActivity implements MotionListener, View.OnClickListener {
    private SwmService.SwmBinder mSwmBinder;
    private TextView mGyro;
    private TextView mAccelerator;
    private TextView mMagnetic;
    private Button mRecordBtn;
    private boolean mRecording = false;
    private TextView packetLossView;
    private TextView throughputView;
    private MyLocationService mLocationService;
    private SwmLineChart acceleratorView;
    private SwmLineChart gyroView;
    private SwmLineChart magneticView;
    private LocationView locationView;

    private ServiceConnection mLocationConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyLocationService.LocationBinder locationBinder = (MyLocationService.LocationBinder) service;
            mLocationService = locationBinder.getService();
            mLocationService.setLocationListener(MotionActivity.this);
            mLocationService.startTracking();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mLocationService = null;
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mSwmBinder = (SwmService.SwmBinder) service;
            mSwmBinder.setMotionListener(MotionActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion);
        Intent intent = new Intent(this, SwmService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
        Intent locationIntent = new Intent(this, MyLocationService.class);
        bindService(locationIntent, mLocationConnection, BIND_AUTO_CREATE);

        acceleratorView = (SwmLineChart) findViewById(R.id.swm_accelerator);
        gyroView = (SwmLineChart) findViewById(R.id.swm_gyro);
        magneticView = (SwmLineChart) findViewById(R.id.swm_magnetic);
        locationView = (LocationView) findViewById(R.id.swm_location);

        presenter = new MotionPresenter(acceleratorView, gyroView, magneticView, locationView);
        mRecordBtn = (Button) findViewById(R.id.swm_record);
        mRecordBtn.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mSwmBinder != null)
            mSwmBinder.setMotionListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSwmBinder != null)
            mSwmBinder.removeMotionListener();
    }

    @Override
    public void onMotionDataAvailable(final MotionData motionData) {
        //Log.i(LOG_TAG, );
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mGyro.setText("(" + motionData.gyro.x + ", " + motionData.gyro.y + ", " + motionData.gyro.z + ")");
                mAccelerator.setText("(" + motionData.accelerator.x + ", " + motionData.accelerator.y + ", " + motionData.accelerator.z + ")");
                mMagnetic.setText("(" + motionData.magnetic.x + ", " + motionData.magnetic.y + ", " + motionData.magnetic.z + ")");

            }
        });
        }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocationService != null)
            mLocationService.stopTracking();
        unbindService(mConnection);
        unbindService(mLocationConnection);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.swm_record:
                if (!mRecording) {
                    SwmCore.getIns().getMotionService().startRecord();
                    mRecording = true;
                } else {
                    mRecordBtn.setText(getString(R.string.swm_record));
                    mRecordBtn.setBackground(getResources().getDrawable(R.drawable.swm_cta_button));
                    SwmCore.getIns().getMotionService().stopRecord();
                    mRecording = false;
                }
                presenter.onClick(v);
                break;
        }
    }

    @Override
    public void onLocationUpdate(Location location) {
        mLocationView.setText(getString(R.string.swm_location, location.getLatitude(), location.getLongitude()) + ", " + location.getAccuracy() + ", " + location.getTime());
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mLocationService != null)
            mLocationService.setLocationListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mLocationService != null)
            mLocationService.removeLocationListener();
    }


}
