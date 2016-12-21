package com.swm.core;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.opengl.GLSurfaceView;
import android.os.IBinder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.swm.breath.BreathListener;
import com.swm.heart.R;
import com.swm.heart.SwmBaseActivity;
import com.swm.heartbeat.HeartBeatListener;
import com.swm.heartbeat.HeartBeatSound;
import com.swm.hrv.HrvListener;
import com.swm.motion.MotionListener;

public class CompositeActivity extends SwmBaseActivity implements EcgListener
        , BreathListener
        , HeartBeatListener
        , MotionListener
        , View.OnClickListener
        , MyLocationService.LocationListener
        , TimerListener
        , ProfilingListener
        , HrvListener{
    private static final String LOG_TAG = "ECG";
    private SwmBinder mSwmBinder;
    private CompositeView mCompositeView;

    private int mMaxHeartRate;
    private int mMinHeartRate = Integer.MAX_VALUE;
    private TextView mBreathRateView;
    private TextView mHeartRateView;
    private TextView mMaxHeartRateView;
    private TextView mMinHeartRateView;
    private TextView mASensorX;
    private TextView mASensorY;
    private TextView mASensorZ;
    private TextView mGyroX;
    private TextView mGyroY;
    private TextView mGyroZ;
    private TextView mMagneticX;
    private TextView mMagneticY;
    private TextView mMagneticZ;
    private TextView mLocationView;
    private TextView mElapseView;
    private TextView mPacketLossView;
    private TextView mThroughputView;
    private TextView mByteErrorRateView;
    private TextView mSdnnView;
    private TextView mRmssdView;

    private HeartBeatSound mHeartBeatSound;
    private Button mRecordBtn;
    private MyLocationService mLocationService;

    private ServiceConnection mLocationConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyLocationService.LocationBinder locationBinder = (MyLocationService.LocationBinder) service;
            mLocationService = locationBinder.getService();
            mLocationService.setLocationListener(CompositeActivity.this);
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
            mSwmBinder = (SwmBinder) service;
            Log.i(LOG_TAG, "Connected to SWM service");
            try {
                mSwmBinder.registerEcgListener(CompositeActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mSwmBinder.setBreathListener(CompositeActivity.this);
            try {
                mSwmBinder.registerHeartRateListener(CompositeActivity.this);
                mSwmBinder.registerHrvListener(CompositeActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mSwmBinder.setMotionListener(CompositeActivity.this);
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

        Intent locationIntent = new Intent(this, MyLocationService.class);
        boolean success = bindService(locationIntent, mLocationConnection, BIND_AUTO_CREATE);

        setContentView(R.layout.activity_composite);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mCompositeView = (CompositeView) findViewById(R.id.swm_composite_view);
        mHeartRateView = (TextView) findViewById(R.id.swm_current_heart_rate);
        mMaxHeartRateView = (TextView) findViewById(R.id.swm_max_heart_rate);
        mMinHeartRateView = (TextView) findViewById(R.id.swm_min_heart_rate);
        mBreathRateView = (TextView) findViewById(R.id.swm_breath_rate_value);
        mASensorX = (TextView) findViewById(R.id.swm_accelerator_x);
        mASensorY = (TextView) findViewById(R.id.swm_accelerator_y);
        mASensorZ = (TextView) findViewById(R.id.swm_accelerator_z);
        mGyroX = (TextView) findViewById(R.id.swm_gyro_x);
        mGyroY = (TextView) findViewById(R.id.swm_gyro_y);
        mGyroZ = (TextView) findViewById(R.id.swm_gyro_z);
        mMagneticX = (TextView) findViewById(R.id.swm_magnetic_x);
        mMagneticY = (TextView) findViewById(R.id.swm_magnetic_y);
        mMagneticZ = (TextView) findViewById(R.id.swm_magnetic_z);

        mLocationView = (TextView) findViewById(R.id.swm_location);
        mElapseView = (TextView) findViewById(R.id.swm_record_elpase);
        mPacketLossView = (TextView) findViewById(R.id.swm_packetloss_view);
        mThroughputView = (TextView) findViewById(R.id.swm_throughput_view);
        mByteErrorRateView = (TextView) findViewById(R.id.swm_byte_error_rate_view);
        mSdnnView = (TextView) findViewById(R.id.swm_sdnn_value_view);
        mRmssdView = (TextView) findViewById(R.id.swm_rmssd_value_view);
        mHeartBeatSound = new HeartBeatSound(this);
        mRecordBtn = (Button) findViewById(R.id.swm_record);
        mRecordBtn.setOnClickListener(this);
    }


    @Override
    public void onBreathDataAvailable(final BreathData breathData) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBreathRateView.setText(String.valueOf(breathData.breathRate));
            }
        });

    }

    @Override
    public void onEcgDataAvailable(EcgData ecgData) {
        mCompositeView.putEcgData(ecgData);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSwmBinder != null) {
            try {
                mSwmBinder.registerEcgListener(this);
                mSwmBinder.registerHeartRateListener(this);
                mSwmBinder.registerHrvListener(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mSwmBinder.setBreathListener(this);
            mSwmBinder.setMotionListener(this);

        }

        if (isRecording()) {
            mRecordBtn.setText(getString(R.string.swm_stop));
            mRecordBtn.setBackground(getResources().getDrawable(R.drawable.swm_stop_button));
            mElapseView.setVisibility(View.VISIBLE);
            SwmCore.getIns().setTimerListener(this);
        } else {
            mRecordBtn.setText(getString(R.string.swm_record));
            mRecordBtn.setBackground(getResources().getDrawable(R.drawable.swm_cta_button));
            mElapseView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mSwmBinder != null) {
            mSwmBinder.removeEcgListener(this);
            mSwmBinder.removeBreathListener();
            mSwmBinder.removeHeartRateListener(this);
            mSwmBinder.removeHrvListener(this);
            mSwmBinder.removeMotionListener();
        }
        mHeartBeatSound.release();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SwmCore.getIns().removeProfilingListener();
        if (mLocationService != null)
            mLocationService.removeLocationListener();

    }

    @Override
    protected void onStart() {
        super.onStart();
        SwmCore.getIns().setProfilingListener(this);
        if (mLocationService != null)
            mLocationService.setLocationListener(this);

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
    public void onHeartBeatDataAvailable(final HeartBeatData heartBeatData) {
        if(heartBeatData.heartRate == 0)
            return;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mHeartRateView.setText(String.valueOf(heartBeatData.heartRate));
                if(heartBeatData.heartRate > mMaxHeartRate) {
                    mMaxHeartRate = heartBeatData.heartRate;
                    mMaxHeartRateView.setText(String.valueOf(mMaxHeartRate));
                }
                if (heartBeatData.heartRate < mMinHeartRate) {
                    mMinHeartRate = heartBeatData.heartRate;
                    mMinHeartRateView.setText(String.valueOf(mMinHeartRate));
                }
            }
        });

        mHeartBeatSound.onHeartBeatDataAvailable(heartBeatData);
    }

    @Override
    public void onClick(View v) {
        if (!isRecording()) {
            mRecordBtn.setText(getString(R.string.swm_stop));
            mRecordBtn.setBackground(getResources().getDrawable(R.drawable.swm_stop_button));
            SwmCore.getIns().setTimerListener(this);
            SwmCore.getIns().startRecord();
            mLocationService.startRecording();

            mElapseView.setText("00:00:00");
            mElapseView.setVisibility(View.VISIBLE);
        } else {
            mRecordBtn.setText(getString(R.string.swm_record));
            mRecordBtn.setBackground(getResources().getDrawable(R.drawable.swm_cta_button));
            SwmCore.getIns().removeTimerListener(this);
            SwmCore.getIns().stopRecord();
            mLocationService.stopRecording();

            mElapseView.setVisibility(View.INVISIBLE);
        }
    }

    private boolean isRecording() {
        return SwmCore.getIns().isRecording()
                && mLocationService.isRecording();
    }

    @Override
    public void onMotionDataAvailable(final MotionData motionData) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mASensorX.setText(String.valueOf(motionData.accelerator.x));
                mASensorY.setText(String.valueOf(motionData.accelerator.y));
                mASensorZ.setText(String.valueOf(motionData.accelerator.z));

                mGyroX.setText(String.valueOf(motionData.gyro.x));
                mGyroY.setText(String.valueOf(motionData.gyro.y));
                mGyroZ.setText(String.valueOf(motionData.gyro.z));

                mMagneticX.setText(String.valueOf(motionData.magnetic.x));
                mMagneticY.setText(String.valueOf(motionData.magnetic.y));
                mMagneticZ.setText(String.valueOf(motionData.magnetic.z));
            }
        });

    }

    @Override
    public void onLocationUpdate(Location location) {
        mLocationView.setText(getString(R.string.swm_location, location.getLatitude(), location.getLongitude()) + ", " + location.getAccuracy() + ", " + location.getTime());
    }

    @Override
    public void onTick(String tock) {
        mElapseView.setText(tock);
    }

    @Override
    public void onPacketLoss(final double rate) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPacketLossView.setText(String.valueOf(rate) + "%");
            }
        });
    }

    @Override
    public void onThroughput(final double bps) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mThroughputView.setText(String.valueOf(bps) + " bps");
            }
        });
    }

    @Override
    public void onByteError(final double ber) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mByteErrorRateView.setText(String.valueOf(ber) + "%");
            }
        });
    }

    @Override
    public void onHrvDataAvailable(final HrvData hrvData) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSdnnView.setText(String.valueOf(hrvData.sdnn) + " ms");
                mRmssdView.setText(String.valueOf(hrvData.rmssd) + " ms");
            }
        });
    }
}
