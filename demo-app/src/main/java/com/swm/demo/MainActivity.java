package com.swm.demo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.swm.stuff.view.CalorieView;
import com.swm.stuff.view.HeartRateView;
import com.swm.stuff.view.PhyAgeView;
import com.swm.stuff.view.StepView;
import com.swm.stuff.view.StressView;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "Demo";
    private SwmService.SwmBinder swmBinder;
    private Presenter presenter;
    private HeartRateView heartRateView;
    private CalorieView calorieView;
    private StressView stressView;
    private PhyAgeView phyAgeView;
    private StepView stepView;
    private DeviceStatus deviceStatus;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(LOG_TAG, "Connected to SWM service");
            swmBinder = (SwmService.SwmBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, SwmService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);

        setContentView(R.layout.activity_main);
        heartRateView = (HeartRateView) findViewById(R.id.swm_heart_rate);
        calorieView = (CalorieView) findViewById(R.id.swm_calorie);
        stressView = (StressView) findViewById(R.id.swm_stress);
        phyAgeView = (PhyAgeView) findViewById(R.id.swm_phy_age);
        stepView = (StepView) findViewById(R.id.swm_step);
        deviceStatus = (DeviceStatus) findViewById(R.id.ble_device_status);

        presenter = new Presenter(this, heartRateView, calorieView, stressView, phyAgeView, stepView, deviceStatus);
        getApplication().registerActivityLifecycleCallbacks(presenter);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getApplication().unregisterActivityLifecycleCallbacks(presenter);
        unbindService(connection);
    }
}
