package com.swm.heartbeat;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.swm.core.HeartRateData;

/**
 * Created by yangzhenyu on 2016/10/12.
 */

public class HeartBeatHandler implements HeartRateListener{
    private Handler mHandler;
    private View mHeart;
    private long mTime;

    private AnimatorSet mHeartBeatAnim;
    private Runnable mBeat;
    private static final float TOTAL = 50f + 50f + 80f;
    private float mEnlargeXFactor = 80f / TOTAL;
    private float mShrinkXFactor = 50f / TOTAL;
    private float mOriXFactor = 50f / TOTAL;
    private TextView mHeartRate;

    public HeartBeatHandler(View heart, TextView heartRate) {
        mHeart = heart;
        mHeartRate = heartRate;
        mHandler = new Handler();
    }
    public HeartBeatHandler(View heart) {
        this(heart, null);
    }

    private void initHeartBeatAnim(long totalDuration) {
        Animator heartEnlargeYAnim = ObjectAnimator.ofFloat(mHeart, "scaleY", 1f, 1.2f);
        Animator heartEnlargeXAnim = ObjectAnimator.ofFloat(mHeart, "scaleX", 1f, 1.2f);
        //Animator heartShrinkYAnim = ObjectAnimator.ofFloat(mHeart, "scaleY", 1.2f, 0.8f);
        Animator heartShrinkXAnim = ObjectAnimator.ofFloat(mHeart, "scaleX", 1.2f, 0.8f);
        Animator heartOriXAnim = ObjectAnimator.ofFloat(mHeart, "scaleX", 0.8f, 1f);
        Animator heartOriYAnim = ObjectAnimator.ofFloat(mHeart, "scaleY", 0.8f, 1f);
        heartEnlargeXAnim.setDuration((long) (totalDuration * mEnlargeXFactor));

        AnimatorSet r = new AnimatorSet();
        r.playTogether(heartShrinkXAnim, heartEnlargeYAnim);
        r.setDuration((long) (totalDuration * mShrinkXFactor));

        AnimatorSet t = new AnimatorSet();
        t.playTogether(heartOriXAnim, heartOriYAnim);
        t.setDuration((long) (totalDuration * mOriXFactor));

        mHeartBeatAnim = new AnimatorSet();
        mHeartBeatAnim.playSequentially(heartEnlargeXAnim,r, t);
    }

    private void onHeartBeat(int heartRate) {
        if (heartRate == 0)
            return;

        long time = 1 * 60 * 1000 / heartRate;
        if (time == mTime) {
            return;
        }

        mTime = time;

        if (mHeartBeatAnim != null && mHeartBeatAnim.isStarted())
            mHeartBeatAnim.cancel();

        initHeartBeatAnim(mTime);

        if (mBeat == null) {
            mBeat  = new Runnable() {
                @Override
                public void run() {
                    mHeartBeatAnim.start();
                    mHandler.postDelayed(this, mTime);
                }
            };
            mHandler.postDelayed(mBeat, mTime);
        }
        if (mHeartRate != null)
            mHeartRate.setText(String.valueOf(heartRate));

    }

    @Override
    public void onHeartRateDataAvailable(final HeartRateData heartRateData) {
        mHeart.post(new Runnable() {
            @Override
            public void run() {
                onHeartBeat(heartRateData.heartRate);
            }
        });
    }
}
