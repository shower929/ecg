package com.swm.core;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.swm.motion.MotionListener;
import com.swm.heart.R;


public class MotionActivity extends AppCompatActivity implements MotionListener, View.OnClickListener {
    private SwmBinder mSwmBinder;
    private TextView mGyro;
    private TextView mAccelerator;
    private TextView mMagnetic;
    private Button mRecordBtn;
    private boolean mRecording = false;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mSwmBinder = (SwmBinder) service;
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
        mGyro = (TextView) findViewById(R.id.swm_gyro);
        mAccelerator = (TextView) findViewById(R.id.swm_accelerator);
        mMagnetic = (TextView) findViewById(R.id.swm_magnetic);
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
        unbindService(mConnection);
    }

    @Override
    public void onClick(View v) {
        if (!mRecording) {
            mRecordBtn.setText(getString(R.string.swm_stop));
            mRecordBtn.setBackground(getResources().getDrawable(R.drawable.swm_stop_button));
            SwmCore.getIns().getMotionService().startRecord();
            mRecording = true;
        } else {
            mRecordBtn.setText(getString(R.string.swm_record));
            mRecordBtn.setBackground(getResources().getDrawable(R.drawable.swm_cta_button));
            SwmCore.getIns().getMotionService().stopRecord();
            mRecording = false;
        }
    }
}
