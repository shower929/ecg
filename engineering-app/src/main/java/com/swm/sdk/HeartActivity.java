package com.swm.sdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.swm.chart.SwmLineChart;
import com.swm.engineering.app.SwmBaseActivity;
import com.swm.stuff.HeartRateSound;
import com.swm.view.HeartRateView;
import com.swm.view.HrvView;
import com.swm.engineering.app.R;
import com.swm.view.RecordButton;
import com.swm.view.TimerView;

public class HeartActivity extends SwmBaseActivity implements View.OnClickListener {
    private static final String LOG_TAG = "ECG";

    private SwmService.SwmBinder mSwmBinder;

    private SwmLineChart ecgBreathView;
    private HeartRateView heartRateView;
    private HrvView hrvView;
    private TimerView timerView;

    private HeartRateSound mHeartBeatSound;
    private Button mRecordBtn;

    private MenuController mMenuController;

    private HeartPresenter presenter;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mSwmBinder = (SwmService.SwmBinder) service;
            Log.i(LOG_TAG, "Connected to SWM service");

            mSwmBinder.getHeartEngine().setRawOutput(presenter);
            mSwmBinder.setServiceListener(presenter);

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

        setContentView(R.layout.activity_heart);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ecgBreathView = (SwmLineChart) findViewById(R.id.swm_ecg_breath_view);
        heartRateView = (HeartRateView) findViewById(R.id.swm_heart_rate);
        hrvView = (HrvView) findViewById(R.id.swm_hrv_view);

        timerView = (TextView) findViewById(R.id.swm_record_elpase);
        presenter = new HeartPresenter(ecgBreathView, heartRateView, hrvView, timerView);
        mHeartBeatSound = new HeartRateSound(this);
        recordButton = (RecordButton) findViewById(R.id.swm_record_button);
        recordButton.setOnClickListener(presenter);
        mMenuController = new MenuController(this, findViewById(R.id.swm_menu));
        findViewById(R.id.swm_menu_icon).setOnClickListener(this);
        findViewById(R.id.swm_menu_ecg).setOnClickListener(this);
        findViewById(R.id.swm_menu_hrv).setOnClickListener(this);
        findViewById(R.id.swm_menu_motion).setOnClickListener(this);
        findViewById(R.id.swm_menu_ble).setOnClickListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mSwmBinder != null)
            mSwmBinder.setServiceListener(presenter);

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
        if(mSwmBinder != null)
            mSwmBinder.setServiceListener(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHeartBeatSound.release();


    }

    @Override
    protected void onStart() {
        super.onStart();
        mHeartBeatSound.prepare();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


        unbindService(mConnection);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.swm_record:
                handleRecordEvent();
                break;
            case R.id.swm_menu_icon:
                mMenuController.onToggle();
                break;
            case R.id.swm_menu_ecg:
            case R.id.swm_menu_hrv:
            case R.id.swm_menu_motion:
            case R.id.swm_menu_ble:
                mMenuController.onSwitchMode(v.getId());
                finish();
                break;
        }
    }

    @Override
    public void onTick(String tock) {
        mElapseView.setText(tock);
    }


}
