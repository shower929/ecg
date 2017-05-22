package com.swm.app.superrun;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.swm.app.superrun.marathon.MarathonActivity;
import com.swm.app.superrun.training.TrainingModelActivity;
import com.swm.app.superrun.power.RunPowerMeterHandler;
import com.swm.app.superrun.power.SwmMeter;
import com.swm.battery.BatteryListener;
import com.swm.core.BatteryData;
import com.swm.core.CompositeActivity;
import com.swm.core.HeartRateData;
import com.swm.core.SwmBinder;
import com.swm.core.SwmService;
import com.swm.device.SwmDeviceListener;
import com.swm.heart.BuildConfig;
import com.swm.heart.R;
import com.swm.heart.SwmBaseActivity;
import com.swm.heartbeat.HeartBeatHandler;
import com.swm.heartbeat.HeartRateListener;
import com.swm.heartbeat.HeartRateSound;

public class SuperRunActivity extends SwmBaseActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , HeartRateListener
        , SwmDeviceListener
        , View.OnClickListener
        , BatteryListener{

    private static final String LOG_TAG = "SuperRun";

    private SwmBinder mSwmBinder;
    private HeartRateSound mHeartBeatSound;
    private HeartBeatHandler mHeartBeatHandler;
    private Handler mAniHandler;
    private ToneGenerator mConnectedSound;
    private Button mTrainingBtn;
    private ImageView mRipple;
    private RunPowerMeterHandler mMeterHandler;
    private AnimatorSet mBtnBeatingAnim;
    private Runnable mBtnAnimRunnable;
    private TextView mBatteryPercent;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mSwmBinder = (SwmBinder) service;
            Log.i(LOG_TAG, "Connected to heart rate service");
            try {
                mSwmBinder.registerHeartRateListener(SuperRunActivity.this);
                mSwmBinder.registerHeartRateListener(mHeartBeatHandler);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mSwmBinder.setDeviceListener(SuperRunActivity.this);
            mSwmBinder.setBatteryListener(SuperRunActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AudioManager audio = (AudioManager) getSystemService(AUDIO_SERVICE);
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_RING);

        mConnectedSound = new ToneGenerator(AudioManager.STREAM_RING, currentVolume);

        Intent intent = new Intent(this, SwmService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);

        setContentView(R.layout.activity_super_run);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (BuildConfig.ENGINEERING) {
            navigationView.getMenu().findItem(R.id.nav_manage).setVisible(true);
        }

        SwmMeter powerMeter = (SwmMeter) findViewById(R.id.swm_run_power_meter);
        TextView meterValue = (TextView) findViewById(R.id.swm_run_power);
        mMeterHandler = new RunPowerMeterHandler(powerMeter, meterValue);
        mMeterHandler.setMax(100);
        mMeterHandler.setExcellentLevel(90);
        mMeterHandler.setGoodLevel(60);
        mMeterHandler.setPoorLevel(40);
        TextView runPowerValue = (TextView) findViewById(R.id.swm_run_power);
        mTrainingBtn = (Button) findViewById(R.id.swm_cta_training_btn);
        mTrainingBtn.setOnClickListener(this);
        mRipple = (ImageView) findViewById(R.id.swm_ripple);
        ImageView heart = (ImageView) findViewById(R.id.swm_heart);

        mHeartBeatSound = new HeartRateSound(this);
        mHeartBeatHandler = new HeartBeatHandler(heart);

        mBatteryPercent = (TextView) findViewById(R.id.swm_battery_percent);

        initBtnAnim(mTrainingBtn, mRipple);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.super_run, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.swm_marathon) {
            Intent intent = new Intent(this, MarathonActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(this, CompositeActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_setting) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_app_info) {
            Intent intent = new Intent(this, AppInfoActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSwmBinder != null) {
            try {
                mSwmBinder.registerHeartRateListener(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mSwmBinder.setDeviceListener(this);
            mSwmBinder.setBatteryListener(this);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSwmBinder != null) {
            mSwmBinder.removeHeartRateListener(this);
            mSwmBinder.removeDeviceListener();
            mSwmBinder.removeBatteryListener();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
        mConnectedSound.release();

    }

    @Override
    public void onHeartRateDataAvailable(final HeartRateData heartRateData) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMeterHandler.setHeartRate(heartRateData.heartRate);

                mHeartBeatSound.onHeartRateDataAvailable(heartRateData);

                if (mBtnAnimRunnable == null) {
                    mBtnAnimRunnable = new Runnable() {
                        @Override
                        public void run() {
                            mBtnBeatingAnim.start();
                            mAniHandler.postDelayed(this, 3000);
                        }
                    };
                    mAniHandler.post(mBtnAnimRunnable);
                }
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
                    mConnectedSound.startTone(ToneGenerator.TONE_CDMA_INTERCEPT, 100);
                }
            });
        }

        if (state == SwmDeviceListener.DISCONNECTED){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMeterHandler.sendEmptyMessage(RunPowerMeterHandler.MSG_TURNOFF);
                    mConnectedSound.startTone(ToneGenerator.TONE_PROP_PROMPT, 100);
                    Toast.makeText(SuperRunActivity.this, getString(R.string.swm_disconnect_msg), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.swm_cta_training_btn:
                Intent intent = new Intent(this, TrainingModelActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void initBtnAnim(View btn, View ripple) {
        mAniHandler = new Handler();
        Animator btnEnlargeXAnim = ObjectAnimator.ofFloat(btn, "scaleX", 1f, 1.1f);
        Animator btnEnlargeYAnim = ObjectAnimator.ofFloat(btn, "scaleY", 1f, 1.1f);
        Animator btnOriXAnim = ObjectAnimator.ofFloat(btn, "scaleX", 1.1f, 1f);
        Animator btnOriYAnim = ObjectAnimator.ofFloat(btn, "scaleY", 1.1f, 1f);

        AnimatorSet btnEnlargeAnim = new AnimatorSet();
        btnEnlargeAnim.playTogether(btnEnlargeXAnim, btnEnlargeYAnim);
        btnEnlargeAnim.setDuration(100);

        AnimatorSet btnOriAnim = new AnimatorSet();
        btnOriAnim.playTogether(btnOriXAnim, btnOriYAnim);
        btnOriAnim.setDuration(100);

        AnimatorSet btnAnimSet = new AnimatorSet();
        btnAnimSet.playSequentially(btnEnlargeAnim, btnOriAnim);

        Animator rippleEnlargeXAnim = ObjectAnimator.ofFloat(ripple, "scaleX", 1f, 2.2f);
        Animator rippleEnlargeYAnim = ObjectAnimator.ofFloat(ripple, "scaleY", 1f, 2.2f);
        Animator rippleAlphaAnim = ObjectAnimator.ofFloat(ripple, "alpha", 1f, 0f);

        AnimatorSet rippleAnimSet = new AnimatorSet();
        rippleAnimSet.playTogether(rippleEnlargeXAnim, rippleEnlargeYAnim, rippleAlphaAnim);
        rippleAnimSet.setDuration(1300);

        mBtnBeatingAnim = new AnimatorSet();
        mBtnBeatingAnim.playTogether(btnAnimSet, rippleAnimSet);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mHeartBeatSound.prepare();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHeartBeatSound.release();
    }

    @Override
    public void onBatteryDataAvailable(final BatteryData batteryData) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBatteryPercent.setText(String.valueOf(batteryData.percent) + "%");
            }
        });

    }
}
