package com.swm.engineering;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.swm.sdk.HeartEngine;
import com.swm.sdk.MotionEngine;
import com.swm.stuff.view.RecordButton;
import com.swm.stuff.view.TimerView;
import com.swm.engineering.app.R;

/**
 * Created by yangzhenyu on 2017/4/30.
 */

public class RecordPresenter implements View.OnClickListener
                                        , Application.ActivityLifecycleCallbacks{
    private final Context context;
    private HeartEngine heartEngine;
    private MotionEngine motionEngine;
    private final TimerView timerView;
    private final RecordButton recordButton;
    private volatile boolean logging;

    RecordPresenter(Context context,  TimerView timerView, RecordButton recordButton) {
        this.context = context;
        this.timerView = timerView;
        this.recordButton = recordButton;
    }

    void setModel(HeartEngine heartEngine, MotionEngine motionEngine) {
        this.heartEngine = heartEngine;
        this.motionEngine = motionEngine;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.swm_record_button)
            if(logging) {
                logging = false;
                nolog();
            } else {
                logging = true;
                logging();
            }
    }

    private synchronized void nolog() {
        heartEngine.nolog();
        motionEngine.nolog();
        recordButton.setText(context.getResources().getString(R.string.swm_record));
        recordButton.setBackground(context.getResources().getDrawable(R.drawable.swm_cta_button));
        timerView.setVisibility(View.INVISIBLE);
    }

    private synchronized void logging() {
        heartEngine.log();
        motionEngine.log();
        recordButton.setText(context.getResources().getString(R.string.swm_stop));
        recordButton.setBackground(context.getResources().getDrawable(R.drawable.swm_stop_button));
        timerView.setText("00:00:00");
        timerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (logging) {
            recordButton.setText(activity.getString(R.string.swm_stop));
            recordButton.setBackground(activity.getResources().getDrawable(R.drawable.swm_stop_button));
            timerView.setVisibility(View.VISIBLE);
        } else {
            recordButton.setText(activity.getString(R.string.swm_record));
            recordButton.setBackground(activity.getResources().getDrawable(R.drawable.swm_cta_button));
            timerView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
