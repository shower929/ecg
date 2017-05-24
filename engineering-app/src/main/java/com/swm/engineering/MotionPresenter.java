package com.swm.engineering;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;

import com.google.android.gms.location.LocationResult;
import com.swm.engineering.app.R;
import com.swm.sdk.CaloriePlugin;
import com.swm.sdk.RunningPlugin;
import com.swm.stuff.view.CalorieView;
import com.swm.stuff.view.JumpView;
import com.swm.stuff.view.LocationView;
import com.swm.stuff.view.StepView;

import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by yangzhenyu on 2017/4/28.
 */

public class MotionPresenter extends Presenter {
    private Activity myActivity;
    private IntentFilter intentFilter;
    private BroadcastReceiver broadcastReceiver;

    private final StepView stepView;
    private final LocationView locationView;
    private final CalorieView calorieView;
    private final JumpView jumpView;
    private Handler handler;
    private Runnable falling;

    private int stepCount;

    private MyLocationService mLocationService;
    private View parent;

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
        myActivity = activity;
        parent = view;

        locationView = (LocationView) view.findViewById(R.id.swm_location_view);
        calorieView = (CalorieView) view.findViewById(R.id.swm_calorie_view);
        stepView = (StepView) view.findViewById(R.id.swm_step_view);
        jumpView = (JumpView) view.findViewById(R.id.swm_jump_view);

        handler = new Handler();
        falling = new Runnable() {
            @Override
            public void run() {
                jumpView.setStatus(myActivity.getString(R.string.swm_steady));
            }
        };
    }

    @Override
    public void show() {
        onStart();
    }


    @Override
    public void hide() {
        onStop();
    }


    private void registerBroadcastReceiver() {
        intentFilter = new IntentFilter(RunningPlugin.ACTION_STEP);
        intentFilter.addAction(RunningPlugin.ACTION_JUMP);
        intentFilter.addAction(CaloriePlugin.ACTION_CALORIE_AVAILABLE);
        intentFilter.addAction(MyLocationService.ACTION_LAST_LOCATION);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(RunningPlugin.ACTION_STEP))
                    handleStep(intent);
                else if (intent.getAction().equals(RunningPlugin.ACTION_JUMP))
                    handleJump(intent);
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
    public void onStart() {
        Intent locationIntent = new Intent(myActivity, MyLocationService.class);
        myActivity.bindService(locationIntent, mLocationConnection, BIND_AUTO_CREATE);
        registerBroadcastReceiver();
        parent.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStop() {
        unregisterbroadcastReceiver();
        if (mLocationService != null)
            mLocationService.stopTracking();

        myActivity.unbindService(mLocationConnection);
        parent.setVisibility(View.GONE);

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

    private void handleJump(Intent intent) {
        int jump = intent.getIntExtra(RunningPlugin.EXTRA_JUMP, 0);
        if (jump == RunningPlugin.JUMP) {
            jumpView.setStatus(myActivity.getString(R.string.swm_jump));
            fall();
        }
    }

    private void fall() {
        handler.removeCallbacks(falling);
        handler.postDelayed(falling, 1000);
    }
}
