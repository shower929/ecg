package com.swm.engineering;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.RadioGroup;

import com.google.android.gms.location.LocationResult;
import com.swm.chart.Oscilloscope;
import com.swm.engineering.app.R;
import com.swm.sdk.AcceleratorDataSource;
import com.swm.sdk.CaloriePlugin;
import com.swm.sdk.GyroDataSource;
import com.swm.sdk.MagneticDataSource;
import com.swm.sdk.MotionDataSource;
import com.swm.sdk.RunningPlugin;
import com.swm.stuff.view.CalorieView;
import com.swm.stuff.view.LocationView;
import com.swm.stuff.view.StepView;

import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by yangzhenyu on 2017/4/28.
 */

public class MotionPresenter extends Presenter implements RadioGroup.OnCheckedChangeListener{
    private Activity myActivity;
    private IntentFilter intentFilter;
    private BroadcastReceiver broadcastReceiver;
    private final Oscilloscope motionEngineView;
    private final StepView stepView;
    private final LocationView locationView;
    private final CalorieView calorieView;

    private static boolean mRecording = false;

    private int stepCount;

    private AcceleratorDataSource acceleratorDataSource;
    private GyroDataSource gyroDataSource;
    private MagneticDataSource magneticDataSource;

    private MotionDataSource currentDataSource;

    private MyLocationService mLocationService;

    private ServiceConnection mLocationConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyLocationService.LocationBinder locationBinder = (MyLocationService.LocationBinder) service;
            mLocationService = locationBinder.getService();
            mLocationService.startTracking();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mLocationService = null;
        }
    };

    MotionPresenter(Activity activity, View view) {
        this.myActivity = activity;
        motionEngineView = (Oscilloscope) view.findViewById(R.id.swm_motion_engine_view);
        locationView = (LocationView) view.findViewById(R.id.swm_location_view);
        calorieView = (CalorieView) view.findViewById(R.id.swm_calorie_view);
        stepView = (StepView) view.findViewById(R.id.swm_step_view);
        Intent locationIntent = new Intent(myActivity, MyLocationService.class);
        myActivity.bindService(locationIntent, mLocationConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void show() {
        onStart();
    }

    private void injectDataSource(MotionDataSource dataSource) {
        motionEngineView.addDataSource(dataSource.getXSource(), myActivity.getResources().getColor(R.color.swm_motion_x));
        motionEngineView.addDataSource(dataSource.getYSource(), myActivity.getResources().getColor(R.color.swm_motion_y));
        motionEngineView.addDataSource(dataSource.getZSource(), myActivity.getResources().getColor(R.color.swm_motion_z));
    }

    @Override
    public void hide() {
        onStop();
    }

    private void initDataSource() {
        acceleratorDataSource = new AcceleratorDataSource();
        gyroDataSource = new GyroDataSource();
        magneticDataSource = new MagneticDataSource();
    }

    private void registerBroadcastReceiver() {
        intentFilter = new IntentFilter(RunningPlugin.ACTION_STEP);
        intentFilter.addAction(CaloriePlugin.ACTION_CALORIE_AVAILABLE);
        intentFilter.addAction(MyLocationService.ACTION_LAST_LOCATION);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(RunningPlugin.ACTION_STEP))
                    handleStep(intent);
                else if (intent.getAction().equals(CaloriePlugin.ACTION_CALORIE_AVAILABLE))
                    handleCalorie(intent);
                else if (intent.getAction().equals(MyLocationService.ACTION_LAST_LOCATION)) {
                    handleLocationUpdate(intent);
                }

            }
        };
        myActivity.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void unregisterbroadcastReceiver() {
        myActivity.unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        if (checkedId == R.id.swm_accelerator_button)
            switchToAccelerator();
        else if(checkedId == R.id.swm_gyro_button)
            switchToGyro();
        else if(checkedId == R.id.swm_magnetic_button)
            switchToMagnetic();
    }

    private void resetView() {
        motionEngineView.removeDataSource(currentDataSource.getXSource());
        motionEngineView.removeDataSource(currentDataSource.getYSource());
        motionEngineView.removeDataSource(currentDataSource.getZSource());
    }

    private void switchToAccelerator() {
        resetView();
        injectDataSource(acceleratorDataSource);
    }

    private void switchToGyro() {
        resetView();
        injectDataSource(gyroDataSource);
    }

    private void switchToMagnetic() {
        resetView();
        injectDataSource(magneticDataSource);
    }

    @Override
    void onStart() {
        registerBroadcastReceiver();
        initDataSource();
    }

    @Override
    void onStop() {
        unregisterbroadcastReceiver();
        if (mLocationService != null)
            mLocationService.stopTracking();

        myActivity.unbindService(mLocationConnection);
    }

    public static class LocationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(!LocationResult.hasResult(intent))
                return;

            LocationResult locationResult = LocationResult.extractResult(intent);
            List<Location> locations = locationResult.getLocations();

            for (Location location : locations) {
                log(new LocationData(location.getLatitude(), location.getLongitude(), location.getAccuracy(), location.getTime()));
            }
        }
    }

    private static void log(LocationData locationData) {
        // @TODO
        // Implementation location data
    }

    private void handleLocationUpdate(Intent intent) {
        Location location = intent.getParcelableExtra(MyLocationService.EXTRA_LOCATION);
        locationView.setLocation(location);
    }

    private void handleCalorie(Intent intent) {
        float calorie = intent.getFloatExtra(CaloriePlugin.EXTRA_CALORIE, 0f);
        calorieView.setCalorie(calorie);
    }

    private void handleStep(Intent intent) {
        int step = intent.getIntExtra(RunningPlugin.EXTRA_STEP, 0);
        stepCount += step;
        stepView.setStep(stepCount);
    }
}
