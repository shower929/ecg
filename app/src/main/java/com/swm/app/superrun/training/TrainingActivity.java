package com.swm.app.superrun.training;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.swm.app.superrun.power.RunPowerMeterHandler;
import com.swm.app.superrun.power.SwmMeter;
import com.swm.core.HeartRateData;
import com.swm.core.MyLocationService;
import com.swm.core.SwmBinder;
import com.swm.core.SwmService;
import com.swm.device.SwmDeviceListener;
import com.swm.heart.R;
import com.swm.heart.SwmBaseActivity;
import com.swm.heartbeat.HeartBeatHandler;
import com.swm.heartbeat.HeartRateListener;
import com.swm.report.Reporter;
import com.swm.training.SwmBar;
import com.swm.training.TrainingListener;
import com.swm.training.TrainingModel;

import static com.swm.app.superrun.training.TrainingModelActivity.KEY_TRAINING_MODEL;

public class TrainingActivity extends SwmBaseActivity implements MyLocationService.LocationListener
                                                    , HeartRateListener
                                                    , SwmDeviceListener
                                                    , View.OnClickListener
                                                    , TrainingListener{
    private static final String LOG_TAG = "Training";
    private SwmBar mDistanceBar;
    private SwmBar mElapseBar;
    private SwmBar mPaceBar;
    private SwmBar mStepBar;
    private static final int STRIDE = 50;
    private float mDistance = 0;
    private Location mCurrentLocation;
    private long mElapseTime;
    private SwmBinder mSwmBinder;

    private MyLocationService mLocationService;
    private HeartBeatHandler mHeartBeatHandler;
    private static final int ONE_MINUTE = 60 * 1000;
    private AlertDialog mGpsDialog;
    private Handler mHandler;
    private Runnable mTimer;
    private TextView mCountDownView;
    private int mCount = 10;
    private Runnable mCountDown;
    private View mCover;
    private View mMain;
    private RunPowerMeterHandler mMeterHandler;
    private View mPauseCover;
    private ImageView mPauseView;
    private TextView mIntensity;
    private boolean mPausing = false;
    private boolean mShowingPauseCover = false;
    private Animator mIntensityColorAnimator;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mSwmBinder = (SwmBinder) service;

            try {
                mSwmBinder.registerHeartRateListener(TrainingActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mSwmBinder.setDeviceListener(TrainingActivity.this);
            mSwmBinder.setTrainingListener(TrainingActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private ServiceConnection mLocationConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyLocationService.LocationBinder locationBinder = (MyLocationService.LocationBinder) service;
            mLocationService = locationBinder.getService();
            mLocationService.setLocationListener(TrainingActivity.this);
            mLocationService.startTracking();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mLocationService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        TrainingModel model = intent.getParcelableExtra(KEY_TRAINING_MODEL);

        Reporter.reportStartTraining(this);

        Intent swmServiceInent = new Intent(this, SwmService.class);
        bindService(swmServiceInent, mConnection, BIND_AUTO_CREATE);

        Intent locationIntent = new Intent(this, MyLocationService.class);
        boolean success = bindService(locationIntent, mLocationConnection, BIND_AUTO_CREATE);

        setContentView(R.layout.activity_coach);
        mIntensity = (TextView) findViewById(R.id.swm_intensity_value);
        mMain = findViewById(R.id.activity_coach);
        mMain.setOnClickListener(this);
        mCountDownView = (TextView) findViewById(R.id.swm_timer);
        mCover = findViewById(R.id.swm_training_cover);
        Button cta = (Button) findViewById(R.id.swm_cta);
        cta.setOnClickListener(this);
        findViewById(R.id.swm_cancel).setOnClickListener(this);

        mCount = 10;
        mHandler = new Handler();
        mCountDown = new Runnable() {
            @Override
            public void run() {
                mCount--;
                mCountDownView.setText(String.valueOf(mCount));
                if (mCount > 0)
                    mHandler.postDelayed(this, 1000);
                else {
                    startTraining();
                }
            }
        };
        mHandler.postDelayed(mCountDown, 1000);

        // Pause
        mPauseCover = findViewById(R.id.swm_training_pause_cover);
        mPauseView = (ImageView) findViewById(R.id.swm_pause);
        mPauseView.setOnClickListener(this);
        findViewById(R.id.swm_stop).setOnClickListener(this);

        mDistanceBar = (SwmBar) findViewById(R.id.swm_distance_bar);
        mElapseBar = (SwmBar) findViewById(R.id.swm_duration_bar);


        TextView stepView = (TextView) findViewById(R.id.swm_step);
        mPaceBar = (SwmBar) findViewById(R.id.swm_speed_bar);
        mStepBar = (SwmBar) findViewById(R.id.swm_step_bar);
        SwmMeter meter = (SwmMeter) findViewById(R.id.swm_run_power_meter);
        mMeterHandler = new RunPowerMeterHandler(meter, null);
        mMeterHandler.setMax(8);
        View heart = findViewById(R.id.swm_heart);
        mHeartBeatHandler = new HeartBeatHandler(heart);
        if (model.distance > 0) {
            TextView distanceView = (TextView) findViewById(R.id.swm_distance);
            distanceView.setText(String.valueOf(model.distance));
            mDistanceBar.setMaxValue(model.distance);
        } else {
            mDistanceBar.setMaxValue(50 * 1000);
        }

        if (model.duration > 0) {
            TextView durationView = (TextView) findViewById(R.id.swm_duration);
            durationView.setText(String.valueOf(model.duration));
            mElapseBar.setMaxValue(model.duration);
        } else {
            mElapseBar.setMaxValue(3 * 60 * 60);  // 3 hours
        }

        mTimer = new Runnable(){

            @Override
            public void run() {
                mElapseTime+=1;
                mElapseBar.setValue(mElapseTime);
                mElapseBar.invalidate();
                mHandler.postDelayed(mTimer, 1000);
            }
        };

        mHandler.postDelayed(mTimer, 1000);
    }

    private void startTraining() {
        mCover.setVisibility(View.GONE);
    }

    private boolean isBetterLocation(Location location, Location currentLocation) {
        long timeDelta = location.getTime() - currentLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > ONE_MINUTE;
        boolean isSignificantlyOlder = timeDelta < -ONE_MINUTE;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        int accuracyDelta = (int) (location.getAccuracy() - currentLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 1;

        boolean isFromSameProvider = isSameProvider(location.getProvider(), currentLocation.getProvider());

        if(isMoreAccurate)
            return true;
        else if(isNewer && !isLessAccurate)
            return true;
        else if(isNewer && !isSignificantlyLessAccurate && isFromSameProvider)
            return true;

        return false;
    }

    private boolean isSameProvider(String provider1, String provider2) {
        return TextUtils.equals(provider1, provider2);
    }

    @Override
    public void onLocationUpdate(Location location) {
        if(mCurrentLocation == null) {
            mCurrentLocation = location;
            return;
        }

        if(!isBetterLocation(location, mCurrentLocation))
            return;

        float distance = location.distanceTo(mCurrentLocation);
        if (distance <= 0)
            return;

        mCurrentLocation = location;
        mDistance += distance;
        mDistanceBar.setValue(mDistance);
        mDistanceBar.invalidate();
        mPaceBar.setValue((float)mElapseTime / 60f / mDistance * 1000);
        mPaceBar.invalidate();
        mSwmBinder.logLocation(location);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocationService != null) {
            mLocationService.stopTracking();
            unbindService(mLocationConnection);
        }
        if (mSwmBinder != null) {
            mSwmBinder.removeHeartRateListener(this);
            mSwmBinder.removeTrainingListener();
            mSwmBinder.stopSport();
            unbindService(mConnection);
        }
        mHandler.removeCallbacks(mTimer);
        mHandler.removeCallbacks(mCountDown);
    }

    @Override
    public void onHeartRateDataAvailable(final HeartRateData heartRateData) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMeterHandler.setHeartRate(heartRateData.heartRate);
                mHeartBeatHandler.onHeartBeat(heartRateData.heartRate);
            }
        });
    }

    @Override
    public void onConnectStateChanged(int state) {
        if (state == SwmDeviceListener.CONNECTED){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMeterHandler.sendEmptyMessage(RunPowerMeterHandler.MSG_TURNON);
                }
            });
        }

        if (state == SwmDeviceListener.DISCONNECTED){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMeterHandler.sendEmptyMessage(RunPowerMeterHandler.MSG_TURNOFF);
                    Toast.makeText(TrainingActivity.this, getString(R.string.swm_disconnect_msg), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mGpsDialog != null && mGpsDialog.isShowing())
            mGpsDialog.cancel();

    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            int locationOnOff = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
            if (locationOnOff == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.swm_gps_title))
                        .setMessage(getString(R.string.swm_gps_message))
                        .setCancelable(false)
                        .setPositiveButton(R.string.swm_cta_go, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent optGps = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(optGps);
                            }
                        });
                mGpsDialog = builder.create();
                mGpsDialog.show();
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void showPauseCover() {
        if (mShowingPauseCover)
            return;

        mShowingPauseCover = true;

        mPauseCover.setVisibility(View.VISIBLE);
        startShowPauseCountDown();
    }

    private void startShowPauseCountDown() {
        mCount = 3;
        mCountDown = new Runnable() {
            @Override
            public void run() {
                mCount--;

                if(mCount > 0)
                    mHandler.postDelayed(this, 1000);
                else {
                    hidePauseCover();
                }
            }
        };
        mHandler.postDelayed(mCountDown, 1000);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.activity_coach) {
            showPauseCover();
        }

        if (v.getId() == R.id.swm_cta) {
            mHandler.removeCallbacks(mCountDown);
            startTraining();
        }

        if (v.getId() == R.id.swm_cancel) {
            mHandler.removeCallbacks(mCountDown);
            finish();
        }

        if (v.getId() == R.id.swm_pause) {
            if (!mPausing) {
                mPausing = true;
                pauseTraining();
            } else {
                mPausing = false;
                resumeTraining();
            }
        }

        if (v.getId() == R.id.swm_stop) {
            //Intent intent = new Intent(this, TrainingResultActivity.class);
            finish();
        }
    }

    void pauseTraining() {
        mHandler.removeCallbacks(mTimer);
        mHandler.removeCallbacks(mCountDown);
        mPauseView.setImageDrawable(getResources().getDrawable(R.drawable.swm_play));
    }

    void resumeTraining() {
        startShowPauseCountDown();
        mHandler.postDelayed(mTimer, 1000);
        mPauseView.setImageDrawable(getResources().getDrawable(R.drawable.swm_pause));
    }

    @Override
    public void onBackPressed() {
        if (mShowingPauseCover)
            hidePauseCover();
        else {
            showPauseCover();
        }
    }

    private void hidePauseCover() {
        mPauseCover.setVisibility(View.GONE);
        mShowingPauseCover = false;
    }

    @Override
    public void onTrainingIntensityChanged(final int newIntensity) {

        mMeterHandler.setMainValue(newIntensity);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (mIntensityColorAnimator != null && mIntensityColorAnimator.isStarted()) {
                    mIntensityColorAnimator.cancel();
                }
                ArgbEvaluator evaluator = new ArgbEvaluator();

                if (newIntensity == INTENSITY_NONE) {
                    mIntensity.setText(getString(R.string.swm_intensity_none));
                    mIntensityColorAnimator = ObjectAnimator.ofObject(mIntensity, "textColor", evaluator, mIntensity.getCurrentTextColor(), getResources().getColor(R.color.swm_white));
                } else if (newIntensity == INTENSITY_E) {
                    mIntensity.setText(getString(R.string.swm_intensity_easy));
                    mIntensityColorAnimator = ObjectAnimator.ofObject(mIntensity, "textColor", evaluator, mIntensity.getCurrentTextColor(), getResources().getColor(R.color.swm_intensity_easy));
                } else if (newIntensity == INTENSITY_M) {
                    mIntensity.setText(getString(R.string.swm_intensity_normal));
                    mIntensityColorAnimator = ObjectAnimator.ofObject(mIntensity, "textColor", evaluator, mIntensity.getCurrentTextColor(), getResources().getColor(R.color.swm_intensity_normal));
                } else if (newIntensity == INTENSITY_T) {
                    mIntensity.setText(getString(R.string.swm_intensity_strong));
                    mIntensityColorAnimator = ObjectAnimator.ofObject(mIntensity, "textColor", evaluator, mIntensity.getCurrentTextColor(), getResources().getColor(R.color.swm_intensity_strong));
                } else if (newIntensity == INTENSITY_I) {
                    mIntensity.setText(getString(R.string.swm_intensity_heavy));
                    mIntensityColorAnimator = ObjectAnimator.ofObject(mIntensity, "textColor", evaluator, mIntensity.getCurrentTextColor(), getResources().getColor(R.color.swm_intensity_heavy));
                } else if (newIntensity == INTENSITY_10K) {
                    mIntensity.setText(getString(R.string.swm_intensity_heavy));
                    mIntensityColorAnimator = ObjectAnimator.ofObject(mIntensity, "textColor", evaluator, mIntensity.getCurrentTextColor(), getResources().getColor(R.color.swm_intensity_heavy));
                } else if (newIntensity == INTENSITY_R) {
                    mIntensity.setText(getString(R.string.swm_intensity_hard));
                    mIntensityColorAnimator = ObjectAnimator.ofObject(mIntensity, "textColor", evaluator, mIntensity.getCurrentTextColor(), getResources().getColor(R.color.swm_intensity_hard));
                } else if (newIntensity == DANGER) {
                    mIntensity.setText(getString(R.string.swm_danger));
                    mIntensityColorAnimator = ObjectAnimator.ofObject(mIntensity, "textColor", evaluator, mIntensity.getCurrentTextColor(), getResources().getColor(R.color.swm_intensity_danger));
                }

                if (mIntensityColorAnimator != null) {
                    mIntensityColorAnimator.setDuration(500);
                    mIntensityColorAnimator.start();
                }
            }
        });

    }
}
