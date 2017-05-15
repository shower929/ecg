package com.swm.engineering;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.widget.RadioGroup;

import com.swm.engineering.app.R;

/**
 * Created by yangzhenyu on 2017/5/3.
 */

public class MainPresenter implements RadioGroup.OnCheckedChangeListener
                                        , Application.ActivityLifecycleCallbacks{
    private final Activity myActivity;
    private final HeartPresenter heartPresenter;
    private final MotionPresenter motionPresenter;
    private Presenter currentPresenter;

    public MainPresenter(Activity activity, HeartPresenter heartPresenter, MotionPresenter motionPresenter) {
        myActivity = activity;
        this.heartPresenter = heartPresenter;
        this.motionPresenter = motionPresenter;
        currentPresenter = heartPresenter;
    }


    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        if(checkedId == R.id.swm_ecg_mode_button) {
            heartPresenter.show();
            motionPresenter.hide();
            currentPresenter = heartPresenter;
        } else if (checkedId == R.id.swm_motion_mode_button) {
            heartPresenter.hide();
            motionPresenter.show();
            currentPresenter = motionPresenter;
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        if(activity != myActivity)
            return;

        currentPresenter.onStart();
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        if(activity != myActivity)
            return;

        currentPresenter.onStop();
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
