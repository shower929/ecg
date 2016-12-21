package com.swm.app.superrun.training;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.swm.heart.R;

/**
 * Created by yangzhenyu on 2016/10/15.
 */

public class ExpViewController extends Handler {
    private TextView mExpView;
    private TextView mLevelView;

    private AnimatorSet mOnTrainingExpChangeAnim;
    private AnimatorSet mOnPowerLevelChangeAnim;

    public ExpViewController(TextView expView, TextView levelView) {
        mExpView = expView;
        mLevelView = levelView;

        Animator expTranslateX = ObjectAnimator.ofFloat(mExpView, "translationX", -mExpView.getHeight(), 0);
        //Animator expScaleY = ObjectAnimator.ofFloat(mExpView, "scaleY", 1f, 1.1f);
        Animator expShow = ObjectAnimator.ofFloat(mExpView, "alpha", 0f, 1f);
        mOnTrainingExpChangeAnim = new AnimatorSet();
        mOnTrainingExpChangeAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mExpView.setVisibility(View.VISIBLE);
            }
        });
        mOnTrainingExpChangeAnim.setDuration(100);
        mOnTrainingExpChangeAnim.playTogether(expTranslateX, expShow);

        Animator levelTranslateX = ObjectAnimator.ofFloat(mLevelView, "translationX", -mLevelView.getHeight(), 0);
        Animator levelShow = ObjectAnimator.ofFloat(mLevelView, "alpha", 0f, 1f);
        mOnPowerLevelChangeAnim = new AnimatorSet();
        mOnPowerLevelChangeAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mLevelView.setVisibility(View.VISIBLE);
            }
        });
        mOnPowerLevelChangeAnim.setDuration(100);
        mOnPowerLevelChangeAnim.playTogether(levelTranslateX, levelShow);
    }

    public void setTrainingExp(int exp) {
        mExpView.setText(mExpView.getResources().getString(R.string.swm_plus_value, exp));
        mOnTrainingExpChangeAnim.start();
    }

    public void setLevelUp(int power) {
        mLevelView.setText(mLevelView.getResources().getString(R.string.swm_plus_value, power));
        mOnPowerLevelChangeAnim.start();
    }
}
