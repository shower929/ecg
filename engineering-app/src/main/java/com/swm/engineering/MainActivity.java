package com.swm.engineering;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioGroup;

import com.swm.chart.Oscilloscope;
import com.swm.engineering.app.R;
import com.swm.filter.FirFilter;
import com.swm.filter.IirFilter;
import com.swm.filter.NotchFilter;
import com.swm.filter.RemoveBaselineWander;
import com.swm.sdk.SwmService;
import com.swm.stuff.view.RecordButton;
import com.swm.stuff.view.TimerView;

public class MainActivity extends AppCompatActivity {
    private MainPresenter mainPresenter;

    private static final int ECG_SAMPLE_RATE = 250;
    private View heartView;
    private Oscilloscope ecg;
    private OscilloscopeController oscilloscopeController;
    private IirFilter iirFilter;
    private FirFilter firFilter;
    private NotchFilter notchFilter;
    private RemoveBaselineWander removeBaselineWander;
    private HeartPresenter heartPresenter;

    private View motionView;
    private MotionPresenter motionPresenter;

    private View deviceView;
    private DevicePresenter devicePresenter;

    private RadioGroup switchEngine;

    // Record UI
    private TimerView timerView;
    private RecordButton recordButton;
    private RecordPresenter recordPresenter;

    private SwmService.SwmBinder mSwmBinder;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("Main", "Connected to SWM service");
            mSwmBinder = (SwmService.SwmBinder) service;
            recordPresenter.setModel(mSwmBinder.getHeartEngine(), mSwmBinder.getMotionEngine());
            mSwmBinder.getHeartEngine().setListener(oscilloscopeController);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, SwmService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        // Heart UI
        heartView = findViewById(R.id.swm_heart_view);
        ecg = (Oscilloscope) findViewById(R.id.swm_ecg_breath_view);
        oscilloscopeController = new OscilloscopeController(ecg);

        //iirFilter = new IirFilter(0.992);
        //oscilloscopeController.addFilter(iirFilter);

        notchFilter = new NotchFilter(ECG_SAMPLE_RATE);
        oscilloscopeController.addFilter(notchFilter);

        firFilter = new FirFilter(ECG_SAMPLE_RATE);
        oscilloscopeController.addFilter(firFilter);

        removeBaselineWander = new RemoveBaselineWander(ECG_SAMPLE_RATE);
        oscilloscopeController.addFilter(removeBaselineWander);

        heartPresenter = new HeartPresenter(this, heartView, oscilloscopeController);


        // Motion UI
        motionView = findViewById(R.id.swm_motion_view);
        motionPresenter = new MotionPresenter(this, motionView);

        // Ecg / motion switch
        switchEngine = (RadioGroup) findViewById(R.id.swm_switch_engine);

        // Device UI
        deviceView = findViewById(R.id.swm_device_view);
        devicePresenter = new DevicePresenter(this, deviceView);

        // Main UI
        mainPresenter = new MainPresenter(this, heartPresenter, motionPresenter);
        switchEngine.setOnCheckedChangeListener(mainPresenter);
        getApplication().registerActivityLifecycleCallbacks(mainPresenter);

        timerView = (TimerView) findViewById(R.id.swm_timer_view);
        recordButton = (RecordButton) findViewById(R.id.swm_record_button);
        recordPresenter = new RecordPresenter(this, timerView, recordButton);
        recordButton.setOnClickListener(recordPresenter);
        getApplication().registerActivityLifecycleCallbacks(recordPresenter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
        getApplication().unregisterActivityLifecycleCallbacks(recordPresenter);
        getApplication().unregisterActivityLifecycleCallbacks(mainPresenter);
    }
}
